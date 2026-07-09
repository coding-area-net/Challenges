package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.content.i18n.impl.format.ComponentArguments;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SkipTimerCommand implements SenderCommand, Completer {

  @Override
  public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) throws Exception {
    List<TimedChallenge> challenges = Challenges.getInstance().getChallengeManager().getChallenges().stream()
      .filter(IChallenge::isEnabled)
      .filter(challenge -> challenge instanceof TimedChallenge)
      .map(challenge -> (TimedChallenge) challenge)
      .filter(challenge -> challenge.getSecondsLeftUntilNextActivation() > 0)
      .peek(challenge -> challenge.setSecondsUntilActivation(0))
      .toList();

    if (challenges.isEmpty()) {
      MessageKey.of("command.skip-timer.none").send(sender, Prefix.CHALLENGES);
    } else {
      LocalizableMessage[] names = challenges.stream().map(IChallenge::getChallengeName).toArray(LocalizableMessage[]::new);
      MessageKey.of("command.skip-timer.done").broadcast(Prefix.CHALLENGES, LocalizableMessage.joinArray(names));
    }
  }

  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
    return Collections.emptyList();
  }

}
