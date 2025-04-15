package net.codingarea.challenges.plugin.utils.misc;

import org.bukkit.Material;

/**
 * @author KxmischesDomi | <a href="https://github.com/KxmischesDomi">...</a>
 * @since 2.0
 */
public final class ArmorUtils {

	private ArmorUtils() {
	}

	public static Material[] getArmor() {
		return new Material[]{
			Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.IRON_HELMET,
			Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.DIAMOND_HELMET,
			Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.GOLDEN_HELMET,
			Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.LEATHER_HELMET,
			Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_HELMET,
			Utils.getMaterial("NETHERITE_CHESTPLATE"), Utils.getMaterial("NETHERITE_LEGGINGS"),
			Utils.getMaterial("NETHERITE_BOOTS"), Utils.getMaterial("NETHERITE_HELMET"),
			Material.TURTLE_HELMET
		};
	}

}
