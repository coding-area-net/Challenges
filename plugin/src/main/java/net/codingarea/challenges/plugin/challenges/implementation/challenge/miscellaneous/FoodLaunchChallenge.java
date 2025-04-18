package net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.EntityUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Since("2.0.2")
public class FoodLaunchChallenge extends SettingModifier {

	public FoodLaunchChallenge() {
		super(MenuType.CHALLENGES, 1, 10, 2);
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.CAKE, Message.forName("item-consume-launch-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-launcher-description").asArray(getValue());
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChangeChallengeValueTitle(this, Message.forName("subtitle-launcher-description").asString(getValue()));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerItemConsume(@Nonnull PlayerItemConsumeEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;

		Vector velocityToAdd = new Vector(0, getValue() / 2, 0);
		Vector newVelocity = EntityUtils.getSucceedingVelocity(event.getPlayer().getVelocity()).add(velocityToAdd);
		event.getPlayer().setVelocity(newVelocity);
	}

}
