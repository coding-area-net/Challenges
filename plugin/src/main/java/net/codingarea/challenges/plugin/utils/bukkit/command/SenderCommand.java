package net.codingarea.challenges.plugin.utils.bukkit.command;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.codingarea.challenges.plugin.content.Prefix;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public interface SenderCommand extends CommandExecutor {

  @Override
  default boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
    try {
      onCommand(sender, args);
    } catch (Exception ex) {
      sender.sendMessage(Prefix.CHALLENGES + "§cSomething went wrong while executing the command");
      Logger.error("Something went wrong while processing the command '{}'", label, ex);
    }
    return true;
  }

  void onCommand(@Nonnull CommandSender sender, @Nonnull String[] args) throws Exception;

}
