package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.ItemCollectionGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Since("2.1.2")
public class CollectSwordsGoal extends ItemCollectionGoal {

  public CollectSwordsGoal() {
    super(SettingCategory.FASTEST_TIME, new ItemStack(Material.DIAMOND_SWORD), "collect-swords");
    List<Material> targets = new ArrayList<>(Arrays.asList(
      Material.WOODEN_SWORD, Material.STONE_SWORD,
      Material.IRON_SWORD, Material.GOLDEN_SWORD,
      Material.DIAMOND_SWORD
    ));

    if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_16)) {
      targets.add(Material.NETHERITE_SWORD);
    }

    setTarget(targets.toArray(new Object[0]));
  }

}
