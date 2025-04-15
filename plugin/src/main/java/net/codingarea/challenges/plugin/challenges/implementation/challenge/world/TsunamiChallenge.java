package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ListBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @autho<a href="r">KxmischesDomi | https://github</a>.com/kxmischesdomi
 * @since 2.0
 */
@Since("2.0")
public class TsunamiChallenge extends TimedChallenge {

	public static final int RANGE = 5;

	private final List<Chunk> floodedChunks = new ArrayList<>();

	private int waterHeight = Integer.MAX_VALUE,
			lavaHeight = 0;

	public TsunamiChallenge() {
		super(MenuType.CHALLENGES, 1, 40, 4);
		setCategory(SettingCategory.WORLD);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.ICE, Message.forName("item-tsunami-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-time-seconds-description").asArray(getValue() * 15);
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeSecondsValueChangeTitle(this, getValue() * 15);
	}

	@Override
	protected void onEnable() {

		if (waterHeight == Integer.MAX_VALUE) {
			waterHeight = BukkitReflectionUtils.getMinHeight(ChallengeAPI.getGameWorld(Environment.NORMAL));
		}
		bossbar.setContent((bossbar, player) -> {
			World world = player.getWorld();
			Environment environment = world.getEnvironment();
			int height = environment == Environment.NORMAL ? waterHeight : lavaHeight;
			if (height < (world.getMaxHeight() - 1))
				bossbar.setProgress(getProgress());

			if (environment == Environment.NORMAL) {
				bossbar.setColor(BarColor.BLUE);
				bossbar.setTitle(Message.forName("bossbar-tsunami-water").asString(waterHeight));
			} else if (environment == Environment.NETHER) {
				bossbar.setColor(BarColor.RED);
				bossbar.setTitle(Message.forName("bossbar-tsunami-lava").asString(lavaHeight));
			} else {
				bossbar.setVisible(false);
			}
		});
		bossbar.show();
	}

	@Override
	protected void onDisable() {
		bossbar.hide();
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return getValue() * 15;
	}

	@Override
	protected void handleCountdown() {
		bossbar.update();
	}

	@Override
	protected void onTimeActivation() {
		restartTimer();

		for (World world : ChallengeAPI.getGameWorlds()) {
			if (!world.getPlayers().isEmpty()) {
				if (world.getEnvironment() == Environment.NORMAL) {
					if (waterHeight >= (world.getMaxHeight() - 1)) continue;
					waterHeight++;
				} else if (world.getEnvironment() == Environment.NETHER) {
					if (lavaHeight >= (world.getMaxHeight() - 1)) continue;
					lavaHeight++;
				}
			}
		}

		List<Chunk> previouslyFlooded = new ArrayList<>(floodedChunks);
		bossbar.update();
		floodedChunks.clear();
		for (Chunk chunk : getChunksToFlood()) {
			floodChunk(chunk, !previouslyFlooded.contains(chunk));
		}

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerMove(@Nonnull PlayerMoveEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (event.getTo() == null) return;

		Chunk newChunk = event.getTo().getChunk();
		if (event.getFrom().getChunk() == newChunk) return;

		for (Chunk chunk : getChunksAroundChunk(newChunk)) {
			floodChunk(chunk, true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTeleport(@Nonnull PlayerTeleportEvent event) {
		if (ignorePlayer(event.getPlayer())) return;
		if (!shouldExecuteEffect()) return;
		if (event.getTo() == null) return;

		Chunk newChunk = event.getTo().getChunk();
		if (event.getFrom().getChunk() == newChunk) return;

		bossbar.update();

		for (Chunk chunk : getChunksAroundChunk(newChunk)) {
			floodChunk(chunk, true);
		}
	}

	@EventHandler
	public void onChunkUnload(@Nonnull ChunkUnloadEvent event) {
		floodedChunks.remove(event.getChunk());
	}

	private void floodChunk(@Nonnull Chunk chunk, boolean discovered) {
		if (floodedChunks.contains(chunk)) return;
		floodedChunks.add(chunk);
		if (chunk.getWorld().getEnvironment() == Environment.THE_END) return;

		boolean overworld = chunk.getWorld().getEnvironment() == Environment.NORMAL;
		int height = overworld ? waterHeight : lavaHeight;
		floodChunk0(chunk, discovered ? null : height - 1, height, overworld, (delay, task) -> Bukkit.getScheduler().runTaskLater(plugin, task, delay));
	}

	private void floodChunk0(@Nonnull Chunk chunk, @Nullable Integer givenStartAt, int height, boolean overworld, @Nonnull BiConsumer<Integer, Runnable> executor) {
		int startAt = givenStartAt != null ? Math.max(BukkitReflectionUtils.getMinHeight(chunk.getWorld()) + 1, givenStartAt) : BukkitReflectionUtils
				.getMinHeight(chunk.getWorld()) + 1;
		Map<Integer, List<Block>> blocksByDelay = new HashMap<>();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = startAt + 1; y <= height; y++) {
						List<Block> blocks = blocksByDelay.computeIfAbsent(0, key -> new ArrayList<>(16));
						Block block = chunk.getBlock(x, y, z);
						Material type = block.getType();
						if (type != Material.WATER && type != Material.LAVA && block.isPassable() || (overworld && type == Material.LAVA))
							blocks.add(block);
					}
				}
			}
			blocksByDelay.forEach((delay, blocks) -> {
				executor.accept(delay, () -> {
					for (Block block : blocks) {
						block.setType(overworld ? Material.WATER : Material.LAVA, false);
					}
				});
			});
		});
	}

	private List<Chunk> getChunksToFlood() {
		return new ListBuilder<Chunk>().fill(chunkListBuilder -> {
			broadcastFiltered(player -> chunkListBuilder.addAllIfNotContains(getChunksAroundChunk(player.getLocation().getChunk())));
		}).build();
	}

	private List<Chunk> getChunksAroundChunk(@Nonnull Chunk origin) {
		return new ListBuilder<Chunk>().fill(builder -> {
			for (int x = -RANGE; x <= RANGE; x++) {
				for (int z = -RANGE; z <= RANGE; z++) {
					builder.addIfNotContains(origin.getWorld().getChunkAt(origin.getX() + x, origin.getZ() + z));
				}
			}
		}).build();
	}

	@Override
	public void writeGameState(@Nonnull Document document) {
		super.writeGameState(document);
		document.set("waterHeight", waterHeight);
		document.set("lavaHeight", lavaHeight);
	}

	@Override
	public void loadGameState(@Nonnull Document document) {
		super.loadGameState(document);
		waterHeight = document.getInt("waterHeight");
		lavaHeight = document.getInt("lavaHeight");
	}

}