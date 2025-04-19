package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.commons.common.config.FileDocument;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class GamestateCommand implements SenderCommand, Completer {

  @Override
  public void onCommand(@Nonnull CommandSender sender, @Nonnull String[] args) throws Exception {

    if (args.length != 1) {
      Message.forName("syntax").send(sender, Prefix.CHALLENGES, "gamestate <reset/reload>");
      return;
    }

    FileDocument gamestate = Challenges.getInstance().getConfigManager().getGamestateConfig();
    switch (args[0].toLowerCase()) {
      case "reset":
        gamestate.clear();
        Challenges.getInstance().getChallengeManager().resetGamestate();
        Challenges.getInstance().getScoreboardManager().updateAll();
        Message.forName("command-gamestate-reset").send(sender, Prefix.CHALLENGES);
        break;
      case "reload":
        Challenges.getInstance().getChallengeManager().loadGamestate(gamestate.readonly());
        Challenges.getInstance().getScoreboardManager().updateAll();
        Message.forName("command-gamestate-reload").send(sender, Prefix.CHALLENGES);
        break;
      default:
        Message.forName("syntax").send(sender, Prefix.CHALLENGES, "gamestate <reset/reload>");
    }

  }

  @Nullable
  @Override
  public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
    return args.length == 1 ? Utils.filterRecommendations(args[0], "reset", "reload") : Collections.emptyList();
  }

}
