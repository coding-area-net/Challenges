package net.codingarea.challenges.plugin.management.menu.generator.implementation.custom;

import lombok.Getter;
import net.codingarea.challenges.plugin.challenges.custom.settings.IChallengeSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.SettingType;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.management.menu.generator.ChooseItemGenerator;
import net.codingarea.challenges.plugin.utils.misc.MapUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class CustomMainSettingsMenuGenerator extends ChooseItemGenerator implements IParentCustomGenerator {

	@Getter
  private final IParentCustomGenerator parent;
	private final SettingType type;
	private final String title;
	private final String key;
	private final Function<String, IChallengeSetting> instanceGetter;
	private IChallengeSetting setting;
	private SubSettingsBuilder subSettingsBuilder;
	private Map<String, String[]> subSettings;

	public CustomMainSettingsMenuGenerator(IParentCustomGenerator parent, SettingType type, String key, String title, LinkedHashMap<String, ItemStack> items, Function<String, IChallengeSetting> instanceGetter) {
		super(items);
		this.parent = parent;
		this.type = type;
		this.title = title;
		this.key = key;
		this.instanceGetter = instanceGetter;
		this.subSettings = new HashMap<>();
	}

	@Override
	public String[] getSubTitles(int page) {
		return new String[]{title};
	}

    @Override
	public void accept(Player player, SettingType type, Map<String, String[]> data) {

		subSettings.putAll(data);

		if (!openSubSettingsMenu(player)) {
			parent.accept(player, this.type, subSettings);
		}

	}

	@Override
	public void onItemClick(Player player, String itemKey) {
		this.setting = instanceGetter.apply(itemKey);
		this.subSettingsBuilder = setting.getSubSettingsBuilder();

		subSettings.put(key, new String[]{setting.getName()});

		if (!openSubSettingsMenu(player)) {
			parent.accept(player, type, subSettings);
		}

	}

	private boolean openSubSettingsMenu(Player player) {

		if (subSettingsBuilder != null && subSettingsBuilder.hasSettings()) {
			subSettingsBuilder.open(player, this, title);
			subSettingsBuilder = subSettingsBuilder.getChild();

			return true;
		}
		return false;
	}

	@Override
	public void onBackToMenuItemClick(Player player) {
		parent.decline(player);
	}

	@Override
	public void decline(Player player) {
		if (setting != null)
			this.subSettings = MapUtils.createStringArrayMap(key, setting.getName());
		open(player, 0);
	}

}
