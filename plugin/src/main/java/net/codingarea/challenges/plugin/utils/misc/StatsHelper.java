package net.codingarea.challenges.plugin.utils.misc;

import net.anweisen.utilities.bukkit.utils.animation.AnimatedInventory;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.stats.Statistic;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public final class StatsHelper {

	private StatsHelper() {
	}

	@Nonnull
	public static int[] getSlots(int row) {
		int[] slots = {1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 14, 15};
		for (int i = 0; i < slots.length; i++) {
			slots[i] += row * 9;
		}
		return slots;
	}

	public static void setAccent(@Nonnull AnimatedInventory inventory, int row) {
		inventory.createAndAdd().fill(ItemBuilder.FILL_ITEM);
		int offset = row * 9;
		inventory.cloneLastAndAdd().setAccent(offset, offset + 8);
		inventory.cloneLastAndAdd().setAccent(offset + 1, offset + 7);
		inventory.cloneLastAndAdd().setAccent(offset + 10, offset + 16);
		inventory.cloneLastAndAdd().setAccent(offset + 11, offset + 15);
		inventory.cloneLastAndAdd().setAccent(offset + 12, offset + 14);
	}

	@Nonnull
	public static Message getNameMessage(@Nonnull Statistic statistic) {
		return Message.forName("stat-" + statistic.name().toLowerCase().replace('_', '-'));
	}

	@Nonnull
	public static Material getMaterial(@Nonnull Statistic statistic) {
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
