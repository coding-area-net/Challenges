package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class CompletableForceChallenge extends AbstractForceChallenge {

  public CompletableForceChallenge(@NotNull MenuType menu) {
    super(menu);
  }

  public CompletableForceChallenge(@NotNull MenuType menu, int max) {
    super(menu, max);
  }

  public CompletableForceChallenge(@NotNull MenuType menu, int min, int max) {
    super(menu, min, max);
  }

  public CompletableForceChallenge(@NotNull MenuType menu, int min, int max, int defaultValue) {
    super(menu, min, max, defaultValue);
  }

  @Override
  protected final void handleCountdownEnd() {
    broadcastFailedMessage();
    endForcing();
    ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_FAILED);
  }

  protected final void completeForcing(@NotNull Player player) {
    if (getState() != COUNTDOWN) return;
    broadcastSuccessMessage(player);
    endForcing();
    SoundSample.LEVEL_UP.broadcast();
  }

  protected abstract void broadcastFailedMessage();

  protected abstract void broadcastSuccessMessage(@NotNull Player player);

}
