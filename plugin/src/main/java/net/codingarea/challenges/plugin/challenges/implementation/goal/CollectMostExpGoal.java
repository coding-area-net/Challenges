package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.PointsGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class CollectMostExpGoal extends PointsGoal {

  public CollectMostExpGoal() {
    super(SettingCategory.SCORE_POINTS, new ItemStack(Material.EXPERIENCE_BOTTLE), "most-xp-goal");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onExpChange(@NotNull PlayerExpChangeEvent event) {
    if (!shouldExecuteEffect()) return;
    collect(event.getPlayer(), event.getAmount());
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
    if (!shouldExecuteEffect()) return;
    event.setKeepLevel(true);
  }

}
