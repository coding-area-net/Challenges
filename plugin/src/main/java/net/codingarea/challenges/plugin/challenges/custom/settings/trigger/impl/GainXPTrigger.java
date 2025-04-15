package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class GainXPTrigger extends ChallengeTrigger {

	public GainXPTrigger(String name) {
		super(name);
	}

	@Override
	public Material getMaterial() {
		return Material.EXPERIENCE_BOTTLE;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onXPGain(PlayerExpChangeEvent event) {
		if (event.getAmount() > 0) {
			createData()
					.entity(event.getPlayer())
					.cancelAction(() -> event.setAmount(0))
					.execute();
		}
	}

}
