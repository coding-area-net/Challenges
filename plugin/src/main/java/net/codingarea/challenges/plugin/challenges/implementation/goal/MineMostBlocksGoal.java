package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.PointsGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import javax.annotation.Nonnull;

public class MineMostBlocksGoal extends PointsGoal {

  public MineMostBlocksGoal() {
    super();
    setCategory(SettingCategory.SCORE_POINTS);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.GOLDEN_PICKAXE, Message.forName("item-mine-most-blocks-goal"));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockBreak(@Nonnull BlockBreakEvent event) {
    if (!isEnabled()) return;
    if (event.getBlock().isPassable()) return;
    collect(event.getPlayer());
  }

}
