package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.FindItemGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Since("2.1.1")
public class FindElytraGoal extends FindItemGoal {

  public FindElytraGoal() {
    super(SettingCategory.FASTEST_TIME, Material.ELYTRA, new ItemStack(Material.ELYTRA), "find-elytra");
  }

}
