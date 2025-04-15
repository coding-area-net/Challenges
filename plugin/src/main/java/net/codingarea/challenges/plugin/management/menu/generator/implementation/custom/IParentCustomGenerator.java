package net.codingarea.challenges.plugin.management.menu.generator.implementation.custom;

import net.codingarea.challenges.plugin.challenges.custom.settings.SettingType;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public interface IParentCustomGenerator {

	/**
	 * @param player the player that has the menu open
	 * @param type   the type of the current setting. Only needed if parent is the first setting menu.
	 * @param data   a map that contains all the data of the settings
	 */
	void accept(Player player, SettingType type, Map<String, String[]> data);

	void decline(Player player);

}
