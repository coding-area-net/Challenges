package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.FirstPlayerAtHeightGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.commons.bukkit.utils.misc.BukkitReflectionUtils;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.inventory.ItemStack;

@Since("2.1.0")
public class MinHeightGoal extends FirstPlayerAtHeightGoal {

  public MinHeightGoal() {
    super(SettingCategory.FASTEST_TIME, new ItemStack(Material.BEDROCK), "min-height-goal");
    setHeightToGetTo(BukkitReflectionUtils.getMinHeight(ChallengeAPI.getGameWorld(Environment.NORMAL)) + 1);
  }

}
