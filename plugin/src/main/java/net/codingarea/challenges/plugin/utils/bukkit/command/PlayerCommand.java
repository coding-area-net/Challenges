package net.codingarea.challenges.plugin.utils.bukkit.command;

import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface PlayerCommand extends CommandExecutor {

  @Override
  default boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
    if (sender instanceof Player) {
      try {
        onCommand((Player) sender, args);
      } catch (Exception ex) {
        sender.sendMessage(Prefix.CHALLENGES + "§cSomething went wrong while executing the command");
        Logger.error("Something went wrong while processing the command '{}'", label, ex);
      }
    } else {
      Message.forName("player-command").send(sender, Prefix.CHALLENGES);
    }
    return true;
  }

  void onCommand(@Nonnull Player player, @Nonnull String[] args) throws Exception;

}
