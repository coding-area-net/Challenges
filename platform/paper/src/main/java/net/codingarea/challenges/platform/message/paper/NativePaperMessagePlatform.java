package net.codingarea.challenges.platform.message.paper;

import net.codingarea.challenges.platform.message.common.AbstractMiniMessagePlatform;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Allows for native calls to paper/adventure api bundled by modern paper-like servers,
 * removing the need for slower reflection wrappers with just a small overhead.
 */
public class NativePaperMessagePlatform extends AbstractMiniMessagePlatform {

  public NativePaperMessagePlatform() {
    super(MiniMessage.miniMessage(), LegacyComponentSerializer.legacySection());
  }

  @Override
  public void enable() {
    // no initialization required
  }

  @Override
  public void disable() {
    // no cleanup required
  }

  @Override
  public void sendSenderComponent(@NotNull CommandSender sender, @NotNull Component component) {
    sender.sendMessage(component);
  }

  @Override
  public void sendChatComponent(@NotNull Player player, @NotNull Component component) {
    player.sendMessage(component);
  }

  @Override
  public void sendActionBarComponent(@NotNull Player player, @NotNull Component component) {
    player.sendActionBar(component);
  }

  @Override
  public void sendAdventureTitle(@NotNull Player player, @NotNull Title title) {
    player.showTitle(title);
  }

  @NotNull
  @Override
  public Inventory createInventoryTitled(@NotNull InventoryHolder owner, int size, @NotNull Component titleComponent) {
    return Bukkit.createInventory(owner, size, titleComponent);
  }

  @NotNull
  @Override
  public Inventory createInventoryTitled(@NotNull InventoryHolder owner, @NotNull InventoryType type,
                                         @NotNull Component titleComponent) {
    return Bukkit.createInventory(owner, type, titleComponent);
  }

  @NotNull
  @Override
  public Component getPlayerNameComponent(@NotNull Player player) {
    return player.displayName();
  }

  @Override
  public void applyItemNameComponent(@NotNull ItemMeta meta, @NotNull Component nameComponent) {
    meta.displayName(nameComponent);
  }

  @Override
  public void appendItemNameComponent(@NotNull ItemMeta meta, @NotNull Component nameComponent) {
    Component existingName = meta.displayName();
    if (existingName == null) {
      meta.displayName(nameComponent);
      return;
    }

    meta.displayName(existingName.append(nameComponent));
  }

  @Override
  public void applyItemLoreComponent(@NotNull ItemMeta meta, @NotNull List<Component> loreComponentLines) {
    meta.lore(loreComponentLines);
  }

  @Override
  public void appendItemLoreComponent(@NotNull ItemMeta meta, @NotNull List<Component> loreComponentLines) {
    List<Component> existingLore = meta.lore();
    if (existingLore == null) {
      meta.lore(loreComponentLines);
      return;
    }
    existingLore.addAll(loreComponentLines); // meta.lore() is always modifiable?
    meta.lore(existingLore);
  }

  @Override
  public void showAdventureBossBar(@NotNull Player player, @NotNull BossBar bar) {
    player.showBossBar(bar);
  }

  @Override
  public void hideAdventureBossBar(@NotNull Player player, @NotNull BossBar bar) {
    player.hideBossBar(bar);
  }
}
