package net.codingarea.challenges.platform.message.common.wrapper;

import lombok.RequiredArgsConstructor;
import net.codingarea.challenges.platform.message.MessageHolder;
import net.codingarea.challenges.platform.message.common.AbstractMiniMessagePlatform;
import net.codingarea.challenges.platform.message.wrapper.MessageBossBar;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public class AdventureMessageBossBar implements MessageBossBar {

  protected final AbstractMiniMessagePlatform platform;
  protected final BossBar bar;

  @Override
  public void setText(@NonNull MessageHolder text) {
    bar.name(platform.convertMessageHolderToComponent(text));
  }

  @Override
  public void setColor(@NonNull BarColor color) {
    bar.color(convertColor(color));
  }

  @Override
  public void setStyle(@NonNull BarStyle style) {
    bar.overlay(convertStyle(style));
  }

  @Override
  public void setProgress(float progress) {
    bar.progress(progress);
  }

  @Override
  public void show(@NonNull Player player) {
    platform.showAdventureBossBar(player, bar);
  }

  @Override
  public void hide(@NonNull Player player) {
    platform.hideAdventureBossBar(player, bar);
  }

  @NotNull
  protected static BossBar.Overlay convertStyle(@NotNull BarStyle style) {
    return switch (style) {
      case SEGMENTED_6 -> BossBar.Overlay.NOTCHED_6;
      case SEGMENTED_10 -> BossBar.Overlay.NOTCHED_10;
      case SEGMENTED_12 -> BossBar.Overlay.NOTCHED_12;
      case SEGMENTED_20 -> BossBar.Overlay.NOTCHED_20;
      default -> BossBar.Overlay.PROGRESS;
    };
  }

  @NotNull
  protected static BossBar.Color convertColor(@NotNull BarColor color) {
    return switch (color) {
      case PINK -> BossBar.Color.PINK;
      case BLUE -> BossBar.Color.BLUE;
      case RED -> BossBar.Color.RED;
      case GREEN -> BossBar.Color.GREEN;
      case YELLOW -> BossBar.Color.YELLOW;
      case PURPLE -> BossBar.Color.PURPLE;
      default -> BossBar.Color.WHITE;
    };
  }
}
