package net.codingarea.commons.bukkit.core;

import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.config.FileDocument;
import net.codingarea.commons.common.config.document.GsonDocument;
import net.codingarea.commons.common.config.document.PropertiesDocument;
import net.codingarea.commons.common.config.document.YamlDocument;
import net.codingarea.commons.common.misc.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SimpleConfigManager {

  protected final Map<String, FileDocument> configs = new HashMap<>();
  protected final BukkitModule module;

  public SimpleConfigManager(@NotNull BukkitModule module) {
    this.module = module;
  }

  @NotNull
  public FileDocument getDocument(@NotNull String filename) {
    if (!filename.contains(".")) filename += ".json";
    return getDocument(module.getDataFile(filename));
  }

  public synchronized FileDocument getDocument(@NotNull File file) {
    String extension = FileUtils.getFileExtension(file);
    return configs.computeIfAbsent(file.getName(), key -> FileDocument.readFile(resolveType(extension), file));
  }

  @NotNull
  public static Class<? extends Document> resolveType(@NotNull String extension) {
    switch (extension.toLowerCase()) {
      case "json":
        return GsonDocument.class;
      case "yml":
      case "yaml":
        return YamlDocument.class;
      case "properties":
        return PropertiesDocument.class;
      default:
        throw new IllegalArgumentException("Unknown document file extension '" + extension + "'");
    }
  }

}
