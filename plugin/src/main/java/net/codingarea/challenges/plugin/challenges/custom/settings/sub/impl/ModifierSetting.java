package net.codingarea.challenges.plugin.challenges.custom.settings.sub.impl;

import net.anweisen.utilities.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.ValueSetting;
import net.codingarea.challenges.plugin.challenges.type.IModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;

import java.util.function.Function;

public class ModifierSetting extends ValueSetting implements IModifier {

	private final int min, max;
	private final Function<Integer, ItemBuilder> settingsItemGetter;

	private int tempValue;

	public ModifierSetting(String key, int min, int max, ItemBuilder displayItem) {
		this(key, min, max, displayItem, integer -> "", integer -> "");
	}

	public ModifierSetting(String key, int min, int max, ItemBuilder itemBuilder, Function<Integer, String> prefixGetter, Function<Integer, String> suffixGetter) {
		this(key, min, max, itemBuilder, value ->
		{
			String prefix = prefixGetter.apply(value);
			String suffix = suffixGetter.apply(value);
			return DefaultItem.value(value, (prefix.isEmpty() ? "" : "§7" + prefix + " ") + "§e")
					.appendName(suffix.isEmpty() ? "" : " " + "§7" + suffix);
		});
	}

	public ModifierSetting(String key, int min, int max, ItemBuilder itemBuilder, Function<Integer, ItemBuilder> settingsItemGetter) {
		super(key, itemBuilder);
		this.min = min;
		this.max = max;
		this.settingsItemGetter = settingsItemGetter;
	}

	@Override
	public String onClick(MenuClickInfo info, String value, int slotIndex) {

		int intValue = getIntValue(value);
		tempValue = intValue;

		ChallengeHelper.handleModifierClick(info, this);

		intValue = tempValue;
		tempValue = 0;
		return String.valueOf(intValue);
	}

	@Override
	public int getValue() {
		return tempValue;
	}

	@Override
	public void setValue(int value) {
		tempValue = value;
	}

	@Override
	public int getMinValue() {
		return min;
	}

	@Override
	public int getMaxValue() {
		return max;
	}

	@Override
	public void playValueChangeTitle() {
	}

	@Override
	public ItemBuilder getSettingsItem(String value) {
		int intValue = getIntValue(value);
		return settingsItemGetter.apply(intValue);
	}

	public int getIntValue(String value) {

		try {
			return Integer.parseInt(value);
		} catch (Exception exception) {
			Challenges.getInstance().getLogger().severe("Something went wrong while parsing the "
					+ "value of subsetting " + getKey() + " with value " + value);
			Challenges.getInstance().getLogger().error("", exception);
		}

		return 0;
	}

}
