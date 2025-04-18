package net.codingarea.challenges.plugin;

import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.content.loader.ContentLoader;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ChallengeAPI {

	private ChallengeAPI() {
	}

	public static boolean isStarted() {
		return Challenges.getInstance().getChallengeTimer().isStarted();
	}

	public static boolean isPaused() {
		return Challenges.getInstance().getChallengeTimer().isPaused();
	}

	public static TimerStatus getTimerStatus() {
		return Challenges.getInstance().getChallengeTimer().getStatus();
	}

	public static void pauseTimer() {
		Challenges.getInstance().getChallengeTimer().pause(true);
	}

	public static void pauseTimer(boolean playInGameEffects) {
		Challenges.getInstance().getChallengeTimer().pause(playInGameEffects);
	}

	public static void resumeTimer() {
		Challenges.getInstance().getChallengeTimer().resume();
	}

	public static void resetTimer() {
		Challenges.getInstance().getChallengeTimer().reset();
	}

	public static void endChallenge(@Nonnull ChallengeEndCause endCause) {
		Challenges.getInstance().getServerManager().endChallenge(endCause, null);
	}

	public static void endChallenge(@Nonnull ChallengeEndCause endCause, Supplier<List<Player>> winnerGetter) {
		Challenges.getInstance().getServerManager().endChallenge(endCause, winnerGetter);
	}


	public static boolean isWorldInUse() {
		return Challenges.getInstance().getWorldManager().isWorldInUse();
	}

	public static boolean isFresh() {
		return Challenges.getInstance().getServerManager().isFresh();
	}

	public static void registerScheduler(@Nonnull Object... scheduler) {
		Challenges.getInstance().getScheduler().register(scheduler);
	}

	public static void subscribeLoader(@Nonnull Class<? extends ContentLoader> classOfLoader, @Nonnull Runnable action) {
		Challenges.getInstance().getLoaderRegistry().subscribe(classOfLoader, action);
	}

	@Nonnull
	public static List<Material> getCustomDrops(@Nonnull Material block) {
		return Challenges.getInstance().getBlockDropManager().getCustomDrops(block);
	}

	public static boolean getDropChance(@Nonnull Material block) {
		return Challenges.getInstance().getBlockDropManager().getDropChance(block).getAsBoolean();
	}

	public static boolean getItemsDirectIntoInventory() {
		return Challenges.getInstance().getBlockDropManager().isItemsDirectIntoInventory();
	}

	@Nonnull
	public static String formatTime(long seconds) {
		return Challenges.getInstance().getChallengeTimer().getFormat().format(seconds);
	}

	/**
	 * @return all players that aren't ignored by the plugin
	 */
	@Nonnull
	public static List<Player> getIngamePlayers() {
		List<Player> list = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!AbstractChallenge.ignorePlayer(player)) {
				list.add(player);
			}
		}
		return list;
	}

	public static List<World> getGameWorlds() {
		return Challenges.getInstance().getGameWorldStorage().getGameWorlds();
	}

	public static World getGameWorld(@Nullable Environment environment) {
		return Challenges.getInstance().getGameWorldStorage().getWorld(environment);
	}

	public static boolean isPlayerInGameWorld(@Nonnull Environment environment) {
		World world = getGameWorld(environment);
		for (Player player : world.getPlayers()) {
			if (!AbstractChallenge.ignorePlayer(player)) {
				return true;
			}
		}
		return false;
	}

}
