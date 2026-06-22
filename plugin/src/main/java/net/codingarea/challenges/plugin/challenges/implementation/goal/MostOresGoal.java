package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.PointsGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.1.1")
public class MostOresGoal extends PointsGoal {

  public MostOresGoal() {
    super(SettingCategory.SCORE_POINTS, new ItemStack(Material.COAL_ORE), "most-ores-goal");
  }

  private int getPointsForOre(Material material) {
    switch (material) {
      case EMERALD_ORE:
      case DEEPSLATE_EMERALD_ORE:
        return 15;
      case DIAMOND_ORE:
      case DEEPSLATE_DIAMOND_ORE:
        return 10;
      case LAPIS_ORE:
      case DEEPSLATE_LAPIS_ORE:
        return 8;
      case GOLD_ORE:
      case DEEPSLATE_GOLD_ORE:
        return 6;
      case IRON_ORE:
      case DEEPSLATE_IRON_ORE:
        return 4;
      case COAL_ORE:
      case DEEPSLATE_COAL_ORE:
      case REDSTONE_ORE:
      case DEEPSLATE_REDSTONE_ORE:
        return 2;
      default:
        return 0;
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onUpdate(@NotNull BlockBreakEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    int points = getPointsForOre(event.getBlock().getType());
    if (points > 0) {
      MessageKey.of("points-change").send(event.getPlayer(), Prefix.CHALLENGES, "+" + points);
      SoundSample.PLING.play(event.getPlayer());
      addPoints(event.getPlayer().getUniqueId(), points);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onUpdate(@NotNull BlockPlaceEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    int points = getPointsForOre(event.getBlock().getType());
    if (points > 0) {
      SoundSample.BASS_OFF.play(event.getPlayer());
      MessageKey.of("points-change").send(event.getPlayer(), Prefix.CHALLENGES, "-" + points);
      removePoints(event.getPlayer().getUniqueId(), points);
    }
  }

}
