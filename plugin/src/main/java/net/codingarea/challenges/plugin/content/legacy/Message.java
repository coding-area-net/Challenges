package net.codingarea.challenges.plugin.content.legacy;

import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.common.collection.IRandom;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

@Deprecated
public interface Message {

  String NULL = "§r§fN/A";
  Collection<String> UNKNOWN_MESSAGES = new ArrayList<>();

  static String unknown(@NotNull String name) {
    if (!UNKNOWN_MESSAGES.contains(name)) {
      UNKNOWN_MESSAGES.add(name);
      Logger.warn("Tried accessing unknown messages '{}'", name);
    }

    return name;
  }

  @NotNull
  @CheckReturnValue
  @Deprecated
  static Message forName(@NotNull String name) {
    return MessageManager.getOrCreateMessage(name);
  }

  @NotNull
  String asString(@NotNull Object... args);

  @NotNull
  BaseComponent asComponent(@NotNull Object... args);

  @NotNull
  String asRandomString(@NotNull IRandom random, @NotNull Object... args);

  @NotNull
  BaseComponent asRandomComponent(@NotNull IRandom random, @NotNull Prefix prefix, @NotNull Object... args);

  @NotNull
  String asRandomString(@NotNull Object... args);

  @NotNull
  String[] asArray(@NotNull Object... args);

  @NotNull
  BaseComponent[] asComponentArray(@Nullable Prefix prefix, @NotNull Object... args);

  @NotNull
  ItemDescription asItemDescription(@NotNull Object... args);

  void send(@NotNull CommandSender target, @NotNull Prefix prefix, @NotNull Object... args);

  void sendRandom(@NotNull CommandSender target, @NotNull Prefix prefix, @NotNull Object... args);

  void sendRandom(@NotNull IRandom random, @NotNull CommandSender target, @NotNull Prefix prefix, @NotNull Object... args);

  void broadcast(@NotNull Prefix prefix, @NotNull Object... args);

  void broadcastRandom(@NotNull Prefix prefix, @NotNull Object... args);

  void broadcastRandom(@NotNull IRandom random, @NotNull Prefix prefix, @NotNull Object... args);

  void broadcastTitle(@NotNull Object... args);

  void sendTitle(@NotNull Player player, @NotNull Object... args);

  void sendTitleInstant(@NotNull Player player, @NotNull Object... args);

  void setValue(@NotNull String[] value);

  @NotNull
  String getName();

}
