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

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.2
 */
@Since("2.1.2")
public class CollectSwordsGoal extends ItemCollectionGoal {

	public CollectSwordsGoal() {
		setCategory(SettingCategory.FASTEST_TIME);
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

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.DIAMOND_SWORD, Message.forName("item-collect-swords-goal"));
	}

}
