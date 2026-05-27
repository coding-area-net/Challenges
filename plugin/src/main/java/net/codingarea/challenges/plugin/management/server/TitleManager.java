package net.codingarea.challenges.plugin.management.server;

import lombok.Getter;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.commons.common.config.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public final class TitleManager {

  private static final int fadein = 5, duration = 20, fadeout = 10;

  private final boolean timerStatusEnabled;
  private final boolean challengeStatusEnabled;

  public TitleManager() {
    Document config = Challenges.getInstance().getConfigDocument().getDocument("titles");
    timerStatusEnabled = config.getBoolean("timer-status");
    challengeStatusEnabled = config.getBoolean("challenge-status");
  }

  public void sendTimerStatusTitle(@NotNull Message message) {
    if (!timerStatusEnabled) return;
    message.broadcastTitle();
  }

  public void sendChallengeStatusTitle(@NotNull Message message, @NotNull Object... args) {
    if (!challengeStatusEnabled) return;
    message.broadcastTitle(args);
  }

  public void sendTitle(@NotNull Player player, @NotNull String title, @NotNull String subtitle) {
    player.sendTitle(title, subtitle, fadein, duration, fadeout);
  }

  public void sendTitleInstant(@NotNull Player player, @NotNull String title, @NotNull String subtitle) {
    player.sendTitle(title, subtitle, 0, duration, fadeout);
  }

}
