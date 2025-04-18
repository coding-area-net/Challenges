package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.bukkit.misc.wrapper.AttributeWrapper;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;

@Since("2.0")
public class OldPvPSetting extends Setting {

  public static final double DISABLED = 32, NORMAL = 4; // Values copied from BackToTheRoots

  public OldPvPSetting() {
    super(MenuType.SETTINGS);
  }

  @Nonnull
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
  public void onChangeWorldEvent(@Nonnull PlayerChangedWorldEvent event) {
    if (!isEnabled()) return;
    setAttackSpeed(event.getPlayer(), DISABLED);
  }

  @EventHandler
  public void onJoin(@Nonnull PlayerJoinEvent event) {
    if (!isEnabled()) return;
    setAttackSpeed(event.getPlayer(), DISABLED);
  }

  @EventHandler
  public void onQuit(@Nonnull PlayerQuitEvent event) {
    setAttackSpeed(event.getPlayer(), NORMAL);
  }

  @EventHandler
  public void onSweepDamage(@Nonnull EntityDamageEvent event) {
    if (!isEnabled()) return;
    if (event.getCause() != DamageCause.ENTITY_SWEEP_ATTACK) return;
    event.setCancelled(true);
  }

  protected void setAttackSpeed(@Nonnull Player player, double value) {
    AttributeInstance attribute = player.getAttribute(AttributeWrapper.ATTACK_SPEED);
    if (attribute == null) return;
    attribute.setBaseValue(value);
  }
}
