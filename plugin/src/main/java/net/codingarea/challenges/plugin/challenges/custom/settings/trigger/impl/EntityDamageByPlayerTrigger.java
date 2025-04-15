package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.spigot.events.EntityDamageByPlayerEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class EntityDamageByPlayerTrigger extends ChallengeTrigger {

	public EntityDamageByPlayerTrigger(String name) {
		super(name, SubSettingsHelper.createEntityTypeSettingsBuilder(true, true));
	}

	@Override
	public Material getMaterial() {
		return Material.WOODEN_SWORD;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDeath(@Nonnull EntityDamageByPlayerEvent event) {

		createData()
				.entity(event.getDamager())
				.event(event)
				.entityType(event.getEntityType())
				.execute();

	}

}
