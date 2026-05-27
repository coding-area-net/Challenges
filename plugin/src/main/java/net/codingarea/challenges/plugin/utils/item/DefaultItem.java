package net.codingarea.challenges.plugin.utils.item;

import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder.SkullBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class DefaultItem {

  private static final String ARROW_LEFT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjAwNmVjMWVjYTJmMjY4NWY3MGU2NTQxMWNmZTg4MDhhMDg4ZjdjZjA4MDg3YWQ4ZWVjZTk2MTgzNjEwNzBlMyJ9fX0=",
    ARROW_RIGHT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY5ZTE5ZTVmMmNlMzQ4OGMyOTU4MmI2ZDI2MDE1MDA2MjZlOGRiMmE4OGNkMTgxNjQ0MzJmZWYyZTM0ZGU2YiJ9fX0=";

  @NotNull
  public static String getItemPrefix() {
    return Message.forName("item-prefix").asString() + "§e";
  }

  @NotNull
  public static ItemBuilder navigateBack() {
    return new SkullBuilder().setBase64Texture(ARROW_LEFT).setName(Message.forName("navigate-back")).hideAttributes();
  }

  @NotNull
  public static ItemBuilder navigateNext() {
    return new SkullBuilder().setBase64Texture(ARROW_RIGHT).setName(Message.forName("navigate-next")).hideAttributes();
  }

  @NotNull
  public static ItemBuilder navigateBackMainMenu() {
    return new ItemBuilder(Material.DARK_OAK_DOOR).setName(Message.forName("navigate-back")).hideAttributes();
  }

  @NotNull
  public static ItemBuilder status(boolean enabled) {
    return enabled ? enabled() : disabled();
  }

  @NotNull
  public static ItemBuilder enabled() {
    return new ItemBuilder(Material.LIME_DYE).setName(getTitle(Message.forName("enabled"))).hideAttributes();
  }

  @NotNull
  public static ItemBuilder disabled() {
    return new ItemBuilder(MinecraftNameWrapper.RED_DYE).setName(getTitle(Message.forName("disabled"))).hideAttributes();
  }

  @NotNull
  public static ItemBuilder customize() {
    return new ItemBuilder(MinecraftNameWrapper.SIGN).setName(getTitle(Message.forName("customize"))).hideAttributes();
  }

  @NotNull
  public static ItemBuilder value(int value) {
    return value(value, "§e");
  }

  @NotNull
  public static ItemBuilder value(int value, @NotNull String prefix) {
    return create(Material.STONE_BUTTON, prefix + value).amount(Math.max(value, 1));
  }

  @NotNull
  public static ItemBuilder create(@NotNull Material material, @NotNull String name) {
    return new ItemBuilder(material, getTitle(name));
  }

  @NotNull
  public static ItemBuilder create(@NotNull Material material, @NotNull Message message) {
    ItemBuilder itemBuilder = new ItemBuilder(material, message);
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
