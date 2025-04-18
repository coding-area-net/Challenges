package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.bukkit.utils.misc.MinecraftVersion;
import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.ItemCollectionGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Since("2.1.2")
public class CollectHorseAmorGoal extends ItemCollectionGoal {

	public CollectHorseAmorGoal() {
		super();
		setCategory(SettingCategory.FASTEST_TIME);
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

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.DIAMOND_HORSE_ARMOR, Message.forName("item-collect-horse-armor-goal"));
	}

}
