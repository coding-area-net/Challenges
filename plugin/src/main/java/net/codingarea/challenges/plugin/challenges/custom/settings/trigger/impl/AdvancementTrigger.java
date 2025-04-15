package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class AdvancementTrigger extends ChallengeTrigger {

	public AdvancementTrigger(String name) {
		super(name);
	}

	@Override
	public Material getMaterial() {
		return Material.KNOWLEDGE_BOOK;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPickup(PlayerAdvancementDoneEvent event) {
		if (event.getAdvancement().getKey().toString().contains(":recipes/")) return;
		createData().entity(event.getPlayer()).execute();
	}

}
