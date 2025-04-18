package net.codingarea.challenges.plugin.spigot.command;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.common.misc.StringUtils;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TimerCommand implements SenderCommand, Completer {

	@Override
	public void onCommand(@Nonnull CommandSender sender, @Nonnull String[] args) {

		if (args.length == 0) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				Challenges.getInstance().getMenuManager().openMenu(player, MenuType.TIMER, 0);
				SoundSample.OPEN.play(player);
			}
			return;
		}

		switch (args[0].toLowerCase()) {
			default:
				Message.forName("syntax").send(sender, Prefix.TIMER, "timer <resume/pause/reset/set/mode>");
				return;
			case "resume":
			case "start":
				if (ChallengeAPI.isStarted()) {
					Message.forName("timer-already-started").send(sender, Prefix.TIMER);
					SoundSample.BASS_OFF.playIfPlayer(sender);
					break;
				}
				Challenges.getInstance().getChallengeTimer().resume();
				break;
			case "stop":
			case "pause":
				if (ChallengeAPI.isPaused()) {
					Message.forName("timer-already-paused").send(sender, Prefix.TIMER);
					SoundSample.BASS_OFF.playIfPlayer(sender);
					break;
				}
				Challenges.getInstance().getChallengeTimer().pause(true);
				break;
			case "reset":
				Challenges.getInstance().getChallengeTimer().reset();
				break;
			case "set":
				long seconds = StringUtils.parseSeconds(String.join(" ", args));
				if (seconds >= Integer.MAX_VALUE || seconds < 0) {
					seconds = Integer.MAX_VALUE;
				}
				Challenges.getInstance().getChallengeTimer().setSeconds(seconds);
				Message.forName("timer-was-set").send(sender, Prefix.TIMER, Challenges.getInstance().getChallengeTimer().getFormattedTime());
				break;
			case "show":
				Challenges.getInstance().getChallengeTimer().setHidden(false);
				break;
			case "hide":
				Challenges.getInstance().getChallengeTimer().setHidden(true);
				break;
			case "mode":
				if (args.length != 2) {
					Message.forName("syntax").send(sender, Prefix.TIMER, "timer mode <up/down>");
					break;
				}
				switch (args[1].toLowerCase()) {
                    case "up":
					case "forward":
						Challenges.getInstance().getChallengeTimer().setCountingUp(true);
						break;
					case "down":
					case "back":
					case "backwards":
						Challenges.getInstance().getChallengeTimer().setCountingUp(false);
						break;
                    default:
                        Message.forName("syntax").send(sender, Prefix.TIMER, "timer mode <up/down>");
                        break;
                }

		}

	}

	@Override
	public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
		if (args.length == 0) return new ArrayList<>();
		String last = args[args.length - 1];
		if (args.length == 1) {
			return Utils.filterRecommendations(args[0], "resume", "pause", "stop", "start", "reset", "show", "hide", "mode", "set");
		} else if (args.length == 2 && "mode".equalsIgnoreCase(args[0])) {
			return Utils.filterRecommendations(args[1], "up", "down");
		} else if ("set".equalsIgnoreCase(args[0])) {
			return StringUtils.isNumber(last) ? Utils.filterRecommendations(last, last + "m", last + "h", last + "d", last + "w") : last.isEmpty() ? Arrays.asList("10", "30", "60", "120") : Collections.singletonList(last);
		}
		return new ArrayList<>();
	}

}
