package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.EntityTargetAction;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory.UncraftItemsChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class UncraftInventoryAction extends EntityTargetAction {

	public UncraftInventoryAction(String name) {
		super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(false));
	}

	@Override
	public void executeFor(Entity entity, Map<String, String[]> subActions) {
		if (entity instanceof Player) {
			Player player = (Player) entity;
			UncraftItemsChallenge.uncraftInventory(player);
		}
	}

	@Override
	public Material getMaterial() {
		return Material.CRAFTING_TABLE;
	}

}
