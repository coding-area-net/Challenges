package net.codingarea.challenges.plugin.challenges.custom.settings.sub;

import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;

public abstract class ValueSetting {

	@Getter
	private final String key;
	private final ItemBuilder itemBuilder;

	public ValueSetting(String key, ItemBuilder itemBuilder) {
		this.key = key;
		this.itemBuilder = itemBuilder;
	}

	public ItemBuilder createDisplayItem() {
		return itemBuilder;
	}

	public abstract String onClick(MenuClickInfo info, String value, int slotIndex);

	public ItemBuilder getDisplayItem(String value) {
		return createDisplayItem().hideAttributes();
	}

	public abstract ItemBuilder getSettingsItem(String value);

}
