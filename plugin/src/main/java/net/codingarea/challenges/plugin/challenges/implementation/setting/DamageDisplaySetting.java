package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.commons.common.misc.StringUtils;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.stats.Statistic.Display;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nonnull;

public class DamageDisplaySetting extends Setting {

  public DamageDisplaySetting() {
    super(MenuType.SETTINGS, true);
  }

  public static String getCause(@Nonnull EntityDamageEvent event) {

    if (event.getCause() == DamageCause.CUSTOM) return Message.forName("undefined").asString();
    String cause = StringUtils.getEnumName(event.getCause());

    if (event instanceof EntityDamageByEntityEvent) {

      EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
      if (damageEvent.getDamager() instanceof Player) {
        Player damager = (Player) damageEvent.getDamager();
        cause += " §8(§7" + NameHelper.getName(damager) + "§8)";
      } else if (damageEvent.getDamager() instanceof Projectile) {
        Projectile projectile = (Projectile) damageEvent.getDamager();
        cause = StringUtils.getEnumName(projectile.getType());
        String damager = "";
        ProjectileSource shooter = projectile.getShooter();
        if (shooter instanceof Entity) {
          Entity entity = (Entity) shooter;
          if (entity instanceof Player) {
            Player playerDamager = (Player) entity;
            damager = NameHelper.getName(playerDamager);
          } else {
            damager = StringUtils.getEnumName(entity.getType());
          }
        }

        if (!cause.contains(damager))
          cause += " §8(§7" + damager + "§8)";
      } else {
        String damager = StringUtils.getEnumName(damageEvent.getDamager().getType());
        if (!cause.contains(damager))
          cause += " §8(§7" + damager + "§8)";
      }
    }
    return cause;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(@Nonnull EntityDamageEvent event) {
    if (ChallengeAPI.isPaused() || event.getCause() == DamageCause.CUSTOM || !isEnabled())
      return;
    if (!(event.getEntity() instanceof Player)) return;
    if (ChallengeHelper.finalDamageIsNull(event)) return;

    double damage = event.getFinalDamage();
    String damageDisplay = damage >= 1000 ? "∞" : Display.HEARTS.formatChat(damage);
    Message.forName("player-damage-display").broadcast(Prefix.DAMAGE, NameHelper.getName((Player) event.getEntity()), damageDisplay, getCause(event));
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.COMMAND_BLOCK, Message.forName("item-damage-display-setting"));
  }
}
