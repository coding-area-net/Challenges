package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder.PotionBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SplitHealthSetting extends Setting {

  public SplitHealthSetting() {
    super(MenuType.SETTINGS);
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new PotionBuilder(Material.TIPPED_ARROW, Message.forName("item-split-health-setting")).color(Color.RED);
  }

  @Override
  protected void onEnable() {
    setHealth();
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityRegainHealth(@NotNull EntityRegainHealthEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof Player)) return;
    Player player = (Player) event.getEntity();
    if (ignorePlayer(player)) return;

    Bukkit.getScheduler().runTaskLater(plugin, () -> setHealth(player, null), 1);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onDamage(EntityDamageEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof Player)) return;
    Player player = (Player) event.getEntity();
    if (ignorePlayer(player)) return;

    Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), () -> setHealth(player, event), 1);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onJoin(@NotNull PlayerJoinEvent event) {
    if (ignorePlayer(event.getPlayer())) return;
    setHealth();
  }

  /**
   * Calls {@link SplitHealthSetting#setHealth(Player, EntityDamageEvent)} for the player with the lowest health
   */
  private void setHealth() {
    Player player = null;

    for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
      if (player == null || player.getHealth() > currentPlayer.getHealth()) {
        player = currentPlayer;
      }

    }
    if (player == null) return;
    setHealth(player, null);
  }

  public void setHealth(@NotNull Player player, @Nullable EntityDamageEvent damageEvent) {
    if (!shouldExecuteEffect()) return;

    for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
      if (ignorePlayer(currentPlayer)) continue;
      if (currentPlayer.equals(player)) continue;

      double health = player.getHealth();
      AttributeInstance attribute = player.getAttribute(MinecraftNameWrapper.MAX_HEALTH);
      if (attribute == null) return;
      if (health > attribute.getValue()) {
        health = attribute.getValue();
      }

      if (health <= 0 && damageEvent != null) {
        tryApplyLastDamageCause(currentPlayer, damageEvent);
      }

      currentPlayer.setHealth(health);
    }

  }

  @SuppressWarnings({"deprecation", "removal"})
  private void tryApplyLastDamageCause(Player player, EntityDamageEvent damageEvent) {
    // marked for removal in api version 1.20.4 with no apparent replacement?
    try {
      player.setLastDamageCause(damageEvent);
    } catch (Throwable ignored) {
    }
  }

}
