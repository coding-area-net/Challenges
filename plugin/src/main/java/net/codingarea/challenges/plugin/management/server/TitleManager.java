package net.codingarea.challenges.plugin.management.server;

import lombok.Getter;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.content.legacy.Message;
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

  public void sendChallengeStatusTitle(@NotNull MessageKey message, @NotNull Object... args) {
    if (!challengeStatusEnabled) return;
    message.broadcastTitle(args);
  }

  public void sendChallengeToggleTitle(@NotNull IChallenge challenge, boolean enabled) {
    MessageKey titleMessage = enabled ? MessageKey.of("title.challenge-enabled") : MessageKey.of("title.challenge-disabled");
    sendChallengeStatusTitle(titleMessage, challenge.getChallengeName());
  }

  public void sendGoalToggleTitle(@NotNull IGoal goal, boolean enabled) {
    MessageKey titleMessage = enabled ? MessageKey.of("title.goal-enabled") : MessageKey.of("title.goal-disabled");
    sendChallengeStatusTitle(titleMessage, goal.getChallengeName());
  }

  public void sendChallengeValueTitle(@NotNull IChallenge challenge, @NotNull Object arg) {
    MessageKey titleMessage = MessageKey.of("title.challenge-value-changed");
    sendChallengeStatusTitle(titleMessage, challenge.getChallengeName(), arg);
  }


  @Deprecated
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
