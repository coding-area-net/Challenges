package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ListBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ChunkDeconstructionChallenge extends TimedChallenge {

	public ChunkDeconstructionChallenge() {
		super(MenuType.CHALLENGES, 1, 60, 20);
		setCategory(SettingCategory.WORLD);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.DIAMOND_PICKAXE, Message.forName("item-chunk-deconstruction-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-time-seconds-description").asArray(getValue());
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeSecondsValueChangeTitle(this, getValue());
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return getValue();
	}

	@Override
	protected void onTimeActivation() {
		if (ChallengeAPI.isWorldInUse()) return;

		for (Chunk chunk : getChunksToDeconstruct()) {
			if (!chunk.isLoaded()) continue;
			deconstructChunk(chunk);
		}

		restartTimer();
	}

	private void deconstructChunk(@Nonnull Chunk chunk) {

		for (int x = 0; x < 16; x++) {
			int finalX = x;
			Bukkit.getScheduler().runTask(plugin, () -> {
				for (int z = 0; z < 16; z++) {
					deconstructAtLocation(chunk.getBlock(finalX, 0, z));
				}
			});
		}

	}

	private void deconstructAtLocation(@Nonnull Block block) {
		Block lowestBreakableBlock = getLowestBreakableBlock(block);
		if (lowestBreakableBlock == null) return;

		lowestBreakableBlock.setType(Material.AIR, true);
	}

	@Nullable
	private Block getLowestBreakableBlock(@Nonnull Block block) {
		Location location = block.getLocation();
		location.setY(block.getWorld().getMaxHeight());

		while (location.getY() >= BukkitReflectionUtils.getMinHeight(block.getWorld()) && !isBreakable(location.getBlock())) {
			location.subtract(0, 1, 0);
		}

		Block currentBlock = location.getBlock();
		return currentBlock.getType() == Material.BEDROCK || currentBlock.isLiquid() ? null : location.getBlock();
	}

	private boolean isBreakable(@Nonnull Block block) {
		return block.getType() != Material.BEDROCK && !BukkitReflectionUtils.isAir(block.getType()) && !block.isLiquid();
	}

	private List<Chunk> getChunksToDeconstruct() {
		return new ListBuilder<Chunk>().fill(builder ->
				Bukkit.getOnlinePlayers().forEach(player -> {
					if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE)
						return;
					builder.addIfNotContains(player.getLocation().getChunk());
				})
		).build();
	}

}
