package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.PointsGoal;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MineMostBlocksGoal extends PointsGoal {

  public MineMostBlocksGoal() {
    super(SettingCategory.SCORE_POINTS, new ItemStack(Material.GOLDEN_PICKAXE), "mine-most-blocks");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockBreak(@NotNull BlockBreakEvent event) {
    if (!isEnabled()) return;
    if (event.getBlock().isPassable()) return;
    collect(event.getPlayer());
  }

}
