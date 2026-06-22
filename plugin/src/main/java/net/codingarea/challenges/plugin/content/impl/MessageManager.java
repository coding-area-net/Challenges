package net.codingarea.challenges.plugin.content.impl;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MessageManager {

  private static final Map<String, Message> cache = new ConcurrentHashMap<>();

  private MessageManager() {
  }

  @NotNull
  public static Message getOrCreateMessage(@NotNull String name) {
    return cache.computeIfAbsent(name, key -> {
      MessageImpl message = new MessageImpl(key);
      String[] value = Challenges.getInstance().getTranslationManager().getMessageKey(key).localizeRawValue(Locale.GERMAN);
      message.setValue(value);
      return message;
    });
  }

  public static boolean hasMessageInCache(@NotNull String name) {
    return cache.containsKey(name);
  }

  public static int getMessageCountCached() {
    return cache.size();
  }

}
