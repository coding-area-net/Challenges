package net.codingarea.challenges.plugin.content.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.logging.ConsolePrint;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.common.collection.IOUtils;
import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.misc.FileUtils;
import net.codingarea.commons.common.misc.GsonUtils;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

public final class LanguageLoader extends ContentLoader {

  public static final String FALLBACK_LANGUAGE = "en";

  public static final String
    KEY_LANGUAGE = "language",
    KEY_ONLINE_UPDATE = "language-online-update",
    KEY_LANGUAGE_OVERWRITE = "language-overwrite-path",
    KEY_SMALL_CAPS = "small-caps";

  @Getter
  private static volatile boolean loaded = false;
  @Getter
  private String language;
  @Getter
  private boolean smallCapsFont;
  @Getter
  private boolean onlineUpdate;
  @Getter
  private boolean languageOverwrite;

  @Override
  protected void load() {
    Document config = Challenges.getInstance().getConfigDocument();

    String languageOverwritePath = config.getString(KEY_LANGUAGE_OVERWRITE);
    languageOverwrite = config.contains(KEY_LANGUAGE_OVERWRITE) && languageOverwritePath != null;
    smallCapsFont = config.getBoolean(KEY_SMALL_CAPS, false);
    language = config.getString(KEY_LANGUAGE, FALLBACK_LANGUAGE);
    onlineUpdate = config.getBoolean(KEY_ONLINE_UPDATE, false);

    if (languageOverwrite) {
      loadOverwrite(languageOverwritePath);
      return;
    }

    loadDefault();
  }

  public void changeLanguage(@NotNull String language) {
    if (language.equalsIgnoreCase(this.language)) {
      Logger.info("Language '{}' is already selected", language);
      return;
    }

    Challenges.getInstance().setValueInConfig(KEY_LANGUAGE, language);
    reloadWithLanguage(language);
    Logger.info("Language changed to '{}'", language);
  }

  public void reloadWithLanguage(String language) {
    this.language = language;
    read();
    init();
    Challenges.getInstance().getScoreboardManager().updateAll();
    Challenges.getInstance().getConfigManager().getSettingsConfig().set("language", language);
  }

  private void loadOverwrite(@NotNull String languageOverwritePath) {
    Logger.info("Using direct language file '{}'", languageOverwritePath);
    readLanguage(new File(languageOverwritePath));
  }

  private void loadDefault() {
    migrateLanguages(onlineUpdate);
    init();
    read();
  }

  private void init() {
    language = Challenges.getInstance().getConfigDocument().getString("language", FALLBACK_LANGUAGE);
    File file = getMessageFile(language, "json");

    if (!file.exists()) {
      if (language.equalsIgnoreCase(FALLBACK_LANGUAGE)) return;
      ConsolePrint.unknownLanguage(language);
      language = FALLBACK_LANGUAGE;
    }

    Logger.debug("Language '{}' is currently selected", language);

  }

  private void migrateLanguages(boolean onlineUpdate) {
    // migrate with bundled languages + changed online translation (if enabled)
    for (String language : loadBundledLanguageNames()) {
      try {
        Document bundledDocument = loadBundledLanguageDocument(language);
        if (bundledDocument == null) continue;

        Document migratedDocument = migrateLanguageDocument(bundledDocument, language);
        Document onlineDocument = onlineUpdate ? fetchOnlineLanguageDocument(language) : null;
        migrateLanguageFileAndSave(migratedDocument, bundledDocument, onlineDocument, language);
      } catch (IOException ex) {
        Logger.error("Could not migrate language file for {}", language, ex);
      }
    }

    // pull new languages
    if (onlineUpdate) {
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
    if (onlineLanguage == null || Challenges.getInstance().isDevMode()) { // ignore online update in dev-mode!
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
      return Document.parseStringArray(IOUtils.toString(in));
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
      return Document.parseStringArray(IOUtils.toString(getGitHubUrl("language/languages.json")));
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

  private void read() {
    readLanguage(getMessageFile(language, "json"));
  }

  private void readLanguage(@NotNull File file) {
    try {

      if (!file.exists()) {
        ConsolePrint.unableToGetLanguages();
        return;
      }

      int messages = 0;
      JsonObject read = JsonParser.parseReader(FileUtils.newBufferedReader(file)).getAsJsonObject(); // TODO fix(deps) version ambiguity
      for (Entry<String, JsonElement> entry : read.entrySet()) {
        Message message = Message.forName(entry.getKey());
        JsonElement element = entry.getValue();
        if (element.isJsonPrimitive()) {
          message.setValue(new String[]{element.getAsString()});
          messages++;
        } else if (element.isJsonArray()) {
          message.setValue(GsonUtils.convertJsonArrayToStringArray(element.getAsJsonArray()));
          messages++;
        } else {
          Logger.warn("Illegal type '{}' for {}", element.getClass().getName(), message.getName());
        }
      }

      loaded = true;
      Logger.info("Successfully loaded language '{}' from config file: {} message(s)", language, messages);

    } catch (Exception ex) {
      Logger.error("Could not read languages", ex);
    }
  }

}
