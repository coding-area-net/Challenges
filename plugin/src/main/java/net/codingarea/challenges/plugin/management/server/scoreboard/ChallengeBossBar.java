package net.codingarea.challenges.plugin.management.server.scoreboard;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.bukkit.nms.NMSUtils;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public final class ChallengeBossBar {

  private final Map<Player, BossBar> bossbars = new ConcurrentHashMap<>();
  private BiConsumer<BossBarInstance, Player> content = (bossbar, player) -> {
  };

  private BossBar createBossbar(@NotNull BossBarInstance instance) {
    BossBar bossbar = Bukkit.createBossBar(instance.title.toPlainText(), instance.color, instance.style);
    bossbar.setProgress(instance.progress);
    return bossbar;
  }

  private void apply(@NotNull BossBar bossbar, @NotNull BossBarInstance instance) {
    if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_20_5)) {
      bossbar.setTitle(instance.title.toPlainText());
    } else {
      NMSUtils.setBossBarTitle(bossbar, instance.title);
    }
    bossbar.setColor(instance.color);
    bossbar.setStyle(instance.style);
    bossbar.setProgress(instance.progress);
    bossbar.setVisible(instance.visible);
  }

  public void setContent(@NotNull BiConsumer<BossBarInstance, Player> content) {
    this.content = content;
  }

  public void applyHide(@NotNull Player player) {
    BossBar bossbar = bossbars.get(player);
    if (bossbar == null) return;
    bossbar.removePlayer(player);
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

      BossBarInstance instance = new BossBarInstance();

      if (ChallengeAPI.isPaused()) {
        instance.setTitle(Message.forName("bossbar-timer-paused").asString());
        instance.setColor(BarColor.RED);
      } else {
        content.accept(instance, player);
      }


      BossBar bossbar = bossbars.computeIfAbsent(player, key -> createBossbar(instance));
      apply(bossbar, instance);

      if (!bossbar.getPlayers().contains(player))
        bossbar.addPlayer(player);

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

  public static final class BossBarInstance {

    private BaseComponent title = new TextComponent();
    private double progress = 1;
    private BarColor color = BarColor.WHITE;
    private BarStyle style = BarStyle.SOLID;
    private boolean visible = true;

    private BossBarInstance() {
    }

    @NotNull
    public BossBarInstance setTitle(@NotNull String title) {
      this.title = new TextComponent(title);
      return this;
    }

    @NotNull
    public BossBarInstance setTitle(@NotNull BaseComponent title) {
      this.title = title;
      return this;
    }

    @NotNull
    public BossBarInstance setProgress(double progress) {
      if (progress < 0 || progress > 1)
        throw new IllegalArgumentException("Progress must be between 0 and 1; Got " + progress);
      this.progress = progress;
      return this;
    }

    @NotNull
    public BossBarInstance setColor(@NotNull BarColor color) {
      this.color = color;
      return this;
    }

    @NotNull
    public BossBarInstance setStyle(@NotNull BarStyle style) {
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
