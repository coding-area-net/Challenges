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
public class CollectHorseAmorGoal extends ItemCollectionGoal {

  public CollectHorseAmorGoal() {
    super(SettingCategory.FASTEST_TIME, new ItemStack(Material.DIAMOND_HORSE_ARMOR), "collect-horse-armor");
    List<Material> targets = new ArrayList<>(Arrays.asList(
      Material.DIAMOND_HORSE_ARMOR,
      Material.GOLDEN_HORSE_ARMOR,
      Material.IRON_HORSE_ARMOR
    ));

    if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_14)) {
      targets.add(Material.LEATHER_HORSE_ARMOR);
    }

    setTarget(targets.toArray(new Object[0]));
  }

}
