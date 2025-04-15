package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.0
 */
@Since("2.0")
public class RandomItemRemovingChallenge extends TimedChallenge {

	public RandomItemRemovingChallenge() {
		super(MenuType.CHALLENGES, 1, 30, 30);
		setCategory(SettingCategory.RANDOMIZER);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.DROPPER, Message.forName("item-random-removing-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-time-seconds-description").asArray(getValue() * 10);
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeSecondsValueChangeTitle(this, getValue() * 10);
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return getValue();
	}

	@Override
	protected void onTimeActivation() {
		restartTimer();

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (ignorePlayer(player)) continue;
			if (player.getInventory().getContents().length == 0) continue;

			Bukkit.getScheduler().runTask(plugin, () -> {
				InventoryUtils.removeRandomItem(player.getInventory());
			});
		}
	}

}