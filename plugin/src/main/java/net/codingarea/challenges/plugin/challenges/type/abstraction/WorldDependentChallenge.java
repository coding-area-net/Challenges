package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.server.WorldManager.WorldSettings;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class WorldDependentChallenge extends TimedChallenge {

	private boolean inExtraWorld;
	private BiConsumer<Player, Integer> lastTeleport;
	private int teleportIndex;

	public WorldDependentChallenge(@Nonnull MenuType menu) {
		super(menu);
	}

	public WorldDependentChallenge(@Nonnull MenuType menu, int max) {
		super(menu, max);
	}

	public WorldDependentChallenge(@Nonnull MenuType menu, int min, int max) {
		super(menu, min, max);
	}

	public WorldDependentChallenge(@Nonnull MenuType menu, int min, int max, int defaultValue) {
		super(menu, min, max, defaultValue);
	}

	public WorldDependentChallenge(@Nonnull MenuType menu, boolean runAsync) {
		super(menu, runAsync);
	}

	public WorldDependentChallenge(@Nonnull MenuType menu, int max, boolean runAsync) {
		super(menu, max, runAsync);
	}

	public WorldDependentChallenge(@Nonnull MenuType menu, int min, int max, boolean runAsync) {
		super(menu, min, max, runAsync);
	}

	public WorldDependentChallenge(@Nonnull MenuType menu, int min, int max, int defaultValue, boolean runAsync) {
		super(menu, min, max, defaultValue, runAsync);
	}

	/**
	 * Prevents the activation of two world challenges at the same time
	 */
	@Override
	protected final void onTimeActivation() {
		if (ChallengeAPI.isWorldInUse()) {
			restartTimer(1);
		} else {
			startWorldChallenge();
		}
	}

	public abstract void startWorldChallenge();

	@Override
	protected boolean getTimerTrigger() {
		return inExtraWorld || !Challenges.getInstance().getWorldManager().isWorldInUse();
	}

	protected void teleportToWorld(boolean allowJoinCatchUp, @Nonnull BiConsumer<Player, Integer> action) {
		if (Challenges.getInstance().getWorldManager().isWorldInUse()) return;
		Challenges.getInstance().getWorldManager().setWorldInUse(inExtraWorld = true);
		lastTeleport = allowJoinCatchUp ? action : null;

		teleportIndex = 0;
		broadcastFiltered(player -> teleport(player, action));
		broadcastIgnored(player -> {
			teleport(player, null);
			teleportSpectator(player);
		});
	}

	protected void teleportBack() {
		if (!Challenges.getInstance().getWorldManager().isWorldInUse()) return;
		Challenges.getInstance().getWorldManager().setWorldInUse(inExtraWorld = false);
		lastTeleport = null;
		teleportIndex = 0;
	}

	protected void teleportBack(@Nonnull Player player) {
		Challenges.getInstance().getWorldManager().restorePlayerData(player);
	}

	private void teleport(@Nonnull Player player, @Nullable BiConsumer<Player, Integer> teleport) {
		player.getInventory().clear();
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.setNoDamageTicks(10);
		player.setFallDistance(0);
		player.setHealth(player.getMaxHealth());
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		if (teleport != null) {
			player.setVelocity(new Vector());
			teleport.accept(player, teleportIndex++);
			SoundSample.TELEPORT.play(player);
		}
	}

	protected void teleportSpectator(@Nonnull Player player) {
		player.setGameMode(GameMode.SPECTATOR);
		List<Player> ingamePlayers = ChallengeHelper.getIngamePlayers();
		if (ingamePlayers.isEmpty()) return;
		Player target = ingamePlayers.get(0);
		player.teleport(target);
		player.setSpectatorTarget(target);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(@Nonnull PlayerJoinEvent event) {
		if (isInExtraWorld()) {
			if (lastTeleport == null) return;
			if (Challenges.getInstance().getWorldManager().hasPlayerData(event.getPlayer())) return;
			teleport(event.getPlayer(), lastTeleport);
		} else {
			Challenges.getInstance().getWorldManager().restorePlayerData(event.getPlayer());
		}
	}

	@Nonnull
	protected final World getExtraWorld() {
		return Challenges.getInstance().getWorldManager().getExtraWorld();
	}

	@Nonnull
	protected final WorldSettings getExtraWorldSettings() {
		return Challenges.getInstance().getWorldManager().getSettings();
	}

	public final boolean isInExtraWorld() {
		return inExtraWorld;
	}

}
