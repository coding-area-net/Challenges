package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class EndingForceChallenge extends AbstractForceChallenge {

  public EndingForceChallenge(@NotNull MenuType menu, @Nullable SettingCategory category,
                              @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, displayItemPreset, nameMessageKey);
  }

  public EndingForceChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int max,
                              @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, max, displayItemPreset, nameMessageKey);
  }

  public EndingForceChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max,
                              @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, min, max, displayItemPreset, nameMessageKey);
  }

  public EndingForceChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max, int defaultValue,
                              @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, min, max, defaultValue, displayItemPreset, nameMessageKey);
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
