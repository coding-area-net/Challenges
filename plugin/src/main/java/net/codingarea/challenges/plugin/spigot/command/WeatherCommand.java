package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class WeatherCommand implements PlayerCommand, Completer {

	@Override
	public void onCommand(@Nonnull Player player, @Nonnull String[] args) throws Exception {

		if (args.length == 0) {
			Message.forName("syntax").send(player, Prefix.CHALLENGES, "weather <sun/clear/rain/thunder>");
			return;
		}

		World world = player.getWorld();

		switch (args[0].toLowerCase()) {

			case "clear":
			case "sun":
				world.setStorm(false);
				world.setThundering(false);
				Message.forName("command-weather-set-clear").send(player, Prefix.CHALLENGES);
				break;
			case "rain":
				world.setThundering(false);
				world.setStorm(true);
				Message.forName("command-weather-set-rain").send(player, Prefix.CHALLENGES);
				break;
			case "thunder":
				world.setStorm(true);
				world.setThundering(true);
				Message.forName("command-weather-set-thunder").send(player, Prefix.CHALLENGES);
				break;
			default:
				Message.forName("syntax").send(player, Prefix.CHALLENGES, "weather <sun/clear/rain/thunder>");
		}

	}

	@Nullable
	@Override
	public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
		if (args.length > 1) return new ArrayList<>();
		return Utils.filterRecommendations(args[0], "sun", "clear", "rain", "thunder");
	}

}
