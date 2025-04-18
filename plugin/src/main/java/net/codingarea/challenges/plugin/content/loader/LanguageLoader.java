package net.codingarea.challenges.plugin.content.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.collection.IOUtils;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.config.FileDocument;
import net.anweisen.utilities.common.misc.FileUtils;
import net.anweisen.utilities.common.misc.GsonUtils;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.logging.ConsolePrint;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

public final class LanguageLoader extends ContentLoader {

  public static final String DEFAULT_LANGUAGE = "en";

  public static final String KEY_LANGUAGE = "language", KEY_SMALL_CAPS = "smallcaps", KEY_DIRECT_LANGUAGE_FILE = "manualfile";

  @Getter
  private static volatile boolean loaded = false;
  @Getter
  private String language;
  @Getter
  private boolean smallCapsFont;

  public File getLanguagePropertiesFile() {
    return getMessageFile("language", "properties");
  }

  public Document getLanguageProperties() {
    try {
      FileUtils.createFilesIfNecessary(getLanguagePropertiesFile());

      FileDocument document = FileDocument.readPropertiesFile(getLanguagePropertiesFile());
    } catch (Exception e) {
      Logger.error("Could not read language properties", e);
    }
    return Document.empty();
  }

  public void migrateLanguageConfig() {
    Document configDocument = Challenges.getInstance().getConfigDocument();
    String oldLanguage = configDocument.getString("language");
    String oldDirectFilePath = configDocument.getString("direct-language-file");
    boolean oldSmallCapsFont = configDocument.getBoolean("small-caps");

    Document languageProperties = getLanguageProperties();
    languageProperties.set(KEY_LANGUAGE, oldLanguage);
    languageProperties.set(KEY_SMALL_CAPS, oldSmallCapsFont);

    if (configDocument.contains("direct-language-file")) {
      languageProperties.set(KEY_DIRECT_LANGUAGE_FILE, oldDirectFilePath);
    }
  }

  @Override
  protected void load() {
    migrateLanguageConfig();

    Document config = getLanguageProperties();

    smallCapsFont = config.getBoolean(KEY_SMALL_CAPS, false);

    if (config.contains(KEY_DIRECT_LANGUAGE_FILE)) {
      language = config.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE);
      String path = config.getString(KEY_DIRECT_LANGUAGE_FILE);
      if (path == null) return;
      Logger.info("Using direct language file '{}'", path);
      readLanguage(new File(path));
      return;
    }

    loadDefault();
  }

  public void changeLanguage(@Nonnull String language) {
    if (language.equalsIgnoreCase(this.language)) {
      Logger.info("Language '{}' is already selected", language);
      return;
    }

    this.language = language;
    Document properties = getLanguageProperties();
    properties.set(KEY_LANGUAGE, language);
    try {
      properties.saveToFile(getLanguagePropertiesFile());
    } catch (IOException e) {
      Logger.error("Could not save language properties", e);
    }
    reload(language);
    Logger.info("Language changed to '{}'", language);
  }

  public void reload(String language) {
    this.language = language;
    read();
    download();
    init();
    Challenges.getInstance().getScoreboardManager().updateAll();
    Challenges.getInstance().getConfigManager().getSettingsConfig().set("language", language);
  }

  private void loadDefault() {
    download();
    init();
    read();
  }

  private void init() {

    language = Challenges.getInstance().getConfigDocument().getString("language", DEFAULT_LANGUAGE);
    File file = getMessageFile(language, "json");

    if (!file.exists()) {
      if (language.equalsIgnoreCase(DEFAULT_LANGUAGE)) return;
      ConsolePrint.unknownLanguage(language);
      language = DEFAULT_LANGUAGE;
    }

    Logger.debug("Language '{}' is currently selected", language);

  }

  private void download() {
    try {

      JsonArray languages = JsonParser.parseString(IOUtils.toString(getGitHubUrl("language/languages.json"))).getAsJsonArray();
      Logger.debug("Fetched languages {}", languages);
      for (JsonElement element : languages) {
        try {
          String name = element.getAsString();
          String url = getGitHubUrl("language/files/" + name + ".json");

          Document language = Document.parseJson(IOUtils.toString(url));
          File file = getMessageFile(name, "json");

          Logger.debug("Writing language {} to {}", name, file);
          verifyLanguage(language, file, name);
        } catch (Exception exception) {
          Challenges.getInstance().getLogger().error("", exception);
          Logger.error("Could not download language for {}. {}: {}", element, exception.getClass().getSimpleName(), exception.getMessage());
        }
      }

    } catch (Exception ex) {
      Logger.error("Could not download languages", ex);
    }
  }

  private void verifyLanguage(@Nonnull Document download, @Nonnull File file, @Nonnull String name) throws IOException {
    Document existing = Document.readJsonFile(file);
    FileUtils.createFilesIfNecessary(file);
    download.forEach((key, value) -> {
      if (!existing.contains(key)) {
        Logger.debug("Overwriting message {} in {} with {}", key, name, String.valueOf(value).replace("\"", "§r\""));
        existing.set(key, value);
      }
    });
    existing.saveToFile(file);
  }

  private void read() {
    readLanguage(getMessageFile(language, "json"));
  }

  private void readLanguage(@Nonnull File file) {
    try {

      if (!file.exists()) {
        ConsolePrint.unableToGetLanguages();
        return;
      }

      int messages = 0;
      JsonObject read = JsonParser.parseReader(FileUtils.newBufferedReader(file)).getAsJsonObject();
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
