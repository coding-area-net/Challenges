package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class EndingForceChallenge extends AbstractForceChallenge {

  public EndingForceChallenge(@NotNull MenuType menu) {
    super(menu);
  }

  public EndingForceChallenge(@NotNull MenuType menu, int max) {
    super(menu, max);
  }

  public EndingForceChallenge(@NotNull MenuType menu, int min, int max) {
    super(menu, min, max);
  }

  public EndingForceChallenge(@NotNull MenuType menu, int min, int max, int defaultValue) {
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

  private void killFailedPlayers(@NotNull Iterable<? extends Player> failed) {
    if (!ChallengeAPI.isStarted()) return;
    failed.forEach(ChallengeHelper::kill);
  }

  protected abstract boolean isFailing(@NotNull Player player);

  protected abstract void broadcastFailedMessage(@NotNull Player player);

  protected abstract void broadcastSuccessMessage();

}
