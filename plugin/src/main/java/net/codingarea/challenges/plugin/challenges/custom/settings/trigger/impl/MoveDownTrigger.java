package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class MoveDownTrigger extends ChallengeTrigger {

	public MoveDownTrigger(String name) {
		super(name);
	}

	@Override
	public Material getMaterial() {
		return Material.NETHER_BRICK_STAIRS;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (event.getTo() == null) return;
		if (event.getTo().getBlockY() < event.getFrom().getBlockY()) {
			createData()
					.entity(event.getPlayer())
					.event(event)
					.execute();
		}

	}

}
