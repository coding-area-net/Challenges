package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeExecutionData;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.ChallengeAction;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.Map;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.3
 */
public class ChangeWorldBorderAction extends ChallengeAction {

	public ChangeWorldBorderAction(String name) {
		super(name, SubSettingsBuilder.createValueItem()
				.addModifierSetting(
						"change",
						new ItemBuilder(Material.MAGENTA_GLAZED_TERRACOTTA, Message.forName("item-custom-action-modify_border-change")),
						1, -10, 10
				));
	}

	@Override
	public Material getMaterial() {
		return Material.STRUCTURE_VOID;
	}

	@Override
	public void execute(ChallengeExecutionData executionData, Map<String, String[]> subActions) {

		try {
			int change = Integer.parseInt(subActions.get("change")[0]);

			for (World world : ChallengeAPI.getGameWorlds()) {
				WorldBorder border = world.getWorldBorder();
				if (border.getSize() + change >= 1) { // Don't change if it gets under 1
					border.setSize(border.getSize() + change);
				}
			}

		} catch (NumberFormatException | IndexOutOfBoundsException ex) {
			Logger.error("Error while accessing change border action values", ex);
		}

	}

}
