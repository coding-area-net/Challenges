package net.codingarea.challenges.plugin.management.menu.generator;

import org.jetbrains.annotations.NotNull;

public interface IDynamicMenuGenerator<T> extends IMenuGenerator {

  void addToCache(@NotNull T element);

  void removeFromCache(@NotNull T element);

  boolean isCached(@NotNull T element);

  int getCachedCount();

  void resetCache();

  void updateElementDisplay(@NotNull T element);

}
