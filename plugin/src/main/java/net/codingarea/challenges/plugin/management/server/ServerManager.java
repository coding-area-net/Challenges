package net.codingarea.challenges.plugin.management.server;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.bukkit.utils.misc.BukkitReflectionUtils;
import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.misc.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public final class ServerManager {

  private final boolean setSpectatorOnWin;
  private final boolean dropItemsOnEnd;
  private final boolean winSounds;

  private boolean isFresh; // indicates if the timer was never started before
  private boolean hasCheated;

  public ServerManager() {
    Document sessionConfig = Challenges.getInstance().getConfigManager().getSessionConfig();
    hasCheated = sessionConfig.getBoolean("cheated");
    isFresh = sessionConfig.getBoolean("fresh", true);

    Document pluginConfig = Challenges.getInstance().getConfigDocument();
    setSpectatorOnWin = pluginConfig.getBoolean("set-spectator-on-win");
    dropItemsOnEnd = pluginConfig.getBoolean("drop-items-on-end");
    winSounds = pluginConfig.getBoolean("enabled-win-sounds");
  }

  public void setNotFresh() {
    isFresh = false;
    Challenges.getInstance().getConfigManager().getSessionConfig().set("fresh", false);
  }

  public void setHasCheated() {
    hasCheated = true;
    Challenges.getInstance().getConfigManager().getSessionConfig().set("cheated", true);
  }

  public boolean isFresh() {
    return isFresh;
  }

  public boolean hasCheated() {
    return hasCheated;
  }

  public void endChallenge(@NotNull ChallengeEndCause endCause, Supplier<List<Player>> winnerGetter) {
//    if (!Bukkit.isPrimaryThread()) { // TODO
//      // calling end challenge logic async results in all kinds of issues as bukkit/paper does not allow certain actions
//      // to be performed async (in some versions): PlayerGameModeChangeEvent may only be triggered synchronously
//      Logger.debug("End challenge called from async thread, scheduling sync task (Caller: {}", ReflectionUtils.getCallerName(1));
//      Bukkit.getScheduler().callSyncMethod(Challenges.getInstance(), (Callable<Void>) () -> {
//        endChallenge(endCause, winnerGetter);
//        return null;
//      });
//      return;
//    }

    if (ChallengeAPI.isPaused()) {
      Logger.warn("{} tried to end challenge while timer was paused", ReflectionUtils.getCallerName(1));
      return;
    }

    IGoal currentGoal = Challenges.getInstance().getChallengeManager().getCurrentGoal();
    List<Player> winners = new LinkedList<>();
    if (winnerGetter != null) {
      winners = winnerGetter.get();
    } else if (currentGoal != null && endCause.isWinnable()) {
      currentGoal.getWinnersOnEnd(winners);
    }

    if (endCause != ChallengeEndCause.GOAL_REACHED || setSpectatorOnWin) {
      setSpectator();
    }
    if (endCause == ChallengeEndCause.GOAL_REACHED && winSounds && currentGoal != null && currentGoal.getWinSound() != null) {
      currentGoal.getWinSound().broadcast();
    }
    if (dropItemsOnEnd) {
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (winners.isEmpty() || winners.contains(player)) continue;
        dropItems(player);
      }
    }

    Challenges.getInstance().getChallengeTimer().pause(false);

    String seed = Bukkit.getWorlds().isEmpty() ? "?" :
      String.valueOf(ChallengeAPI.getGameWorld(Environment.NORMAL).getSeed());
    LocalizableMessage winnersFormat = LocalizableMessage.joinList(winners);
    LocalizableMessage timeFormat = Challenges.getInstance().getChallengeTimer().getFormattedTime();
    endCause.getMessage(!winners.isEmpty()).broadcast(Prefix.CHALLENGES, timeFormat, winnersFormat, seed);

  }

  private void setSpectator() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.setGameMode(GameMode.SPECTATOR);
      SoundSample.BLAST.play(player);

      try {
        player.getWorld().spawnEntity(player.getLocation(), MinecraftNameWrapper.FIREWORK);
      } catch (IllegalArgumentException ex) {
        // We cant spawn fireworks like that in some versions of spigot
      }
    }
  }

  private void dropItems(@NotNull Player player) {
    dropItems(player.getLocation(), player.getInventory().getContents());
    player.getInventory().clear();
  }

  private void dropItems(@NotNull Location location, @NotNull ItemStack[] items) {
    for (ItemStack item : items) {
      if (item == null) continue;
      if (BukkitReflectionUtils.isAir(item.getType())) continue;
      if (location.getWorld() == null) return;
      location.getWorld().dropItem(location, item);
    }
  }

}
