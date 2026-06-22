package net.codingarea.challenges.platform.message.common;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public final class TranslatableComponents {

  private TranslatableComponents() {
  }

  // org.bukkit.Translatable available since 1.19.3, but we want to provide compatibility with older versions
  public static boolean isBukkitTranslatable(@NotNull Object translatable) {
    try {
      return BukkitTranslationKeyExtractor.isTranslationKeyAvailable(translatable);
    } catch (Throwable ex) {
      return false;
    }
  }

  @NotNull
  public static Component fromBukkitTranslatable(@NotNull Object translatable) {
    // must check isBukkitTranslatable first
    try {
      return Component.translatable(BukkitTranslationKeyExtractor.extractTranslationKey(translatable));
    } catch (Throwable ex) {
      // ignore and fallback to toString silently instead of runtime exception
      return Component.translatable(translatable.toString());
    }
  }

  @NotNull
  public static Component fromMaterial(@NotNull Material material) {
    try {
      return Component.translatable(material.getTranslationKey()); // 1.19.3+
    } catch (NoSuchMethodError ex) {
      // hopefully this is the correct key... use namespace key instead?
      String prefix = material.isItem() ? "block.minecraft." : "item.minecraft.";
      return Component.translatable(prefix + material.name().toLowerCase());
    }
  }

  @NotNull
  public static Component fromEntityType(@NotNull EntityType entityType) {
    try {
      return Component.translatable(entityType.getTranslationKey()); // 1.19.3+
    } catch (NoSuchMethodError ex) {
      // hopefully this is the correct key... use namespace key instead?
      return Component.translatable("entity.minecraft." + entityType.name().toLowerCase());
    }
  }

  @NotNull
  public static Component fromGameMode(@NotNull GameMode gameMode) {
    return Component.translatable("selectWorld.gameMode." + gameMode.name().toLowerCase());
  }

}
