package net.codingarea.challenges.plugin.management.server;

import lombok.Getter;
import net.codingarea.challenges.plugin.content.legacy.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public enum ChallengeEndCause {

  TIMER_HIT_ZERO(Message.forName("challenge-end-timer-hit-zero"), Message.forName("challenge-end-timer-hit-zero-winner")),
  GOAL_REACHED(Message.forName("challenge-end-goal-reached"), Message.forName("challenge-end-goal-reached-winner")),
  GOAL_FAILED(Message.forName("challenge-end-goal-failed"), null);

  private final Message noWinnerMessage, winnerMessage;

  ChallengeEndCause(@NotNull Message noWinnerMessage, @Nullable Message winnerMessage) {
    this.noWinnerMessage = noWinnerMessage;
    this.winnerMessage = winnerMessage;
  }

  @NotNull
  public Message getMessage(boolean withWinner) {
    return withWinner && winnerMessage != null ? winnerMessage : noWinnerMessage;
  }

  public boolean isWinnable() {
    return winnerMessage != null;
  }

}
