package net.codingarea.challenges.platform.message.common;

import org.bukkit.Translatable;
import org.jetbrains.annotations.NotNull;

// org.bukitt.Translatable available since 1.19.3
// LinkageError due to Translatable not existing -> the whole class may not be loadable
// therefore we encapsulate it which is preferred compared to expesive reflection calls to Translatable
final class BukkitTranslationKeyExtractor {

  private BukkitTranslationKeyExtractor() {
  }

  static boolean isTranslationKeyAvailable(@NotNull Object obj) throws LinkageError {
    return obj instanceof Translatable;
  }

  static String extractTranslationKey(@NotNull Object obj) throws LinkageError {
    if (obj instanceof Translatable translatable)
      return translatable.getTranslationKey();
    throw new IllegalArgumentException("Object " + obj.getClass() + " is not a org.bukkit.Translatable");
  }

}
