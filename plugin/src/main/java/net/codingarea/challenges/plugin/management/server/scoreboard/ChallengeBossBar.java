package net.codingarea.challenges.plugin.management.server.scoreboard;

import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.bukkit.nms.NMSUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public final class ChallengeBossBar {

  private final Map<Player, BossBar> bossbars = new ConcurrentHashMap<>();
  private BiConsumer<BossBarInstance, Player> content = (bossbar, player) -> {
  };

  private BossBar createBossbar(@Nonnull BossBarInstance instance) {
    BossBar bossbar = Bukkit.createBossBar(instance.title.toPlainText(), instance.color, instance.style);
    bossbar.setProgress(instance.progress);
    return bossbar;
  }

  private void apply(@Nonnull BossBar bossbar, @Nonnull BossBarInstance instance) {
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

  public void setContent(@Nonnull BiConsumer<BossBarInstance, Player> content) {
    this.content = content;
  }

  public void applyHide(@Nonnull Player player) {
    BossBar bossbar = bossbars.get(player);
    if (bossbar == null) return;
    bossbar.removePlayer(player);
  }

  public void update() {
    Bukkit.getOnlinePlayers().forEach(this::update);
  }

  public void update(@Nonnull Player player) {
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

    @Nonnull
    public BossBarInstance setTitle(@Nonnull String title) {
      this.title = new TextComponent(title);
      return this;
    }

    @Nonnull
    public BossBarInstance setTitle(@Nonnull BaseComponent title) {
      this.title = title;
      return this;
    }

    @Nonnull
    public BossBarInstance setProgress(double progress) {
      if (progress < 0 || progress > 1)
        throw new IllegalArgumentException("Progress must be between 0 and 1; Got " + progress);
      this.progress = progress;
      return this;
    }

    @Nonnull
    public BossBarInstance setColor(@Nonnull BarColor color) {
      this.color = color;
      return this;
    }

    @Nonnull
    public BossBarInstance setStyle(@Nonnull BarStyle style) {
      this.style = style;
      return this;
    }

    @Nonnull
    public BossBarInstance setVisible(boolean visible) {
      this.visible = visible;
      return this;
    }

  }

}
