package net.codingarea.challenges.plugin.management.server.scoreboard;

import lombok.RequiredArgsConstructor;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public final class ChallengeBossBar {

  private final Map<Player, BossBar> bossbars = new ConcurrentHashMap<>();
  private BiConsumer<BossBarInstance, Player> content = (bossbar, player) -> {
  };

  @NotNull
  private BossBar createBossbar(@NotNull BossBarInstance instance) {
    return BossBar.bossBar(instance.title, instance.progress, instance.color, instance.style);
  }

  private void apply(@NotNull BossBar bossbar, @NotNull BossBarInstance instance, @NotNull Player player) {
    bossbar.color(instance.color);
    bossbar.overlay(instance.style);
    bossbar.progress(instance.progress);
    bossbar.name(instance.title);

    if (instance.visible) {
      player.showBossBar(bossbar);
    } else {
      player.hideBossBar(bossbar);
    }
  }

  public void setContent(@NotNull BiConsumer<BossBarInstance, Player> content) {
    this.content = content;
  }

  public void applyHide() {
    Bukkit.getOnlinePlayers().forEach(this::applyHide);
  }

  public void applyHide(@NotNull Player player) {
    BossBar bossbar = bossbars.get(player);
    if (bossbar == null) return;
    player.hideBossBar(bossbar);
  }

  public void update() {
    Bukkit.getOnlinePlayers().forEach(this::update);
  }

  public void update(@NotNull Player player) {
    if (!isShown()) {
      Logger.warn("Tried to update bossbar which is not shown");
      return;
    }

    try {
      BossBarInstance instance = new BossBarInstance(player);

      if (ChallengeAPI.isPaused()) {
        instance.setTitle(MessageKey.of("bossbar-timer-paused"));
        instance.setColor(BossBar.Color.RED);
      } else {
        content.accept(instance, player);
      }

      BossBar bossbar = bossbars.computeIfAbsent(player, key -> createBossbar(instance));
      apply(bossbar, instance, player);
    } catch (Exception ex) {
      Logger.error("Unable to update bossbar for player '{}'", player.getName(), ex);
    }
  }

  public void show() {
    Challenges.getInstance().getScoreboardManager().showBossBar(this);
  }

  public void hide() {
    Challenges.getInstance().getScoreboardManager().hideBossBar(this);
  }

  public boolean isShown() {
    return Challenges.getInstance().getScoreboardManager().isShown(this);
  }

  @RequiredArgsConstructor
  public static final class BossBarInstance {

    private final Player player;

    private Component title;
    private float progress = 1;
    private BossBar.Color color = BossBar.Color.WHITE;
    private BossBar.Overlay style = BossBar.Overlay.PROGRESS;
    private boolean visible = true;

    @NotNull
    public BossBarInstance setTitle(@NotNull String title) {
      this.title = Component.text(title);
      return this;
    }

    @NotNull
    @Deprecated
    public BossBarInstance setTitle(@NotNull BaseComponent title) {
      return this;
    }

    @NotNull
    public BossBarInstance setTitle(@NotNull MessageKey title, @NotNull Object... args) {
      this.title = title.asComponent(player, args);
      return this;
    }

    @NotNull
    public BossBarInstance setTitle(@NotNull LocalizableMessage title) {
      this.title = title.getLocalizableKey().asComponent(player, title.getLocalizableArgs());
      return this;
    }

    @NotNull
    public BossBarInstance setTitle(Component title) {
      this.title = title;
      return this;
    }

    @NotNull
    public BossBarInstance setProgress(float progress) {
      if (progress < 0 || progress > 1)
        throw new IllegalArgumentException("Progress must be between 0 and 1; Got " + progress);
      this.progress = progress;
      return this;
    }

    @NotNull
    public BossBarInstance setColor(BossBar.Color color) {
      this.color = color;
      return this;
    }

    @NotNull
    public BossBarInstance setStyle(BossBar.Overlay style) {
      this.style = style;
      return this;
    }

    @NotNull
    public BossBarInstance setVisible(boolean visible) {
      this.visible = visible;
      return this;
    }

  }

}
