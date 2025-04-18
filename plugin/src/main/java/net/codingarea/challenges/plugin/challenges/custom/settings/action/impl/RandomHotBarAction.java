package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.PlayerTargetAction;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.HotBarRandomizerChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class RandomHotBarAction extends PlayerTargetAction {

	public RandomHotBarAction(String name) {
		super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(true, true));
	}

	@Override
	public Material getMaterial() {
		return Material.ARMOR_STAND;
	}

	@Override
	public void executeForPlayer(Player player, Map<String, String[]> subActions) {
		HotBarRandomizerChallenge.addItems(player, true);
	}

}
