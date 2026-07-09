package net.codingarea.challenges.plugin.utils.item;

import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder.SkullBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@Deprecated
public final class DefaultItem {

  public static final class SkullTextures {
    public static final String
      ARROW_LEFT = "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1YWQwOTgxNmY1Yjc1ZTcyMzUwNzFlODdiYmZmOGYxYmVlMjZmMDVkNDQwMWFjNGRmOTI1YTA3MDE5In19fQ==",
      ARROW_RIGHT = "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==",
      GREEN_ARROW_UP = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWRhMDI3NDc3MTk3YzZmZDdhZDMzMDE0NTQ2ZGUzOTJiNGE1MWM2MzRlYTY4YzhiN2JjYzAxMzFjODNlM2YifX19",
      RED_ARROW_DOWN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4NTJiZjYxNmYzMWVkNjdjMzdkZTRiMGJhYTJjNWY4ZDhmY2E4MmU3MmRiY2FmY2JhNjY5NTZhODFjNCJ9fX0=";

    private SkullTextures() {
    }
  }

  @NotNull
  public static String getItemPrefix() {
    return Message.forName("item-prefix").asString() + "§e";
  }

  @NotNull
  public static LegacyItemBuilder navigateBack() {
    return new SkullBuilder().setBase64Texture(SkullTextures.ARROW_LEFT).setName(Message.forName("navigate-back")).hideAttributes();
  }

  @NotNull
  public static LegacyItemBuilder navigateNext() {
    return new SkullBuilder().setBase64Texture(SkullTextures.ARROW_RIGHT).setName(Message.forName("navigate-next")).hideAttributes();
  }

  @NotNull
  public static LegacyItemBuilder navigateBackMainMenu() {
    return new LegacyItemBuilder(Material.DARK_OAK_DOOR).setName(Message.forName("navigate-back")).hideAttributes();
  }

  @NotNull
  public static LegacyItemBuilder status(boolean enabled) {
    return enabled ? enabled() : disabled();
  }

  @NotNull
  public static LegacyItemBuilder enabled() {
    return new LegacyItemBuilder(Material.LIME_DYE).setName(getTitle(Message.forName("enabled"))).hideAttributes();
  }

  @NotNull
  public static LegacyItemBuilder disabled() {
    return new LegacyItemBuilder(MinecraftNameWrapper.RED_DYE).setName(getTitle(Message.forName("disabled"))).hideAttributes();
  }

  @NotNull
  public static LegacyItemBuilder customize() {
    return new LegacyItemBuilder(MinecraftNameWrapper.SIGN).setName(getTitle(Message.forName("customize"))).hideAttributes();
  }

  @NotNull
  public static LegacyItemBuilder value(int value) {
    return value(value, "§e");
  }

  @NotNull
  public static LegacyItemBuilder value(int value, @NotNull String prefix) {
    return create(Material.STONE_BUTTON, prefix + value).setAmount(Math.max(value, 1));
  }

  @NotNull
  public static LegacyItemBuilder create(@NotNull Material material, @NotNull String name) {
    return new LegacyItemBuilder(material, getTitle(name));
  }

  @NotNull
  public static LegacyItemBuilder create(@NotNull Material material, @NotNull Message message) {
    LegacyItemBuilder itemBuilder = new LegacyItemBuilder(material, message);
    return itemBuilder.setName(getTitle(message));
  }

  @NotNull
  private static String getTitle(@NotNull Message message) {
    return getTitle(message.asString());
  }

  @NotNull
  private static String getTitle(@NotNull String text) {
    return Message.forName("item-setting-info").asString(text);
  }

}
