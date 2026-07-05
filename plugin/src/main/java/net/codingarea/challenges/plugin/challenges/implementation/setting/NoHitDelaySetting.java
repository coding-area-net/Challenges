package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NoHitDelaySetting extends Setting {

  public NoHitDelaySetting() {
    super(MenuType.SETTINGS, null, new ItemStack(Material.FEATHER), "no-hit-delay");
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onDamageByEntity(@NotNull EntityDamageEvent event) {
    if (!(event.getEntity() instanceof LivingEntity)) return;
    if (!shouldExecuteEffect()) return;
    Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), () -> {
      ((LivingEntity) event.getEntity()).setNoDamageTicks(0);
    }, 1);
  }

}
