package net.codingarea.challenges.plugin.spigot.command;

import com.google.common.collect.Lists;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ResetCommand implements SenderCommand, Completer {

  private final boolean confirmReset, seedResetCommand;

  public ResetCommand() {
    Document pluginConfig = Challenges.getInstance().getConfigDocument();
    confirmReset = pluginConfig.getBoolean("confirm-reset");

    Document seedResetConfig = pluginConfig.getDocument("custom-seed");
    seedResetCommand = seedResetConfig.getBoolean("command");
  }

  @Override
  public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {

    if (confirmReset && (args.length < 1 || !args[0].equalsIgnoreCase("confirm")) || (args.length > 0 && !args[0].equalsIgnoreCase("confirm"))) {
      if (args.length > 0 && args[0].equalsIgnoreCase("settings")) {
        Challenges.getInstance().getChallengeManager().restoreDefaults();
        MessageKey.of("config-reset").broadcast(Prefix.CHALLENGES);
        return;
      } else if (args.length > 0 && args[0].equalsIgnoreCase("customs")) {
        Challenges.getInstance().getCustomChallengesLoader().resetChallenges();
        MessageKey.of("custom_challenges-reset").broadcast(Prefix.CHALLENGES);
        return;
      }

      if (!Challenges.getInstance().getWorldManager().isEnableFreshReset() && ChallengeAPI.isFresh()) {
        MessageKey.of("no-fresh-reset").send(sender, Prefix.CHALLENGES);
        SoundSample.BASS_OFF.playIfPlayer(sender);
        return;
      }

      if (confirmReset) {
        MessageKey.of("confirm-reset").send(sender, Prefix.CHALLENGES, "reset confirm");
        return;
      }
    }

    if (!Challenges.getInstance().getWorldManager().isEnableFreshReset() && ChallengeAPI.isFresh()) {
      MessageKey.of("no-fresh-reset").send(sender, Prefix.CHALLENGES);
      SoundSample.BASS_OFF.playIfPlayer(sender);
      return;
    }

    Long seed = null;

    if (seedResetCommand) {
      int index = confirmReset ? 1 : 0;
      if (args.length > index) {
        String seedInput = args[index];
        try {
          seed = Long.parseLong(seedInput);
        } catch (NumberFormatException exception) {
          Challenges.getInstance().getILogger().error("", exception);
        }
      }
    }

    Challenges.getInstance().getWorldManager().prepareWorldReset(sender, seed);

  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
    if (confirmReset && args.length == 1) return Utils.filterRecommendations(
      args[0], "confirm", "settings", "customs");
    if (seedResetCommand && ((confirmReset && args.length == 2) || args.length == 1)) {
      return args.length == 1 ?
        Utils.filterRecommendations(args[args.length - 1], "[seed]", "settings", "customs") :
        args[0].equalsIgnoreCase("confirm") ? Collections.singletonList("[seed]") : Lists
          .newLinkedList();
    }

    return Lists.newLinkedList();
  }

}
