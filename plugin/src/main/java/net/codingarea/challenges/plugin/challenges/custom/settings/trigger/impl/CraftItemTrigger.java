package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class CraftItemTrigger extends ChallengeTrigger {

	public CraftItemTrigger(String name) {
		super(name);
	}

	@Override
	public Material getMaterial() {
		return Material.CRAFTING_TABLE;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCraft(CraftItemEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		createData()
				.entity(player)
				.event(event)
				.execute();
	}

}
