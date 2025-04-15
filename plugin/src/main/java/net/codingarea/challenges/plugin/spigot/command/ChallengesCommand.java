package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 1.0
 */
public class ChallengesCommand implements PlayerCommand, Completer {

	@Override
	public void onCommand(@Nonnull Player player, @Nonnull String[] args) {
		if (args.length > 1) {
			Message.forName("syntax").send(player, Prefix.CHALLENGES, "challenges [menu]");
			return;
		}

		if (args.length == 1) {
			String menuName = args[0].toUpperCase();
			try {
				MenuType menuType = MenuType.valueOf(menuName);
				Challenges.getInstance().getMenuManager().openMenu(player, menuType, 0);
			} catch (IllegalArgumentException exception) {
				Challenges.getInstance().getMenuManager().openGUI(player);
			}
			return;
		}

		Challenges.getInstance().getMenuManager().openGUI(player);

	}


	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return args.length != 1 ? null : Utils.filterRecommendations(args[0],
			Arrays.stream(MenuType.values())
				.map(menuType -> menuType.name().toLowerCase())
				.toArray(String[]::new)
		);
	}

}
