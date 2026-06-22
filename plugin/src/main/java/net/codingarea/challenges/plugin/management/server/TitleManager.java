package net.codingarea.challenges.plugin.management.server;

import lombok.Getter;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.commons.common.config.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public final class TitleManager {

  public static final int FADEIN = 5, DURATION = 20, FADEOUT = 10;

  private final boolean timerStatusEnabled;
  private final boolean challengeStatusEnabled;

  public TitleManager() {
    Document config = Challenges.getInstance().getConfigDocument().getDocument("titles");
    timerStatusEnabled = config.getBoolean("timer-status");
    challengeStatusEnabled = config.getBoolean("challenge-status");
  }

  public void sendTimerStatusTitle(@NotNull MessageKey message) {
    if (!timerStatusEnabled) return;
    message.broadcastTitle();
  }

  public void sendChallengeStatusTitle(@NotNull Message message, @NotNull Object... args) {
    if (!challengeStatusEnabled) return;
    message.broadcastTitle(args);
  }

  @Deprecated
  public void sendTitle(@NotNull Player player, @NotNull String title, @NotNull String subtitle) {
    player.sendTitle(title, subtitle, FADEIN, DURATION, FADEOUT);
  }

  @Deprecated
  public void sendTitleInstant(@NotNull Player player, @NotNull String title, @NotNull String subtitle) {
    player.sendTitle(title, subtitle, 0, DURATION, FADEOUT);
  }

}
