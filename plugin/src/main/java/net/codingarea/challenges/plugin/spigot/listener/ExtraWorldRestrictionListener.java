package net.codingarea.challenges.plugin.spigot.listener;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import javax.annotation.Nonnull;

public class ExtraWorldRestrictionListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(@Nonnull BlockPlaceEvent event) {
		if (!isInExtraWorld(event.getBlock().getLocation())) return;
		if (Challenges.getInstance().getWorldManager().getSettings().isPlaceBlocks()) return;

		event.setBuild(false);
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(@Nonnull BlockBreakEvent event) {
		if (!isInExtraWorld(event.getBlock().getLocation())) return;
		if (Challenges.getInstance().getWorldManager().getSettings().isDestroyBlocks()) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onDrop(@Nonnull PlayerDropItemEvent event) {
		if (!isInExtraWorld(event.getPlayer().getWorld())) return;
		if (Challenges.getInstance().getWorldManager().getSettings().isDropItems()) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPickUp(@Nonnull PlayerPickupItemEvent event) {
		if (!isInExtraWorld(event.getPlayer().getWorld())) return;
		if (Challenges.getInstance().getWorldManager().getSettings().isPickupItems()) return;

		event.setCancelled(true);
	}

	private boolean isInExtraWorld(@Nonnull Location location) {
		if (location.getWorld() == null) return false;
		return isInExtraWorld(location.getWorld());
	}

	private boolean isInExtraWorld(@Nonnull World world) {
		return Challenges.getInstance().getWorldManager().getExtraWorld().equals(world);
	}

}
