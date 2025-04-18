package net.codingarea.challenges.plugin.management.server;

import lombok.Getter;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

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

  public void sendTimerStatusTitle(@Nonnull Message message) {
    if (!timerStatusEnabled) return;
    message.broadcastTitle();
  }

  public void sendChallengeStatusTitle(@Nonnull Message message, @Nonnull Object... args) {
    if (!challengeStatusEnabled) return;
    message.broadcastTitle(args);
  }

  public void sendTitle(@Nonnull Player player, @Nonnull String title, @Nonnull String subtitle) {
    player.sendTitle(title, subtitle, fadein, duration, fadeout);
  }

  public void sendTitleInstant(@Nonnull Player player, @Nonnull String title, @Nonnull String subtitle) {
    player.sendTitle(title, subtitle, 0, duration, fadeout);
  }

}
