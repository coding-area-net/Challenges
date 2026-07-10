package net.codingarea.challenges.plugin.content.i18n.impl.format;

import net.codingarea.challenges.plugin.content.i18n.MessageHolder;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ComponentArguments {

  private ComponentArguments() {
  }

  @NotNull
  public static Component convertToComponent(@Nullable Object obj) {
    return switch (obj) {
      case null -> Component.empty();
      case Component component -> component;
      case ComponentLike like -> like.asComponent();
      case MessageHolder holder -> convertMessageHolderToComponent(holder);
      case Material material -> getDetailedMaterialWithSprite(material);
      case Translatable translatable -> // net.kyori.adventure.translation.Translatable
        Component.translatable(translatable);
      case Player player -> player.displayName();
      case String string -> // ! user input must be escaped via Component.text(...)
        ComponentFormatter.deserializeText(string);
      case Enum<?> enumObj -> Component.text(stringifyEnumName(enumObj.name()));
      default -> Component.text(obj.toString());
    };
  }

  @NotNull
  public static Component convertMessageHolderToComponent(@NotNull MessageHolder holder) {
    return ComponentFormatter.deserializeLinesWithArgs(null, holder.rawValue(), holder.positionalArgs());
  }

  @NotNull
  public static Component getDetailedMaterialWithSprite(@NotNull Material material) {
    return Component.text().append(getItemSpriteOrEmpty(material)).append(getDetailedMaterialTranslatable(material)).asComponent();
  }

  @NotNull
  public static Component getItemSpriteOrEmpty(@NotNull ItemStack item) {
    try {
      return formatSpriteOrEmpty(ItemSprites.sprite(item));
    } catch (Throwable _) {
      // not yet available in this version
      return Component.empty();
    }
  }

  @NotNull
  public static Component getItemSpriteOrEmpty(@NotNull Material item) {
    try {
      return formatSpriteOrEmpty(ItemSprites.sprite(item));
    } catch (Throwable _) {
      // not yet available in this version
      return Component.empty();
    }
  }

  @NotNull
  private static Component formatSpriteOrEmpty(@NotNull Component sprite) throws Error {
    if (sprite.equals(Component.empty())) return sprite; // don't append space
    return sprite.color(NamedTextColor.WHITE).appendSpace();
  }

  @NotNull
  public static Component getDetailedMaterialTranslatable(@NotNull Material material) {
    String key = material.translationKey(); // e.g., "item.minecraft.music_disc_strad"
    Component baseComponent = Component.translatable(key);
    String name = material.name();

    if (name.startsWith("MUSIC_DISC_")) {
      // 1.21+ changed music disc details description: *.desc -> jukebox_song.minecraft.*

      String songTranslationKey;
      if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_21)) {
        songTranslationKey = key.replace("item.minecraft.music_disc_", "jukebox_song.minecraft.");
      } else {
        songTranslationKey = key + ".desc";
      }

      return appendDetailsTranslatable(baseComponent, songTranslationKey);
    }

    if (name.endsWith("_BANNER_PATTERN")) {
      // 1.20.5+ added pattern name into item name; before *.desc
      if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_20_5)) return baseComponent;

      String patternTranslationKey = key + ".desc";
      return appendDetailsTranslatable(baseComponent, patternTranslationKey);
    }

    if (name.endsWith("_SMITHING_TEMPLATE")) {
      // 1.20.5+ added smithing template name into item name
      if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_20_5)) return baseComponent;

      String descTranslationKey;
      if (name.equals("NETHERITE_UPGRADE_SMITHING_TEMPLATE")) {
        descTranslationKey = "upgrade.minecraft.netherite_upgrade";
      } else {
        // Dynamically extract the trim type (e.g., "ward" from "WARD_ARMOR_TRIM_SMITHING_TEMPLATE")
        String trimType = name.replace("_ARMOR_TRIM_SMITHING_TEMPLATE", "").toLowerCase();
        descTranslationKey = "trim_pattern.minecraft." + trimType;
      }

      return appendDetailsTranslatable(baseComponent, descTranslationKey);
    }

    // Default behavior for all other non-ambiguous items
    return baseComponent;
  }

  @NotNull
  private static Component appendDetailsTranslatable(@NotNull Component baseComponent, @NotNull String translationKey) {
    return baseComponent
      .append(Component.text(" (")) // Component#appendSpace not available pre 1.19.3
      .append(Component.translatable(translationKey))
      .append(Component.text(")"));
  }

  @NotNull
  public static String stringifyEnumName(@NotNull String enumName) {
    // EXAMPLE_ENUM -> "Example Enum"
    int len = enumName.length();
    if (len == 0) return "";

    char[] result = new char[len];
    boolean nextUpperCase = true;
    for (int i = 0; i < len; i++) {
      char letter = enumName.charAt(i);
      if (letter == '_') {
        result[i] = ' ';
        nextUpperCase = true;
      } else {
        result[i] = nextUpperCase ? Character.toUpperCase(letter) : Character.toLowerCase(letter);
        nextUpperCase = false;
      }
    }
    return new String(result);
  }
}
