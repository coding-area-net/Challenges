package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.FirstPlayerAtHeightGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.jetbrains.annotations.NotNull;

@Since("2.1.0")
public class MinHeightGoal extends FirstPlayerAtHeightGoal {

	public MinHeightGoal() {
		setCategory(SettingCategory.FASTEST_TIME);
		setHeightToGetTo(BukkitReflectionUtils.getMinHeight(ChallengeAPI.getGameWorld(Environment.NORMAL)) + 1);
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.BEDROCK, Message.forName("item-min-height-goal").asItemDescription(getHeightToGetTo()));
	}

}
