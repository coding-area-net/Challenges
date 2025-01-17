package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifierGoal;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.2.0
 */
@Since("2.2.0")
public class GetFullHealthGoal extends SettingModifierGoal {

	public GetFullHealthGoal() {
		super(MenuType.GOAL, 1, 20, 20);
		setCategory(SettingCategory.FASTEST_TIME);
	}

	@Override
	protected void onEnable() {
		broadcastFiltered(player -> {
			player.setHealth(getValue());
		});
	}

	@Override
	protected void onValueChange() {
		broadcastFiltered(player -> {
			player.setHealth(getValue());
		});
	}

	@Override
	public void getWinnersOnEnd(@NotNull List<Player> winners) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (ignorePlayer(player)) continue;
			AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
			if (attribute != null) {
				if (player.getHealth() >= attribute.getBaseValue()) {
					winners.add(player);
				}
			}
		}
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.AZURE_BLUET, Message.forName("item-get-full-health-goal"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-heart-start-description").asArray(getValue() / 2f);
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeHeartsValueChangeTitle(this);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHealthChange(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer((Player) event.getEntity())) return;
		AttributeInstance attribute = ((Player) event.getEntity()).getAttribute(Attribute.MAX_HEALTH);
		Bukkit.getScheduler().runTask(plugin, () -> {
			if (attribute != null && ((Player) event.getEntity()).getHealth() >= attribute.getBaseValue()) {
				ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED);
			}
		});
	}

}
