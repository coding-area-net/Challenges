package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.FirstPlayerAtHeightGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.inventory.ItemStack;

@Since("2.1.0")
public class MaxHeightGoal extends FirstPlayerAtHeightGoal {

  public MaxHeightGoal() {
    super(SettingCategory.FASTEST_TIME, new ItemStack(Material.FEATHER), "max-height");
    setHeightToGetTo(ChallengeAPI.getGameWorld(Environment.NORMAL).getMaxHeight());
  }

}
