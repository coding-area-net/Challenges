package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.ItemCollectionGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.RequireVersion;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Since("2.1.2")
@RequireVersion(MinecraftVersion.V1_14)
public class CollectWorkstationsGoal extends ItemCollectionGoal {

  public CollectWorkstationsGoal() {
    super(
      SettingCategory.FASTEST_TIME, new ItemStack(Material.FLETCHING_TABLE), "collect-workstations",
      Material.LECTERN, Material.COMPOSTER, Material.GRINDSTONE, Material.BLAST_FURNACE,
      Material.SMOKER, Material.FLETCHING_TABLE, Material.CARTOGRAPHY_TABLE,
      Material.BREWING_STAND, Material.SMITHING_TABLE, Material.CAULDRON,
      Material.LOOM, Material.STONECUTTER, Material.BARREL
    );
  }

}
