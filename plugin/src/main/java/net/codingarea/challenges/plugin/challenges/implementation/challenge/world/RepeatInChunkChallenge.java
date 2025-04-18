package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import com.google.common.collect.Lists;
import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.collection.pair.Triple;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.config.document.GsonDocument;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.PlayerIgnoreStatusChangeEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;

@Since("2.1.1")
public class RepeatInChunkChallenge extends Setting {

	private final Map<String, Map<Triple<Integer, Integer, Integer>, BlockData>> changedBlocks = new HashMap<>();
	private final Set<Chunk> updatedChunks = new HashSet<>();

	public RepeatInChunkChallenge() {
		super(MenuType.CHALLENGES);
		setCategory(SettingCategory.WORLD);
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.GRASS_BLOCK, Message.forName("item-repeat-chunk-challenge"));
	}

	@Override
	public void writeGameState(@NotNull Document document) {

		GsonDocument changedDocument = new GsonDocument();

		for (Entry<String, Map<Triple<Integer, Integer, Integer>, BlockData>> entry : changedBlocks.entrySet()) {

			List<Document> list = Lists.newLinkedList();

			for (Entry<Triple<Integer, Integer, Integer>, BlockData> blockEntry : entry.getValue().entrySet()) {
				GsonDocument blockDocument = new GsonDocument();
				Triple<Integer, Integer, Integer> pos = blockEntry.getKey();
				blockDocument.set("x", pos.getFirst());
				blockDocument.set("y", pos.getSecond());
				blockDocument.set("z", pos.getThird());
				blockDocument.set("data", blockEntry.getValue().getAsString());
				list.add(blockDocument);
			}
			if (!list.isEmpty()) {
				changedDocument.set(entry.getKey(), list);
			}
		}

		document.set("changed-blocks", changedDocument);
	}

	@Override
	public void loadGameState(@NotNull Document document) {
		changedBlocks.clear();

		Document changedDocument = document.getDocument("changed-blocks");

		for (World world : Bukkit.getWorlds()) {
			List<Document> list = changedDocument.getDocumentList(world.getName());
			Map<Triple<Integer, Integer, Integer>, BlockData> blockMap = changedBlocks.getOrDefault(world.getName(), new HashMap<>());
			changedBlocks.putIfAbsent(world.getName(), blockMap);

			for (Document blockDocument : list) {

				int x = blockDocument.getInt("x");
				int y = blockDocument.getInt("y");
				int z = blockDocument.getInt("z");

				if (x < 0 || z < 0) {
					Logger.error("RepeatInChunkChallenge: Invalid Change Position: {}, {}, {}, {}", world.getName(), x, y, z);
					continue;
				}

				Triple<Integer, Integer, Integer> pos = new Triple<>(x, y, z);

				String data = blockDocument.getString("data");
				BlockData blockData;
				if (data != null) {
					try {
						blockData = Bukkit.createBlockData(data);
					} catch (IllegalArgumentException exception) {
						Logger.error("RepeatInChunkChallenge: Invalid Change Data: {}, {}, {}, {}, {}", world.getName(), x, y, z, data);
						continue;
					}
				} else {
					Logger.error("RepeatInChunkChallenge: Invalid Change Data: {}, {}, {}, {}, {}", world.getName(), x, y, z, null);
					continue;
				}

				blockMap.put(pos, blockData);
			}

		}

	}

	@Override
	protected void onEnable() {

		for (Player player : ChallengeAPI.getIngamePlayers()) {
			updateSurroundingChunks(player.getLocation().getChunk(), false);
		}
	}

	private void changeBlockInEveryChunk(World world, int x, int y, int z, BlockData data, Block exception) {
		updatedChunks.clear();

		Bukkit.getScheduler().runTask(plugin, () -> {

			Map<Triple<Integer, Integer, Integer>, BlockData> map = changedBlocks
					.getOrDefault(world.getName(), new HashMap<>());
			changedBlocks.putIfAbsent(world.getName(), map);
			map.put(new Triple<>(x, y, z), data);

			for (Player player : ChallengeAPI.getIngamePlayers()) {

				for (Chunk chunk : getSurroundingChunks(player.getLocation().getChunk(), false)) {
					Block block = chunk.getBlock(x, y, z);
					if (!Objects.equals(exception, block)) {
						setBlockData(block, data, true);
					}
				}

			}

		});
	}

	private List<Chunk> getSurroundingChunks(Chunk center, boolean onlyBorder) {
		int range = 8;

		int centerX = center.getX();
		int centerZ = center.getZ();

		List<Chunk> chunks = Lists.newLinkedList();

		for (int x = -range; x <= range; x++) {

			for (int z = -range; z <= range; z++) {

				if (onlyBorder) {

					if (x != -range && x != range && z != -range && z != range) {
						continue;
					}

				}

				Chunk chunkAt = center.getWorld().getChunkAt(centerX + x, centerZ + z);
				if (updatedChunks.contains(chunkAt)) continue;
				chunks.add(chunkAt);
			}
		}

		return chunks;
	}

	private void updateSurroundingChunks(Chunk center, boolean onlyBorder) {

		for (Chunk chunk : getSurroundingChunks(center, onlyBorder)) {
			updateChunk(chunk);
		}

	}

	private void updateChunk(Chunk chunk) {
		updatedChunks.add(chunk);

		Map<Triple<Integer, Integer, Integer>, BlockData> dataMap = changedBlocks.getOrDefault(chunk.getWorld().getName(), new HashMap<>());

		for (Entry<Triple<Integer, Integer, Integer>, BlockData> blockEntry : dataMap.entrySet()) {
			Triple<Integer, Integer, Integer> pos = blockEntry.getKey();
			BlockData blockData = blockEntry.getValue();
			Block block = chunk.getBlock(pos.getFirst(), pos.getSecond(), pos.getThird());
			if (!block.getBlockData().matches(blockData)) {
				setBlockData(block, blockData, false);
			}

		}


	}

	private void setBlockData(Block block, BlockData blockData, boolean update) {
		if (block.getType() == Material.END_PORTAL ||
				block.getType() == Material.END_PORTAL_FRAME ||
				block.getType() == Material.END_GATEWAY) return;
		block.setBlockData(blockData, update);
	}

	private int getRelativeChunkCoordinate(int worldCoordinate) {
		return worldCoordinate & 0xF;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;

		Block block = event.getBlock();
		changeBlockInEveryChunk(
				event.getBlock().getWorld(),
				getRelativeChunkCoordinate(block.getX()),
				block.getY(),
				getRelativeChunkCoordinate(block.getZ()),
				block.getBlockData(),
				event.getBlock()
		);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;

		Block block = event.getBlock();
		changeBlockInEveryChunk(
				event.getBlock().getWorld(),
				getRelativeChunkCoordinate(block.getX()),
				block.getY(),
				getRelativeChunkCoordinate(block.getZ()),
				Bukkit.createBlockData(Material.AIR),
				event.getBlock()
		);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (event.getTo() == null) return;
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (event.getTo().getChunk() == event.getFrom().getChunk()) return;
		updateSurroundingChunks(event.getTo().getChunk(), true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(VehicleMoveEvent event) {
		if (!shouldExecuteEffect()) return;

		boolean anyMatch = false;
		for (Entity passenger : event.getVehicle().getPassengers()) {
			if (passenger instanceof Player) {
				anyMatch = true;
				break;
			}
		}
		if (!anyMatch) return;

		if (event.getTo().getChunk() == event.getFrom().getChunk()) return;
		updateSurroundingChunks(event.getTo().getChunk(), true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent event) {
		if (event.getTo() == null) return;
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (event.getTo().getChunk() == event.getFrom().getChunk()) return;
		updateSurroundingChunks(event.getTo().getChunk(), false);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onGamemodeChange(PlayerIgnoreStatusChangeEvent event) {
		if (!shouldExecuteEffect()) return;
		if (event.isNotIgnored()) {
			updateSurroundingChunks(event.getPlayer().getLocation().getChunk(), false);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChunkUnload(ChunkUnloadEvent event) {
		if (!shouldExecuteEffect()) return;
		updatedChunks.remove(event.getChunk());
	}

}
