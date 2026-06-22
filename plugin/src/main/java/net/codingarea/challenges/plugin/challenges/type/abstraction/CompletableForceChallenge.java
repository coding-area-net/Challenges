package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CompletableForceChallenge extends AbstractForceChallenge {

  public CompletableForceChallenge(@NotNull MenuType menu, @Nullable SettingCategory category,
                                   @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, displayItemPreset, nameMessageKey);
  }

  public CompletableForceChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int max,
                                   @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, max, displayItemPreset, nameMessageKey);
  }

  public CompletableForceChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max,
                                   @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, min, max, displayItemPreset, nameMessageKey);
  }

  public CompletableForceChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max, int defaultValue,
                                   @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, min, max, defaultValue, displayItemPreset, nameMessageKey);
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
