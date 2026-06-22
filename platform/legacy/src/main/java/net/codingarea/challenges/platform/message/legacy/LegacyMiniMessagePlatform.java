package net.codingarea.challenges.platform.message.legacy;

import net.codingarea.challenges.platform.message.common.AbstractMiniMessagePlatform;
import net.codingarea.challenges.platform.message.common.wrapper.AdventureMessageBossBar;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Adds support for MiniMessages if there is no native implementation found, like on spigot or older paper versions.
 * It uses a bundled and relocated version of the adventure dependency.
 */
public class LegacyMiniMessagePlatform extends AbstractMiniMessagePlatform {

  protected final JavaPlugin plugin;
  protected BukkitAudiences audiences;

  public LegacyMiniMessagePlatform(@NotNull JavaPlugin plugin) {
    super(MiniMessage.miniMessage(), LegacyComponentSerializer.legacySection());
    this.plugin = plugin;
  }

  @Override
  public void enable() {
    // lib will try to register a PlayerJoinListener which is only possible once the plugin is enabled
    this.audiences = BukkitAudiences.create(plugin);
  }

  @Override
  public void disable() {
    audiences.close();
  }

  @Override
  public void sendSenderComponent(@NotNull CommandSender sender, @NotNull Component component) {
    audiences.sender(sender).sendMessage(component);
  }

  @Override
  public void sendChatComponent(@NotNull Player player, @NotNull Component component) {
    audiences.player(player).sendMessage(component);
  }

  @Override
  public void sendActionBarComponent(@NotNull Player player, @NotNull Component component) {
    audiences.player(player).sendActionBar(component);
  }

  @Override
  public void sendAdventureTitle(@NotNull Player player, @NotNull Title title) {
    audiences.player(player).showTitle(title);
  }

  @NotNull
  @Override
  public Inventory createInventoryTitled(@NotNull InventoryHolder owner, int size, @NotNull Component titleComponent) {
    return Bukkit.createInventory(owner, size, legacySerializer.serialize(titleComponent));
  }

  @NotNull
  @Override
  public Inventory createInventoryTitled(@NotNull InventoryHolder owner, @NotNull InventoryType type, @NotNull Component titleComponent) {
    return Bukkit.createInventory(owner, type, legacySerializer.serialize(titleComponent));
  }

  @NotNull
  @Override
  public Component getPlayerNameComponent(@NotNull Player player) {
    return legacySerializer.deserialize(player.getDisplayName());
  }

  @Override
  public void applyItemNameComponent(@NotNull ItemMeta meta, @NotNull Component nameComponent) {
    meta.setDisplayName(legacySerializer.serialize(nameComponent));
  }

  @Override
  public void appendItemNameComponent(@NotNull ItemMeta meta, @NotNull Component nameComponent) {
    String existingName = meta.hasDisplayName() ? meta.getDisplayName() : "";
    meta.setDisplayName(existingName + legacySerializer.serialize(nameComponent));
  }

  @Override
  public void applyItemLoreComponent(@NotNull ItemMeta meta, @NotNull List<Component> loreComponentLines) {
    meta.setLore(componentsToLegacyStrings(loreComponentLines));
  }

  @Override
  public void appendItemLoreComponent(@NotNull ItemMeta meta, @NotNull List<Component> loreComponentLines) {
    List<String> existingLore = meta.getLore();
    if (existingLore == null) {
      applyItemLoreComponent(meta, loreComponentLines);
      return;
    }
    existingLore.addAll(componentsToLegacyStrings(loreComponentLines));
    meta.setLore(existingLore);
  }

  @Override
  public void showAdventureBossBar(@NotNull Player player, @NotNull BossBar bar) {
    audiences.player(player).showBossBar(bar);
  }

  @Override
  public void hideAdventureBossBar(@NotNull Player player, @NotNull BossBar bar) {
    audiences.player(player).hideBossBar(bar);
  }

  @NotNull
  protected List<String> componentsToLegacyStrings(@NotNull List<Component> components) {
    return components.stream()
      .map(legacySerializer::serialize)
      .toList();
  }
}
