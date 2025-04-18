package net.codingarea.challenges.plugin.spigot.listener;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeScoreboard;
import net.codingarea.challenges.plugin.spigot.events.PlayerIgnoreStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ScoreboardUpdateListener implements Listener {

	@EventHandler
	public void onIgnoredChange(PlayerIgnoreStatusChangeEvent event) {

		Bukkit.getScheduler().runTask(Challenges.getInstance(), () -> {
			ChallengeScoreboard currentScoreboard = Challenges.getInstance().getScoreboardManager().getCurrentScoreboard();
			if (currentScoreboard != null) {
				currentScoreboard.update();
			}
		});

	}

}
