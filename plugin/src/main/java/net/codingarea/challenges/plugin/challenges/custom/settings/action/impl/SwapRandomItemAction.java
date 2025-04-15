package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.PlayerTargetAction;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.RandomItemSwappingChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class SwapRandomItemAction extends PlayerTargetAction {

	public SwapRandomItemAction(String name) {
		super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(false, true));
	}

	@Override
	public Material getMaterial() {
		return Material.HOPPER;
	}

	@Override
	public void executeForPlayer(Player player, Map<String, String[]> subActions) {
		RandomItemSwappingChallenge.swapRandomItems(player);
	}

}
