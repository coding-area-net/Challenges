package net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.management.menu.generator.MenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.IParentCustomGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.SubSettingChooseMenuGenerator;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
@Getter
public class ChooseItemSubSettingsBuilder extends GeneratorSubSettingsBuilder {

	protected final LinkedHashMap<String, ItemStack> settings = new LinkedHashMap<>();

	public ChooseItemSubSettingsBuilder(String key) {
		super(key);
	}

	public ChooseItemSubSettingsBuilder(String key, SubSettingsBuilder parent) {
		super(key, parent);
	}

	@Override
	public MenuGenerator getGenerator(Player player, IParentCustomGenerator parentGenerator, String title) {
		return new SubSettingChooseMenuGenerator(getKey(), parentGenerator, getSettings(), title);
	}

	@Override
	public List<String> getDisplay(Map<String, String[]> activated) {
		List<String> display = Lists.newLinkedList();

		for (Entry<String, String[]> entry : activated.entrySet()) {
			if (entry.getKey().equals(getKey())) {
				for (String value : entry.getValue()) {
					ItemStack itemStack = getSettings().get(value);
					if (itemStack != null) {
						if (itemStack.getItemMeta() == null) continue;
						display.add("§7" + getKeyTranslation() + " " + itemStack.getItemMeta().getDisplayName());
					}
				}
			}
		}

		return display;
	}

	public ChooseItemSubSettingsBuilder addSetting(String key, ItemStack value) {
		settings.put(key, value);
		return this;
	}

	public ChooseItemSubSettingsBuilder addSetting(String key, ItemBuilder value) {
		settings.put(key, value.hideAttributes().build());
		return this;
	}

	public ChooseItemSubSettingsBuilder fill(Consumer<ChooseItemSubSettingsBuilder> actions) {
		actions.accept(this);
		return this;
	}

	public boolean hasSettings() {
		return !settings.isEmpty();
	}

}
