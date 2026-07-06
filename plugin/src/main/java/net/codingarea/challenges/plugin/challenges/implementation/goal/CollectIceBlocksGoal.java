package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.ItemCollectionGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Since("2.1.2")
public class CollectIceBlocksGoal extends ItemCollectionGoal {

  public CollectIceBlocksGoal() {
    super(SettingCategory.FASTEST_TIME, new ItemStack(Material.PACKED_ICE), "collect-ice", Material.ICE, Material.BLUE_ICE, Material.PACKED_ICE, Material.SNOW_BLOCK);
  }

}
