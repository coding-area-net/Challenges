package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.EntityDamageByPlayerEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ReversedDamageChallenge extends Setting {

  public ReversedDamageChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.DAMAGE, new ItemStack(Material.GOLDEN_SWORD), "reversed-damange-challenge");
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onDamageByPlayer(@NotNull EntityDamageByPlayerEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getDamager())) return;

    double damage = event.getFinalDamage();
    event.getDamager().damage(damage);
  }

}
