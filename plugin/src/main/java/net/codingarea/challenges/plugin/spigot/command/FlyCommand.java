package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.misc.CommandHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FlyCommand implements SenderCommand, Completer {

  @Override
  public void onCommand(@Nonnull CommandSender sender, @Nonnull String[] args) throws Exception {

    List<Player> targets = new ArrayList<>();

    if (args.length > 0) {
      targets.addAll(CommandHelper.getPlayers(sender, args[0]));

    } else if (sender instanceof Player) {
      targets.add((Player) sender);
    }

    if (targets.isEmpty()) {
      Message.forName("command-no-target").send(sender, Prefix.CHALLENGES);
      return;
    }

    boolean otherPlayers = false;
    for (Player target : targets) {

      if (target.getAllowFlight()) {
        Message.forName("command-fly-disabled").send(target, Prefix.CHALLENGES);
      } else {
        Message.forName("command-fly-enabled").send(target, Prefix.CHALLENGES);
      }
      target.setAllowFlight(!target.getAllowFlight());

      if (target != sender)
        otherPlayers = true;

    }

    if (otherPlayers)
      Message.forName("command-fly-toggled-others").send(sender, Prefix.CHALLENGES, targets.size());

  }

  @Nullable
  @Override
  public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
    return CommandHelper.getCompletions(sender);
  }

}
