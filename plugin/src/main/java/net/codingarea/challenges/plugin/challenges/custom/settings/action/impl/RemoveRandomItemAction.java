package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.PlayerTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class RemoveRandomItemAction extends PlayerTargetAction {

	public RemoveRandomItemAction(String name) {
		super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(false, true));
	}

	@Override
	public Material getMaterial() {
		return Material.DROPPER;
	}

	@Override
	public void executeForPlayer(Player player, Map<String, String[]> subActions) {
		InventoryUtils.removeRandomItem(player.getInventory());
	}

}
