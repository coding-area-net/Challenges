package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class NetherPortalSpawnSetting extends OneEnabledSetting {

	private static final String id = "netherportal_handle";

	private final Map<Location, Location> netherPortalsByOverworldPortals = new HashMap<>();
	private final StructureType structureType;
	private final Collection<Material> groundMaterial;
	private final String unableToFindMessage;

	public NetherPortalSpawnSetting(@Nonnull MenuType menu, @Nonnull StructureType structureType, @Nonnull String unableToFindMessage, @Nonnull Material... groundMaterial) {
		super(menu, id);
		this.structureType = structureType;
		this.unableToFindMessage = unableToFindMessage;
		this.groundMaterial = Arrays.asList(groundMaterial);
	}

	public NetherPortalSpawnSetting(@Nonnull MenuType menu, @Nonnull StructureType structureType, @Nonnull String unableToFindMessage, @Nonnull Collection<Material> groundMaterial) {
		super(menu, id);
		this.structureType = structureType;
		this.unableToFindMessage = unableToFindMessage;
		this.groundMaterial = groundMaterial;
	}

	@EventHandler
	public void onNetherPortal(@Nonnull PlayerTeleportEvent event) {
		if (!isEnabled()) return;
		if (event.getCause() != TeleportCause.NETHER_PORTAL) return;
		if (event.getTo() == null) return;
		if (event.getTo().getWorld() == null) return;

		if (event.getTo().getWorld().getEnvironment() == Environment.NETHER) {

			World world = event.getTo().getWorld();
			Location location = getNetherPortal(event.getFrom(), world);
			if (location == null) {
				Message.forName(unableToFindMessage).send(event.getPlayer(), Prefix.CHALLENGES);
				return;
			}

			buildPortal(location);
			event.setTo(location.clone().add(1, 0, 0.5)); // Middle of portal

		} else if (event.getTo().getWorld().getEnvironment() == Environment.NORMAL) {
			Location location = getOverworldPortal(event.getFrom());
			if (location == null) return;
			event.setTo(location.clone().add(1, 0, 0.5)); // Middle of portal
		}
	}

	@Nullable
	private Location getNetherPortal(@Nonnull Location overworldPortal, @Nonnull World nether) {

		// Look if the current portal was used before
		for (Entry<Location, Location> entry : netherPortalsByOverworldPortals.entrySet()) {
			if (overworldPortal.distance(entry.getKey()) < 10)
				return entry.getValue();
		}

		// Find next structure
		Location structure = nether.locateNearestStructure(new Location(nether, 0, 100, 0), structureType, 10000, false);
		if (structure == null) return null;

		// Look if there's already a portal to this structure
		for (Entry<Location, Location> entry : netherPortalsByOverworldPortals.entrySet()) {
			if (structure.distance(entry.getValue()) < 100)
				return entry.getValue();
		}

		Location target = findNearestStructurePart(structure.clone());
		netherPortalsByOverworldPortals.put(overworldPortal, target);
		return target;

	}

	@Nullable
	private Location getOverworldPortal(@Nonnull Location netherPortal) {
		for (Entry<Location, Location> entry : netherPortalsByOverworldPortals.entrySet()) {
			if (netherPortal.distance(entry.getValue()) < 100)
				return entry.getKey();
		}
		return null;
	}


	@Nullable
	private Location findNearestStructurePart(@Nonnull Location structure) {

		Chunk chunk = structure.getChunk();
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {

				Location location = chunk.getBlock(x, 100, z).getLocation(); // Structures wont be above y=100
				while (location.getBlockY() > 30) { // Structures wont be below y=30
					location.subtract(0, 1, 0);
					if (groundMaterial.contains(location.getBlock().getType()))
						return location.add(0, 1, 0);
				}

			}
		}

		return null;

	}

	private void buildPortal(@Nonnull Location origin) {

		// Floor
		origin.clone().add(-1, -1, 0).getBlock().setType(Material.OBSIDIAN);
		origin.clone().add(0, -1, 0).getBlock().setType(Material.OBSIDIAN);
		origin.clone().add(1, -1, 0).getBlock().setType(Material.OBSIDIAN);
		origin.clone().add(2, -1, 0).getBlock().setType(Material.OBSIDIAN);

		// Above
		origin.clone().add(-1, 3, 0).getBlock().setType(Material.OBSIDIAN);
		origin.clone().add(0, 3, 0).getBlock().setType(Material.OBSIDIAN);
		origin.clone().add(1, 3, 0).getBlock().setType(Material.OBSIDIAN);
		origin.clone().add(2, 3, 0).getBlock().setType(Material.OBSIDIAN);

		// Wall left
		origin.clone().add(-1, 0, 0).getBlock().setType(Material.OBSIDIAN);
		origin.clone().add(-1, 1, 0).getBlock().setType(Material.OBSIDIAN);
		origin.clone().add(-1, 2, 0).getBlock().setType(Material.OBSIDIAN);

		// Wall right
		origin.clone().add(2, 0, 0).getBlock().setType(Material.OBSIDIAN);
		origin.clone().add(2, 1, 0).getBlock().setType(Material.OBSIDIAN);
		origin.clone().add(2, 2, 0).getBlock().setType(Material.OBSIDIAN);

		// Fill portal
		origin.clone().add(0, 0, 0).getBlock().setType(Material.NETHER_PORTAL);
		origin.clone().add(1, 0, 0).getBlock().setType(Material.NETHER_PORTAL);
		origin.clone().add(0, 1, 0).getBlock().setType(Material.NETHER_PORTAL);
		origin.clone().add(1, 1, 0).getBlock().setType(Material.NETHER_PORTAL);
		origin.clone().add(0, 2, 0).getBlock().setType(Material.NETHER_PORTAL);
		origin.clone().add(1, 2, 0).getBlock().setType(Material.NETHER_PORTAL);

		// Free some place around
		for (int x = -1; x <= 2; x++) {
			for (int y = 0; y <= 3; y++) {
				origin.clone().add(x, y, 1).getBlock().setType(Material.AIR);
				origin.clone().add(x, y, 2).getBlock().setType(Material.AIR);
				origin.clone().add(x, y, -1).getBlock().setType(Material.AIR);
				origin.clone().add(x, y, -2).getBlock().setType(Material.AIR);
			}
		}

	}

	@Override
	public void loadGameState(@Nonnull Document document) {
		super.loadGameState(document);

		Document portals = document.getDocument("portals");
		for (String key : portals.keys()) {
			Document portal = portals.getDocument(key);
			Location from = portal.getSerializable("portal", Location.class);
			Location to = portal.getSerializable("target", Location.class);

			if (from == null || to == null) continue;
			netherPortalsByOverworldPortals.put(from, to);
		}
	}

	@Override
	public void writeGameState(@Nonnull Document document) {
		super.writeGameState(document);

		int index = 0;
		Document portals = document.getDocument("portals");
		for (Entry<Location, Location> entry : netherPortalsByOverworldPortals.entrySet()) {
			Document portal = portals.getDocument(String.valueOf(index));
			portal.set("portal", entry.getKey());
			portal.set("target", entry.getValue());
		}
	}

}
