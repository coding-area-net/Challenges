package net.codingarea.challenges.plugin.challenges.custom.settings.sub;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * @see Option
 * @see ValueSetting
 */
public interface SelectableKey {

  @NotNull
  String getKey();

  @NotNull
  ItemBuilder getDisplayItem(@NotNull Locale locale);

  // TODO rename factories to represent args

  @Contract(pure = true)
  static SelectableKey.Option of(@NotNull String key, @NotNull Material displayMaterial, @NotNull LocalizableMessage name) {
    return new SelectableKey.Option(key, new ItemStack(displayMaterial), name);
  }

  @Contract(pure = true)
  static SelectableKey.Option of(@NotNull String key, @NotNull Material displayMaterial, @NotNull Translatable name) {
    return new SelectableKey.Option(key, new ItemStack(displayMaterial), name);
  }

  @Contract(pure = true)
  static SelectableKey.Option of(@NotNull String key, @NotNull Material displayMaterial, @NotNull Component name) {
    return new SelectableKey.Option(key, new ItemStack(displayMaterial), name);
  }

  @Contract(pure = true)
  static SelectableKey.Option of(@NotNull String key, @NotNull Material displayMaterial, @NotNull String nameKey, @NotNull Object... nameArgs) {
    return of(key, displayMaterial, LocalizableMessage.of(nameKey, nameArgs));
  }

  @Contract(pure = true)
  static SelectableKey.Option of(@NotNull String key, @NotNull ItemStack displayPreset, @NotNull LocalizableMessage name) {
    return new SelectableKey.Option(key, displayPreset, name);
  }

  @Contract(pure = true)
  static SelectableKey.Option of(@NotNull String key, @NotNull ItemStack displayPreset, @NotNull Translatable name) {
    return new SelectableKey.Option(key, displayPreset, name);
  }

  @Contract(pure = true)
  static SelectableKey.Option of(@NotNull String key, @NotNull ItemStack displayPreset, @NotNull Component name) {
    return new SelectableKey.Option(key, displayPreset, name);
  }

  @Contract(pure = true)
  static SelectableKey.Option of(@NotNull String key, @NotNull ItemStack displayPreset, @NotNull String nameKey, @NotNull Object... nameArgs) {
    return of(key, displayPreset, LocalizableMessage.of(nameKey, nameArgs));
  }

  @Getter
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  class Option implements SelectableKey {

    // TODO DESCRIPTION?

    private final String key;
    private final ItemStack displayItemPreset;
    private final Object localizableNameArg;

    @NotNull
    @Override
    public ItemBuilder getDisplayItem(@NotNull Locale locale) {
      return DefaultItems.createMenuDisplayFormat(displayItemPreset, localizableNameArg, locale);
    }
  }

}
