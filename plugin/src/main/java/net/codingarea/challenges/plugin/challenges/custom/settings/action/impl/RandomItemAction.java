package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeExecutionData;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.ChallengeAction;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.IEntityTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class RandomItemAction extends ChallengeAction {

	public RandomItemAction(String name) {
		super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(false, true));
	}

	public static void giveRandomItemToPlayer(@Nonnull Player player) {
		InventoryUtils.giveItem(player.getInventory(),
				player.getLocation(), InventoryUtils.getRandomItem(true, false));
	}

	@Override
	public void execute(
			ChallengeExecutionData executionData,
			Map<String, String[]> subActions) {

		for (Entity target : IEntityTargetAction.getTargets(executionData.getEntity(), subActions)) {
			if (target instanceof Player) {
				Player player = (Player) target;
				giveRandomItemToPlayer(player);
			}
		}
	}

	@Override
	public Material getMaterial() {
		return Material.BEACON;
	}

}
