package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class OldPvPSetting extends Setting {

  public static final double DISABLED = 32, NORMAL = 4; // Values copied from BackToTheRoots

  public OldPvPSetting() {
    super(MenuType.SETTINGS);
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.IRON_SWORD, Message.forName("item-old-pvp-setting"));
  }

  @Override
  protected void onDisable() {
    broadcast(player -> setAttackSpeed(player, NORMAL));
  }

  @Override
  protected void onEnable() {
    broadcast(player -> setAttackSpeed(player, DISABLED));
  }

  @EventHandler
  public void onChangeWorldEvent(@NotNull PlayerChangedWorldEvent event) {
    if (!isEnabled()) return;
    setAttackSpeed(event.getPlayer(), DISABLED);
  }

  @EventHandler
  public void onJoin(@NotNull PlayerJoinEvent event) {
    if (!isEnabled()) return;
    setAttackSpeed(event.getPlayer(), DISABLED);
  }

  @EventHandler
  public void onQuit(@NotNull PlayerQuitEvent event) {
    setAttackSpeed(event.getPlayer(), NORMAL);
  }

  @EventHandler
  public void onSweepDamage(@NotNull EntityDamageEvent event) {
    if (!isEnabled()) return;
    if (event.getCause() != DamageCause.ENTITY_SWEEP_ATTACK) return;
    event.setCancelled(true);
  }

  protected void setAttackSpeed(@NotNull Player player, double value) {
    AttributeInstance attribute = player.getAttribute(MinecraftNameWrapper.ATTACK_SPEED);
    if (attribute == null) return;
    attribute.setBaseValue(value);
  }
}
