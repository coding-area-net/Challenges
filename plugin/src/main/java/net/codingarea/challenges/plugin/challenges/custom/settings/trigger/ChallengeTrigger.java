package net.codingarea.challenges.plugin.challenges.custom.settings.trigger;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

public abstract class ChallengeTrigger extends ChallengeSetting implements
		IChallengeTrigger {

	public ChallengeTrigger(String name,
							SubSettingsBuilder subSettingsBuilder) {
		super(name, subSettingsBuilder);
	}

	public ChallengeTrigger(String name) {
		super(name);
	}

	public ChallengeTrigger(String name, Supplier<SubSettingsBuilder> builderSupplier) {
		super(name, builderSupplier);
	}

	public static LinkedHashMap<String, ItemStack> getMenuItems() {
		LinkedHashMap<String, ItemStack> map = new LinkedHashMap<>();

		for (ChallengeTrigger value : Challenges.getInstance().getCustomSettingsLoader().getTriggers().values()) {
			map.put(value.getName(), new ItemBuilder(value.getMaterial(), Message.forName(value.getMessage())).hideAttributes().build());
		}

		return map;
	}

	@Override
	public final String getMessage() {
		return "item-custom-trigger-" + getMessageSuffix();
	}

}
