package net.codingarea.challenges.plugin.content.impl;

import net.codingarea.challenges.plugin.content.Message;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MessageManager {

  private static final Map<String, Message> cache = new ConcurrentHashMap<>();

  private MessageManager() {
  }

  @Nonnull
  @CheckReturnValue
  public static Message getOrCreateMessage(@Nonnull String name) {
    return cache.computeIfAbsent(name, key -> new MessageImpl(key));
  }

  @CheckReturnValue
  public static boolean hasMessageInCache(@Nonnull String name) {
    return cache.containsKey(name);
  }

  public static int getMessageCountCached() {
    return cache.size();
  }

}
