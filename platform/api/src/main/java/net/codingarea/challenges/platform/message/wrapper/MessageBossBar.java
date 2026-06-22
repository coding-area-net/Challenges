package net.codingarea.challenges.platform.message.wrapper;

import net.codingarea.challenges.platform.message.MessageHolder;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper for underlying boss bar api (bukkit or adventure).
 * We cannot expose the adventure api BossBar due to incompatibilities.
 */
public interface MessageBossBar {

  void show(@NotNull Player player);

  void hide(@NotNull Player player);

  void setText(@NotNull MessageHolder text);

  void setColor(@NotNull BarColor color);

  void setStyle(@NotNull BarStyle style);

  void setProgress(float progress);

}
