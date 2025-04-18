package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.misc.CommandHelper;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GamemodeCommand implements SenderCommand, Completer {

	@Override
	public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) throws Exception {
		List<Player> targets = new ArrayList<>();

		if (args.length == 0) {
			Message.forName("syntax").send(sender, Prefix.CHALLENGES, "gm <gamemode> [player]");
			return;
		}

		GameMode gamemode = getGameMode(args[0]);

		if (gamemode == null) {
			Message.forName("syntax").send(sender, Prefix.CHALLENGES, "gm <gamemode> [player]");
			return;
		}

		if (args.length > 1) {
			targets.addAll(CommandHelper.getPlayers(sender, args[1]));

		} else if (sender instanceof Player) {
			targets.add(((Player) sender).getPlayer());
		}

		if (targets.isEmpty()) {
			Message.forName("command-no-target").send(sender, Prefix.CHALLENGES);
			return;
		}

		boolean otherPlayers = false;

		for (Player player : targets) {
			Message.forName("command-gamemode-gamemode-changed").send(player, Prefix.CHALLENGES, gamemode);
			player.setGameMode(gamemode);
			if (player != sender)
				otherPlayers = true;

		}
		if (otherPlayers) {
			Message.forName("command-gamemode-gamemode-changed-others").send(sender, Prefix.CHALLENGES, gamemode, targets.size());
		}

	}

	@Nullable
	private GameMode getGameMode(String input) {
		switch (input.toLowerCase()) {
			case "survival":
			case "s":
			case "0":
				return GameMode.SURVIVAL;
			case "spectator":
			case "sp":
			case "3":
				return GameMode.SPECTATOR;
			case "adventure":
			case "a":
			case "2":
				return GameMode.ADVENTURE;
			case "creative":
			case "c":
			case "1":
				return GameMode.CREATIVE;
		}
		return null;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
		if (args.length == 2) return CommandHelper.getCompletions(sender);
		if (args.length > 1) return null;
		return Utils.filterRecommendations(args[0], "0", "1", "2", "3", "survival", "creative", "spectator", "adventure", "s", "c", "sp", "a");
	}

}
