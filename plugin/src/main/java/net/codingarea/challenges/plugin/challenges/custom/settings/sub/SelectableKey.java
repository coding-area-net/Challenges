package net.codingarea.challenges.plugin.challenges.custom.settings.sub;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface SelectableKey {

  @NotNull
  String getKey();

  @NotNull
  ItemBuilder getDisplayItem(@NotNull Locale locale);

  @NotNull
  @Contract(pure = true)
  static SelectableKey.Option of(@NotNull String key, @NotNull ItemStack displayPreset, @NotNull LocalizableMessage name, @Nullable LocalizableMessage description) {
    return new SelectableKey.Option(key, displayPreset, name, description);
  }

  @NotNull
  @Contract(pure = true)
  static SelectableKey.Option of(@NotNull String key, @NotNull Material displayMaterial, @NotNull LocalizableMessage name) {
    return of(key, new ItemStack(displayMaterial), name);
  }

  @NotNull
  @Contract(pure = true)
  static SelectableKey.Option of(@NotNull String key, @NotNull ItemStack displayPreset, @NotNull LocalizableMessage name) {
    return of(key, displayPreset, name, null);
  }

  @NotNull
  @Contract(pure = true)
  static SelectableKey.Option ofMaterial(@NotNull String key, @NotNull Material material) {
    return ofName(key, material, "material.format", material);
  }

  @NotNull
  @Contract(pure = true)
  static SelectableKey.Option ofName(@NotNull String key, @NotNull Material displayMaterial, @NotNull String nameKey, @NotNull Object... nameArgs) {
    String baseKey = "custom.setting." + nameKey;
    return of(key, new ItemStack(displayMaterial), MessageKey.of(baseKey + ".name").withArgs(nameArgs), null);
  }

  @NotNull
  @Contract(pure = true)
  static SelectableKey.Option ofNameDesc(@NotNull String key, @NotNull Material displayMaterial, @NotNull String nameKey, @NotNull Object... nameArgs) {
    String baseKey = "custom.setting." + nameKey;
    return of(key, new ItemStack(displayMaterial), MessageKey.of(baseKey + ".name").withArgs(nameArgs), MessageKey.of(baseKey + ".desc").withArgs(nameArgs));
  }

  @Getter
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  class Option implements SelectableKey {

    private final String key;
    private final ItemStack displayItemPreset;
    private final LocalizableMessage localizableName;
    private final LocalizableMessage localizableDescription;

    @NotNull
    @Override
    public ItemBuilder getDisplayItem(@NotNull Locale locale) {
      return DefaultItems.createChallengeDisplayFormat(displayItemPreset, localizableName, localizableDescription, locale);
    }
  }

}
