package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class StandsOnSpecificBlockTrigger extends ChallengeTrigger {

	public StandsOnSpecificBlockTrigger(String name) {
		super(name, SubSettingsHelper.createBlockSettingsBuilder());
	}

	@Override
	public Material getMaterial() {
		return Material.PACKED_ICE;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (BlockUtils.isSameBlockLocation(event.getTo(), event.getFrom())) return;
		Block blockBelow = BlockUtils.getBlockBelow(
				event.getTo());
		if (blockBelow == null) return;

		createData()
				.entity(event.getPlayer())
				.event(event)
				.block(blockBelow.getType())
				.execute();
	}

}
