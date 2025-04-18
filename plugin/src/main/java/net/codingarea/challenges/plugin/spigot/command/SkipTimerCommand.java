package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SkipTimerCommand implements SenderCommand, Completer {

	@Override
	public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) throws Exception {
		for (IChallenge challenge : Challenges.getInstance().getChallengeManager().getChallenges()) {
			if (!challenge.isEnabled()) continue;
			if (challenge instanceof TimedChallenge) {
				TimedChallenge timedChallenge = (TimedChallenge) challenge;
				timedChallenge.setSecondsUntilActivation(0);
			}
		}
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return Collections.emptyList();
	}

}
