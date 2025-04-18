package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
import java.util.List;

@Since("2.0")
public class LastManStandingGoal extends SettingGoal {

	private Player winner;

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.IRON_HELMET, Message.forName("item-last-man-standing-goal"));
	}

	@Override
	public void getWinnersOnEnd(@Nonnull List<Player> winners) {
		determineWinner();
		if (winner != null)
			winners.add(winner);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDeath(@Nonnull PlayerDeathEvent event) {
		if (!isEnabled()) return;
		checkEnd();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeave(@Nonnull PlayerQuitEvent event) {
		if (!isEnabled()) return;
		checkEnd();
	}

	protected void determineWinner() {
		int playersLiving = 0;
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getGameMode() == GameMode.SPECTATOR) continue;
			playersLiving++;
			winner = player;
		}

		if (playersLiving != 1)
			winner = null;
	}

	protected void checkEnd() {
		determineWinner();
		if (winner == null) return;
		ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED);
	}

}
