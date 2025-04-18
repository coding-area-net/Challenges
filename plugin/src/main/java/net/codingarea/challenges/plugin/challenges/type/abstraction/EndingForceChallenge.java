package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class EndingForceChallenge extends AbstractForceChallenge {

  public EndingForceChallenge(@Nonnull MenuType menu) {
    super(menu);
  }

  public EndingForceChallenge(@Nonnull MenuType menu, int max) {
    super(menu, max);
  }

  public EndingForceChallenge(@Nonnull MenuType menu, int min, int max) {
    super(menu, min, max);
  }

  public EndingForceChallenge(@Nonnull MenuType menu, int min, int max, int defaultValue) {
    super(menu, min, max, defaultValue);
  }

  @Override
  protected final void handleCountdownEnd() {
    checkAllPlayers();
  }

  private void checkAllPlayers() {
    List<Player> failed = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (!ignorePlayer(player) && isFailing(player)) {
        broadcastFailedMessage(player);
        failed.add(player);
      }
    }
    if (!failed.isEmpty()) {
      killFailedPlayers(failed);
      return;
    }

    broadcastSuccessMessage();
    SoundSample.LEVEL_UP.broadcast();
  }

  private void killFailedPlayers(@Nonnull Iterable<? extends Player> failed) {
    if (!ChallengeAPI.isStarted()) return;
    failed.forEach(ChallengeHelper::kill);
  }

  protected abstract boolean isFailing(@Nonnull Player player);

  protected abstract void broadcastFailedMessage(@Nonnull Player player);

  protected abstract void broadcastSuccessMessage();

}
