package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.impl.RandomItemAction;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Since("2.0")
public class RandomItemChallenge extends TimedChallenge {

	public RandomItemChallenge() {
		super(MenuType.CHALLENGES, 1, 60, 30, false);
		setCategory(SettingCategory.RANDOMIZER);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.BEACON, Message.forName("item-random-item-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-time-seconds-description").asArray(getValue());
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeSecondsValueChangeTitle(this, getValue());
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return getValue();
	}

	@Override
	protected void onTimeActivation() {
		restartTimer();
		broadcastFiltered(RandomItemAction::giveRandomItemToPlayer);
	}

}
