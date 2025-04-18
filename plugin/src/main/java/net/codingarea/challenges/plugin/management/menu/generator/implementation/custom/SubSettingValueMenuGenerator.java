package net.codingarea.challenges.plugin.management.menu.generator.implementation.custom;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.ValueSetting;
import net.codingarea.challenges.plugin.management.menu.generator.ValueMenuGenerator;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SubSettingValueMenuGenerator extends ValueMenuGenerator {

	private final IParentCustomGenerator parent;
	private final String title;

	public SubSettingValueMenuGenerator(IParentCustomGenerator parent, Map<ValueSetting, String> settings, String title) {
		super(settings);
		this.title = title;
		this.parent = parent;
	}

	@Override
	public String[] getSubTitles(int page) {
		return new String[]{title};
	}

	@Override
	public void onSaveItemClick(Player player) {

		Map<String, String[]> map = new HashMap<>();
		for (Entry<ValueSetting, String> entry : getSettings().entrySet()) {
			map.put(entry.getKey().getKey(), new String[]{entry.getValue()});
		}

		parent.accept(player, null, map);
	}

	@Override
	public void onBackToMenuItemClick(Player player) {
		parent.decline(player);
	}

}
