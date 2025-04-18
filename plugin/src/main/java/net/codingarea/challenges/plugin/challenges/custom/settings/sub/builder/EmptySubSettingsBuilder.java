package net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder;

import com.google.common.collect.Lists;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.IParentCustomGenerator;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class EmptySubSettingsBuilder extends SubSettingsBuilder {

	public EmptySubSettingsBuilder() {
		super("none");
	}

	@Override
	public List<String> getDisplay(Map<String, String[]> activated) {
		return Lists.newLinkedList();
	}

	@Override
	public boolean hasSettings() {
		return false;
	}

	@Override
	public boolean open(Player player, IParentCustomGenerator parentGenerator, String title) {
		return false;
	}

}
