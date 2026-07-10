package net.codingarea.challenges.plugin.utils.bukkit.command;

import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerCommand extends CommandExecutor {

  @Override
  default boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender instanceof Player) {
      try {
        onCommand((Player) sender, args);
      } catch (Exception ex) {
        sender.sendMessage(Prefix.CHALLENGES + "§cSomething went wrong while executing the command");
        Logger.error("Something went wrong while processing the command '{}'", label, ex);
      }
    } else {
      MessageKey.of("player-command").send(sender, Prefix.CHALLENGES);
    }
    return true;
  }

  void onCommand(@NotNull Player player, @NotNull String[] args) throws Exception;

}
