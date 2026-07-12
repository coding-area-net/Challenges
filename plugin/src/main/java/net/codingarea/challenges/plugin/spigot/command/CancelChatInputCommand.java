package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.chat.ChatInputHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class CancelChatInputCommand implements PlayerCommand {

  @Override
  public void onCommand(@NotNull Player player, @NonNull @NotNull String[] args) throws Exception {
    ChatInputHandler handler = ChatInputHandler.get(player);
    if (handler == null) {
      MessageKey.of("command.cancel-input.none").send(player, Prefix.CHALLENGES);
      return;
    }

    SoundSample.BASS_OFF.play(player);
    MessageKey.of("command.cancel-input.done").send(player, Prefix.CHALLENGES);
    ChatInputHandler.remove(player);
  }

}
