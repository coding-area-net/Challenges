package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class InLiquidTrigger extends ChallengeTrigger {

	public InLiquidTrigger(String name) {
		super(name, SubSettingsBuilder.createChooseMultipleItem(SubSettingsHelper.LIQUID).fill(builder -> {
			builder.addSetting("LAVA", new ItemBuilder(Material.LAVA_BUCKET, DefaultItem.getItemPrefix() + "§cLava"));
			builder.addSetting("WATER", new ItemBuilder(Material.WATER_BUCKET, DefaultItem.getItemPrefix() + "§9Water"));
		}));
		Challenges.getInstance().getScheduler().register(this);
	}

	@Override
	public Material getMaterial() {
		return Material.BUCKET;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (BlockUtils.isSameBlockLocation(event.getTo(), event.getFrom())) return;
		if (event.getTo() == null) return;

		Material type = event.getTo().getBlock().getType();
		Material oldType = event.getFrom().getBlock().getType();
		if ((type == Material.WATER ||
				type == Material.LAVA) &&
				oldType != Material.WATER &&
				oldType != Material.LAVA) {
			createData()
					.entity(event.getPlayer())
					.data(SubSettingsHelper.LIQUID, type.name())
					.execute();
		}

	}

	@ScheduledTask(ticks = 5, async = false)
	public void onFifthTick() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			Material type = player.getLocation().getBlock().getType();
			if (type == Material.WATER ||
					type == Material.LAVA) {
				createData()
						.entity(player)
						.data("liquid", type.name())
						.execute();
			}
		}

	}

}
