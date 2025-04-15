package net.codingarea.challenges.plugin.management.server;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.misc.StringUtils;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 1.0
 */
public final class ServerManager {

	private final boolean setSpectatorOnWin;
	private final boolean dropItemsOnEnd;
	private final boolean winSounds;

	private boolean isFresh; // This indicated if the timer was never started before
	private boolean hasCheated;

	public ServerManager() {
		Document sessionConfig = Challenges.getInstance().getConfigManager().getSessionConfig();
		hasCheated = sessionConfig.getBoolean("cheated");
		isFresh = sessionConfig.getBoolean("fresh", true);

		Document pluginConfig = Challenges.getInstance().getConfigDocument();
		setSpectatorOnWin = pluginConfig.getBoolean("set-spectator-on-win");
		dropItemsOnEnd = pluginConfig.getBoolean("drop-items-on-end");
		winSounds = pluginConfig.getBoolean("enabled-win-sounds");
	}

	public void setNotFresh() {
		isFresh = false;
		Challenges.getInstance().getConfigManager().getSessionConfig().set("fresh", false);
	}

	public void setHasCheated() {
		hasCheated = true;
		Challenges.getInstance().getConfigManager().getSessionConfig().set("cheated", true);
	}

	public boolean isFresh() {
		return isFresh;
	}

	public boolean hasCheated() {
		return hasCheated;
	}

	public void endChallenge(@Nonnull ChallengeEndCause endCause, Supplier<List<Player>> winnerGetter) {
		if (ChallengeAPI.isPaused()) {
			Logger.warn("Tried to end challenge while timer was paused");
			return;
		}

		IGoal currentGoal = Challenges.getInstance().getChallengeManager().getCurrentGoal();
		List<Player> winners = new LinkedList<>();
		if (winnerGetter != null) {
			winners = winnerGetter.get();
		} else if (currentGoal != null && endCause.isWinnable()) {
			currentGoal.getWinnersOnEnd(winners);
		}

		if (endCause != ChallengeEndCause.GOAL_REACHED || setSpectatorOnWin) {
			setSpectator();
		}
		if (endCause == ChallengeEndCause.GOAL_REACHED && winSounds && currentGoal != null && currentGoal.getWinSound() != null) {
			currentGoal.getWinSound().broadcast();
		}
		if (dropItemsOnEnd) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (winners.isEmpty() || winners.contains(player)) continue;
				dropItems(player);
			}
		}

		Challenges.getInstance().getChallengeTimer().pause(false);

		String winnerString = StringUtils.getIterableAsString(winners, "§7, ", player -> "§e§l" + NameHelper.getName(player));
		String time = Challenges.getInstance().getChallengeTimer().getFormattedTime();
		String seed = Bukkit.getWorlds().isEmpty() ? "?" :
				String.valueOf(ChallengeAPI.getGameWorld(Environment.NORMAL).getSeed());
		endCause.getMessage(!winners.isEmpty()).broadcast(Prefix.CHALLENGES, time, winnerString, seed);

	}

	private void setSpectator() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.setGameMode(GameMode.SPECTATOR);
			SoundSample.BLAST.play(player);

			try {
				player.getWorld().spawnEntity(player.getLocation(), MinecraftNameWrapper.FIREWORK);
			} catch (IllegalArgumentException ex) {
				// We cant spawn fireworks like that in some versions of spigot
			}
		}
	}

	private void dropItems(@Nonnull Player player) {
		dropItems(player.getLocation(), player.getInventory().getContents());
		player.getInventory().clear();
	}

	private void dropItems(@Nonnull Location location, @Nonnull ItemStack[] items) {
		for (ItemStack item : items) {
			if (item == null) continue;
			if (BukkitReflectionUtils.isAir(item.getType())) continue;
			if (location.getWorld() == null) return;
			location.getWorld().dropItem(location, item);
		}
	}

}
