package net.codingarea.challenges.plugin.utils.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultItems {

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class SkullTextures {
    public static final String
      ARROW_LEFT = "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1YWQwOTgxNmY1Yjc1ZTcyMzUwNzFlODdiYmZmOGYxYmVlMjZmMDVkNDQwMWFjNGRmOTI1YTA3MDE5In19fQ==",
      ARROW_RIGHT = "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==",
      GREEN_ARROW_UP = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWRhMDI3NDc3MTk3YzZmZDdhZDMzMDE0NTQ2ZGUzOTJiNGE1MWM2MzRlYTY4YzhiN2JjYzAxMzFjODNlM2YifX19",
      RED_ARROW_DOWN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4NTJiZjYxNmYzMWVkNjdjMzdkZTRiMGJhYTJjNWY4ZDhmY2E4MmU3MmRiY2FmY2JhNjY5NTZhODFjNCJ9fX0=";
  }

  public static ItemBuilder createNavigateNext(@NotNull Locale locale) {
    return new ItemBuilder.SkullBuilder(locale, MessageKey.of("navigate-next")).setBase64Texture(DefaultItem.SkullTextures.ARROW_RIGHT);
  }

  public static ItemBuilder createNavigateBack(@NotNull Locale locale) {
    return new ItemBuilder.SkullBuilder(locale, MessageKey.of("navigate-back")).setBase64Texture(SkullTextures.ARROW_LEFT);
  }

  public static ItemBuilder createNavigateBackMainMenu(@NotNull Locale locale) {
    return new ItemBuilder(locale, Material.DARK_OAK_DOOR, MessageKey.of("navigate-back"));
  }

  public static ItemStack createEnabledPreset() {
    return new ItemStack(Material.LIME_DYE);
  }

  public static ItemStack createEnabledValuePreset(int amount) {
    return new ItemStack(Material.LIME_DYE, amount);
  }

  public static ItemStack createDisabledPreset() {
    return new ItemStack(MinecraftNameWrapper.RED_DYE);
  }

  public static ItemStack createValuePreset(int value) {
    return new ItemStack(Material.STONE_BUTTON, Math.max(value, 1));
  }

  public static ItemStack createCustomizePreset() {
    return new ItemStack(MinecraftNameWrapper.SIGN);
  }

}
