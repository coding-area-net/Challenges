package net.codingarea.challenges.plugin.spigot.command;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.challenges.type.abstraction.ForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author sehrschlechtYT | <a href="https://github.com/sehrschlechtYT">...</a>
 * @since 2.1.4
 */
public class ResultCommand implements PlayerCommand {
	@Override
	public void onCommand(@NotNull Player player, @NotNull String[] args) throws Exception {
		if (ChallengeAPI.isPaused()) {
			Message.forName("timer-not-started").send(player, Prefix.CHALLENGES);
			SoundSample.BASS_OFF.play(player);
			return;
		}

		IGoal currentGoal = Challenges.getInstance().getChallengeManager().getCurrentGoal();

		if (currentGoal instanceof ForceBattleGoal) {
			ForceBattleGoal<?> forceBattleGoal = (ForceBattleGoal<?>) currentGoal;
			forceBattleGoal.sendResult(player);
			return;
		}

		Message.forName("command-result-no-battle-active").send(player, Prefix.CHALLENGES);
	}
}
