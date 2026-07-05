package net.codingarea.challenges.plugin.utils.misc;

import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.management.stats.Statistic;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.AnimatedInventory;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class StatsHelper {

  private StatsHelper() {
  }

  @NotNull
  public static int[] getSlots(int row) {
    int[] slots = {1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 14, 15};
    for (int i = 0; i < slots.length; i++) {
      slots[i] += row * 9;
    }
    return slots;
  }

  public static void setAccent(@NotNull AnimatedInventory inventory, int row) {
    inventory.createAndAdd().fill(LegacyItemBuilder.FILL_ITEM);
    int offset = row * 9;
    inventory.cloneLastAndAdd().setContrast(offset, offset + 8);
    inventory.cloneLastAndAdd().setContrast(offset + 1, offset + 7);
    inventory.cloneLastAndAdd().setContrast(offset + 10, offset + 16);
    inventory.cloneLastAndAdd().setContrast(offset + 11, offset + 15);
    inventory.cloneLastAndAdd().setContrast(offset + 12, offset + 14);
  }

  @NotNull
  public static Message getNameMessage(@NotNull Statistic statistic) {
    return Message.forName("stat-" + statistic.name().toLowerCase().replace('_', '-'));
  }

  @NotNull
  public static Material getMaterial(@NotNull Statistic statistic) {
    switch (statistic) {
      default:
        return Material.PAPER;
      case DEATHS:
        return Material.STONE_SHOVEL;
      case BLOCKS_MINED:
        return Material.GOLDEN_PICKAXE;
      case BLOCKS_PLACED:
        return Material.DIRT;
      case DAMAGE_DEALT:
        return Material.STONE_SWORD;
      case DAMAGE_TAKEN:
        return Material.LEATHER_CHESTPLATE;
      case ENTITY_KILLS:
        return Material.IRON_SWORD;
      case DRAGON_KILLED:
        return Material.DRAGON_EGG;
      case BLOCKS_TRAVELED:
        return Material.MINECART;
      case CHALLENGES_PLAYED:
        return Material.GOLD_INGOT;
      case JUMPS:
        return Material.GOLDEN_BOOTS;
    }
  }

}
