package net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.MenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.IParentCustomGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.SubSettingChooseMultipleMenuGenerator;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
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
public class ChooseMultipleItemSubSettingBuilder extends GeneratorSubSettingsBuilder {

	protected final LinkedHashMap<String, ItemStack> settings = new LinkedHashMap<>();

	public ChooseMultipleItemSubSettingBuilder(String key) {
		super(key);
	}

	public ChooseMultipleItemSubSettingBuilder(String key, SubSettingsBuilder parent) {
		super(key, parent);
	}

	@Override
	public MenuGenerator getGenerator(Player player, IParentCustomGenerator parentGenerator, String title) {
		return new SubSettingChooseMultipleMenuGenerator(getKey(), parentGenerator, getSettings(), title);
	}

	@Override
	public List<String> getDisplay(Map<String, String[]> activated) {
		List<String> display = Lists.newLinkedList();

		for (Entry<String, String[]> entry : activated.entrySet()) {
			if (entry.getKey().equals(getKey())) {

				int count = 0;
				String firstDisplay = null;

				for (String value : entry.getValue()) {
					ItemStack itemStack = getSettings().get(value);
					if (itemStack != null) {
						if (firstDisplay == null) {
							if (itemStack.getItemMeta() == null) continue;
							firstDisplay = "§7" + getKeyTranslation() + " " + itemStack.getItemMeta().getDisplayName();
						} else {
							count++;
						}

					}
				}

				if (firstDisplay != null) {
					String suffix = count == 0 ? "" : " §7+" + count;
					display.add(firstDisplay + suffix);
				} else {
					display.add("§7" + getKeyTranslation() + " " + DefaultItem.getItemPrefix() + Message.forName("none").asString());
				}

			}
		}

		return display;
	}

	public ChooseMultipleItemSubSettingBuilder addSetting(String key, ItemStack value) {
		settings.put(key, value);
		return this;
	}

	public ChooseMultipleItemSubSettingBuilder addSetting(String key, ItemBuilder value) {
		settings.put(key, value.hideAttributes().build());
		return this;
	}

	public ChooseMultipleItemSubSettingBuilder fill(Consumer<ChooseMultipleItemSubSettingBuilder> actions) {
		actions.accept(this);
		return this;
	}

	public boolean hasSettings() {
		return !settings.isEmpty();
	}

}
