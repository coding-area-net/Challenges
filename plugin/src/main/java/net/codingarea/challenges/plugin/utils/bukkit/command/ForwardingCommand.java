package net.codingarea.challenges.plugin.utils.bukkit.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.0
 */
public class ForwardingCommand implements SenderCommand, TabCompleter {

	private final String forwardCommand;
	private final boolean overrideTab;

	public ForwardingCommand(@Nonnull String forwardCommand) {
		this(forwardCommand, true);
	}

	public ForwardingCommand(@Nonnull String forwardCommand, boolean overrideTab) {
		this.forwardCommand = forwardCommand;
		this.overrideTab = overrideTab;
	}

	@Override
	public void onCommand(@Nonnull CommandSender sender, @Nonnull String[] args) throws Exception {
		Bukkit.dispatchCommand(sender, forwardCommand + " " + String.join(" ", args));
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
		return overrideTab ? new ArrayList<>() : null;
	}

}