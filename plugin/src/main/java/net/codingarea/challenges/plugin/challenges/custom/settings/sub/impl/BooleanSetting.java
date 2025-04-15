package net.codingarea.challenges.plugin.challenges.custom.settings.sub.impl;

import net.anweisen.utilities.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.ValueSetting;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class BooleanSetting extends ValueSetting {


	public BooleanSetting(String key, ItemBuilder itemBuilder) {
		super(key, itemBuilder);
	}

	@Override
	public String onClick(MenuClickInfo info, String value,
						  int slotIndex) {
		return value.equals("enabled") ? "disabled" : "enabled";
	}

	@Override
	public ItemBuilder getSettingsItem(String value) {
		return value.equals("enabled") ? DefaultItem.enabled() : DefaultItem.disabled();
	}

}
