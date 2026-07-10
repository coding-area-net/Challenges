package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import net.codingarea.commons.common.config.FileDocument;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GamestateCommand implements SenderCommand, Completer {

  @Override
  public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) throws Exception {

    if (args.length != 1) {
      MessageKey.of("syntax").send(sender, Prefix.CHALLENGES, "gamestate <reset/reload>");
      return;
    }

    FileDocument gamestate = Challenges.getInstance().getConfigManager().getGamestateConfig();
    switch (args[0].toLowerCase()) {
      case "reset":
        gamestate.clear();
        Challenges.getInstance().getChallengeManager().resetGamestate();
        Challenges.getInstance().getScoreboardManager().updateAll();
        MessageKey.of("command-gamestate-reset").send(sender, Prefix.CHALLENGES);
        break;
      case "reload":
        Challenges.getInstance().getChallengeManager().loadGamestate(gamestate.readonly());
        Challenges.getInstance().getScoreboardManager().updateAll();
        MessageKey.of("command-gamestate-reload").send(sender, Prefix.CHALLENGES);
        break;
      default:
        MessageKey.of("syntax").send(sender, Prefix.CHALLENGES, "gamestate <reset/reload>");
    }

  }

  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
    return args.length == 1 ? Utils.filterRecommendations(args[0], "reset", "reload") : Collections.emptyList();
  }

}
