package net.codingarea.challenges.plugin.challenges.type;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.management.challenges.ChallengeManager;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IGoal extends IChallenge {

  /**
   * Enabled / disabled this goal.
   * This must call {@link ChallengeManager#setCurrentGoal(IGoal)} with
   * {@code this} if {@code enabled} is {@code true} or {@code null} if {@code enabled} is {@code false} and this goal is the current.
   * This can be checked by comparing {@link ChallengeManager#getCurrentGoal()} to {@code this}.
   * You may just call {@link GoalHelper#handleSetEnabled(IGoal, boolean)} with {@code this}, {@code enabled}
   */
  void setEnabled(boolean enabled);

  /**
   * This sound will be played when the timer is started and the plugin setting "enable-specific-start-sounds" is enabled.
   * If not the standard sound will be played.
   *
   * @return the sound to play
   */
  @Nonnull
  SoundSample getStartSound();

  /**
   * This sound will be played when the challenge is won and the plugin setting "enabled-win-sounds" is enabled.
   * If this returns {@code null} no sound will be played.
   *
   * @return the sound to play
   */
  @Nullable
  SoundSample getWinSound();

  /**
   * This method will be called, when the challenges are being ended with a winnable cause ({@link ChallengeEndCause#isWinnable()}) and when there is no player supplier given in order to determine the winner of this challenge run.
   * If no players are added to the winners list, the {@link ChallengeEndCause#getNoWinnerMessage()} will be shown instead of the {@link ChallengeEndCause#getWinnerMessage()}.
   *
   * @param winners the list to which the winners should be added
   */
  void getWinnersOnEnd(@Nonnull List<Player> winners);

}
