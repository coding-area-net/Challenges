package net.codingarea.challenges.plugin.management.server;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeBossBar;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public final class ScoreboardManager {

	private final List<ChallengeBossBar> bossbars = new ArrayList<>();
	private ChallengeScoreboard currentScoreboard;

	public ScoreboardManager() {
		ChallengeAPI.subscribeLoader(LanguageLoader.class, this::updateAll);
		ChallengeAPI.registerScheduler(this);
	}

	public void handleQuit(@Nonnull Player player) {
		for (ChallengeBossBar bossbar : bossbars) {
			bossbar.applyHide(player);
		}
		if (currentScoreboard != null) {
			currentScoreboard.applyHide(player);
			Bukkit.getScheduler().runTaskLaterAsynchronously(Challenges.getInstance(), () -> currentScoreboard.update(), 1);
		}
	}

	public void handleJoin(@Nonnull Player player) {
		updateAll();
	}

	@TimerTask(status = {TimerStatus.RUNNING, TimerStatus.PAUSED})
	public void updateAll() {
		for (ChallengeBossBar bossbar : bossbars) {
			bossbar.update();
		}
		if (currentScoreboard != null) {
			currentScoreboard.update();
		}
	}

	public void showBossBar(@Nonnull ChallengeBossBar bossbar) {
		if (bossbars.contains(bossbar)) return;
		bossbars.add(bossbar);
		bossbar.update();
	}

	public void hideBossBar(@Nonnull ChallengeBossBar bossbar) {
		if (!bossbars.remove(bossbar)) return;
		Bukkit.getOnlinePlayers().forEach(bossbar::applyHide);
	}

	@Nullable
	public ChallengeScoreboard getCurrentScoreboard() {
		return currentScoreboard;
	}

	public void setCurrentScoreboard(@Nullable ChallengeScoreboard scoreboard) {
		if (currentScoreboard == scoreboard) return;

		// Remove old scoreboard
		if (currentScoreboard != null) {
			Bukkit.getOnlinePlayers().forEach(currentScoreboard::applyHide);
		}

		currentScoreboard = scoreboard;

		// Add new scoreboard if available
		if (scoreboard == null) return;
		scoreboard.update();
	}

	public void disable() {
		for (ChallengeBossBar bossbar : bossbars.toArray(new ChallengeBossBar[0])) {
			hideBossBar(bossbar);
		}
		setCurrentScoreboard(null);
	}

	public boolean isShown(@Nonnull ChallengeBossBar bossbar) {
		return bossbars.contains(bossbar);
	}

	public boolean isShown(@Nonnull ChallengeScoreboard scoreboard) {
		return currentScoreboard == scoreboard;
	}

}
