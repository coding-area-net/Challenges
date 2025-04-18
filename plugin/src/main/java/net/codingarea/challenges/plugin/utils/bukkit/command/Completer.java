package net.codingarea.challenges.plugin.utils.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface Completer extends TabCompleter {

	@Override
	@Nullable
	default List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
		return onTabComplete(sender, args);
	}

	@Nullable
	List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull String[] args);
}
