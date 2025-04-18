package net.codingarea.challenges.plugin.management.menu.generator.implementation.custom;

import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.codingarea.challenges.plugin.challenges.custom.settings.SettingType;
import net.codingarea.challenges.plugin.management.menu.generator.ChooseItemGenerator;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.MapUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class MaterialMenuGenerator extends ChooseItemGenerator {

	private final IParentCustomGenerator parent;

	public MaterialMenuGenerator(IParentCustomGenerator parent) {
		super(createMaterialsMap());
		this.parent = parent;
	}

	public static LinkedHashMap<String, ItemStack> createMaterialsMap() {
		LinkedHashMap<String, ItemStack> map = new LinkedHashMap<>();

		for (Material material : ExperimentalUtils.getMaterials()) {
			if (BukkitReflectionUtils.isAir(material)) continue;
			if (!material.isItem()) continue;
			map.put(material.name(), new ItemStack(material));
		}

		return map;
	}

	@Override
	public String[] getSubTitles(int page) {
		return new String[]{"Material"};
	}

	@Override
	public void onItemClick(Player player, String itemKey) {
		parent.accept(player, SettingType.MATERIAL, MapUtils.createStringArrayMap("material", itemKey));
	}

	@Override
	public void onBackToMenuItemClick(Player player) {
		parent.decline(player);
	}

}
