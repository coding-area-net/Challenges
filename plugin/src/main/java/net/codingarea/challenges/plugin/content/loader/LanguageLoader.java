package net.codingarea.challenges.plugin.content.loader;

import lombok.Getter;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.LanguageProvider;
import net.codingarea.challenges.plugin.content.i18n.TranslationManager;
import net.codingarea.challenges.plugin.management.files.ConfigManager;
import net.codingarea.challenges.plugin.utils.logging.ConsolePrint;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.common.collection.IOUtils;
import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.misc.FileUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public final class LanguageLoader extends ContentLoader {

  public static final String FALLBACK_LANGUAGE = TranslationManager.FALLBACK_LOCALE.getLanguage();

  public static final String GLOBAL_TAG = "global";

  @Getter
  private static volatile boolean loaded = false;
  @Getter
  private String configLanguageTag; // default, will be ignored if custom LanguageProvider is set in TranslationManager
  @Getter
  private Locale configLanguage; // default, will be ignored if custom LanguageProvider is set in TranslationManager
  @Getter
  @Deprecated
  private final boolean smallCapsFont = false;
  @Getter
  private boolean onlineUpdateEnabled;
  @Getter
  private Set<String> availableLanguageTags;
  @Getter
  private boolean skipMigration;

  @Override
  protected void load() {
    Document config = Challenges.getInstance().getConfigDocument();

    onlineUpdateEnabled = config.getBoolean(ConfigManager.Keys.ONLINE_UPDATE, false);
    configLanguageTag = config.getString(ConfigManager.Keys.LANGUAGE, FALLBACK_LANGUAGE);
    skipMigration = config.getBoolean(ConfigManager.Keys.SKIP_LANGUAGE_MIGRATION, false);
    configLanguage = Locale.forLanguageTag(configLanguageTag);
    availableLanguageTags = new HashSet<>();

    // extracted the language provider so it can easily be overridden
    Challenges.getInstance().getTranslationManager().setLanguageProvider(new DefaultLanguageProvider());

    loadDefault();
  }

  private void loadDefault() {
    if (!skipMigration) migrateLanguages();
    discoverAvailableLanguageTagFiles();
    checkLanguageFileExists();
    readSelectedLanguage();
  }

  public void changeLanguage(@NotNull String language) {
    if (language.equalsIgnoreCase(this.configLanguageTag)) {
      Logger.info("Language '{}' is already selected", language);
      return;
    }

    Challenges.getInstance().setValueInConfig(ConfigManager.Keys.LANGUAGE, language);
    reloadWithLanguage(language);
    Logger.info("Language changed to '{}'", language);
  }

  private void reloadWithLanguage(String language) {
    this.configLanguageTag = language;
    checkLanguageFileExists(); // do the check first, might change languageTag to fallback
    this.configLanguage = Locale.forLanguageTag(configLanguageTag);
    readSelectedLanguage(); // TODO async
    Challenges.getInstance().getScoreboardManager().updateAll();
  }

  private void checkLanguageFileExists() {
    File file = getLanguageFile(configLanguageTag);

    if (!file.exists()) {
      if (configLanguageTag.equalsIgnoreCase(FALLBACK_LANGUAGE)) return;
      ConsolePrint.unknownLanguage(configLanguageTag);
      configLanguageTag = FALLBACK_LANGUAGE;
    }

    Logger.debug("Language '{}' is currently selected", configLanguageTag);
  }

  private void readSelectedLanguage() {
    populateLanguageFromFile(configLanguage);
  }

  private void discoverAvailableLanguageTagFiles() {
    // only .json files supported; global is not a selectable language
    File[] files = getMessagesFolder()
      .listFiles((_, name) -> name.endsWith(".json") && !name.startsWith(GLOBAL_TAG));
    if (files == null) return;
    for (File file : files) {
      if (!file.isFile()) continue;
      availableLanguageTags.add(FileUtils.getFileName(file));
    }
  }

  private void migrateLanguages() {
    // migrate using bundled languages + changed online translations (if enabled)
    for (String language : loadBundledLanguageNames()) {
      try {
        Document bundledDocument = loadBundledLanguageDocument(language);
        if (bundledDocument == null) continue;

        if (Challenges.getInstance().isDevMode()) { // overwrite all in dev mode
          bundledDocument.saveToFile(getLanguageFile(language));
          continue;
        }

        Document migratedDocument = migrateLanguageDocument(bundledDocument, language);
        Document onlineDocument = onlineUpdateEnabled ? fetchOnlineLanguageDocument(language) : null;
        migrateLanguageFileAndSave(migratedDocument, bundledDocument, onlineDocument, language);
      } catch (IOException ex) {
        Logger.error("Could not migrate language file for {}", language, ex);
      }
    }

    // pull new languages / updates
    if (onlineUpdateEnabled) {
      for (String language : fetchOnlineLanguages()) {
        File file = getLanguageFile(language);
        if (file.exists()) continue; // already exists, was migrated in previous step

        try {
          Document onlineDocument = fetchOnlineLanguageDocument(language);
          if (onlineDocument == null) continue;
          onlineDocument.saveToFile(file);
          Logger.info("Added new language '{}' from online update", language);
        } catch (IOException ex) {
          Logger.error("Could not save new language file for {}", language, ex);
        }
      }
    }
  }

  @NotNull
  @CheckReturnValue
  private Document migrateLanguageDocument(@NotNull Document bundledLanguage, @NotNull String languageName) {
    File destinationFile = getLanguageFile(languageName);
    if (!destinationFile.exists()) {
      return bundledLanguage;
    }

    // if the language file already exists, don't overwrite customized messages, only add missing ones
    Document existing = Document.readJsonFile(destinationFile);
    bundledLanguage.forEach((key, value) -> {
      if (!existing.contains(key)) {
        Logger.debug("Overwriting message {} in {} from bundled with {}", key, languageName, String.valueOf(value).replace("\"", "§r\""));
        existing.set(key, value);
      }
    });
    return existing;
  }

  private void migrateLanguageFileAndSave(@NotNull Document migratedLanguage, @NotNull Document bundledLanguage,
                                          @Nullable Document onlineLanguage, @NotNull String languageName) throws IOException {
    File destinationFile = getLanguageFile(languageName);
    if (onlineLanguage == null) {
      migratedLanguage.saveToFile(destinationFile);
      return;
    }

    onlineLanguage.forEach((key, value) -> {
      if (!migratedLanguage.contains(key)) {
        Logger.debug("Overwriting message {} in {} from online-update with {}", key, languageName, String.valueOf(value).replace("\"", "§r\""));
        migratedLanguage.set(key, value);
      } else if (Objects.equals(migratedLanguage.getString(key), bundledLanguage.getString(key))) {
        // if the message was not customized, overwrite it with the online version (like typo fix)
        Logger.debug("Overwriting message {} in {} from online-update with {}", key, languageName, String.valueOf(value).replace("\"", "§r\""));
        migratedLanguage.set(key, value);
      }
    });
    migratedLanguage.saveToFile(destinationFile);
  }

  @NotNull
  private List<String> loadBundledLanguageNames() {
    try (InputStream in = getLanguageResourceStream("languages.json")) {
      return Document.parseJsonStringArray(IOUtils.toString(in));
    } catch (Exception ex) {
      Logger.error("Could not load bundled languages", ex);
      return Collections.emptyList();
    }
  }

  @Nullable
  private Document loadBundledLanguageDocument(@NotNull String languageName) {
    try (InputStream in = getLanguageResourceStream("files/" + languageName + ".json")) {
      if (in == null) return null;
      return Document.parseJson(in);
    } catch (Exception ex) {
      Logger.error("Could not load bundled language '{}' from jar", languageName, ex);
      return null;
    }
  }

  private InputStream getLanguageResourceStream(String path) {
    // no leading slash!
    return Challenges.getInstance().getResource("language/" + path);
  }

  private File getLanguageFile(String languageName) {
    return getMessageFile(languageName, "json");
  }

  @NotNull
  private List<String> fetchOnlineLanguages() {
    try {
      return Document.parseJsonStringArray(IOUtils.toString(getGitHubUrl("language/languages.json")));
    } catch (Exception ex) {
      Logger.error("Could not fetch online languages", ex);
      return Collections.emptyList();
    }
  }

  @Nullable
  private Document fetchOnlineLanguageDocument(@NotNull String languageName) {
    String url = getGitHubUrl("language/files/" + languageName + ".json");
    try {
      return Document.parseJson(IOUtils.toString(url));
    } catch (Exception ex) {
      Logger.error("Could not fetch online language '{}'", languageName, ex);
      return null;
    }
  }

  public void populateLanguageFromFile(@NotNull Locale locale) {
    populateLanguageFromFile(getLanguageFile(locale.getLanguage()), getLanguageFile(GLOBAL_TAG), locale);
  }

  private void populateLanguageFromFile(@NotNull File languageFile, @NotNull File globalFile, @NotNull Locale locale) {
    try {
      if (!languageFile.exists() || !globalFile.exists()) {
        ConsolePrint.unableToGetLanguages();
        return;
      }

      Document globalDocument = Document.readJsonFile(globalFile);
      Document languageDocument = Document.readJsonFile(languageFile);
      int messageCount = Challenges.getInstance().getTranslationManager().populateLanguageFromDocument(locale, languageDocument, globalDocument);

      loaded = true;
      Logger.info("Successfully loaded language '{}' from config file: {} message(s)", locale, messageCount);
    } catch (Exception ex) {
      Logger.error("Could not read languages", ex);
    }
  }

  public class DefaultLanguageProvider implements LanguageProvider {

    @NotNull
    @Override
    public Locale getPlayerLanguage(@NotNull Player player) {
      return configLanguage;
    }

    @Override
    public boolean isDefaultBehaviour() {
      return true;
    }

    @Override
    public boolean isUserSpecific() {
      return false;
    }
  }

}
