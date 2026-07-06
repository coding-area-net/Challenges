package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class DeathOnFallChallenge extends Setting {

  public DeathOnFallChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.DAMAGE, new ItemStack(Material.FEATHER), "death-on-fall");
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDamage(@NotNull EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    if (!shouldExecuteEffect()) return;
    Player player = (Player) event.getEntity();
    if (ignorePlayer(player)) return;
    if (event.getCause() != DamageCause.FALL) return;
    event.setDamage(player.getHealth());
    event.setCancelled(false);
  }

}
