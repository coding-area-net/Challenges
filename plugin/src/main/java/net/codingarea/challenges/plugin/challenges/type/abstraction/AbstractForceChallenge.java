package net.codingarea.challenges.plugin.challenges.type.abstraction;

import lombok.Setter;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeBossBar.BossBarInstance;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

@Setter
public abstract class AbstractForceChallenge extends TimedChallenge {

  public static final int WAITING = 0, COUNTDOWN = 1;

  private int state = WAITING;

  public AbstractForceChallenge(@NotNull MenuType menu, @Nullable SettingCategory category,
                                @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, false, displayItemPreset, nameMessageKey);
  }

  public AbstractForceChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int max, @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, max, false, displayItemPreset, nameMessageKey);
  }

  public AbstractForceChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max,
                                @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, min, max, false, displayItemPreset, nameMessageKey);
  }

  public AbstractForceChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max, int defaultValue,
                                @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, min, max, defaultValue, false, displayItemPreset, nameMessageKey);
  }

  @Override
  protected void onTimeActivation() {
    switch (state) {
      case WAITING:
        state = COUNTDOWN;
        chooseForcing();
        restartTimer(getForcingTime());
        SoundSample.BASS_ON.broadcast();
        bossbar.update();
        break;
      case COUNTDOWN:
        state = WAITING;
        restartTimer();
        handleCountdownEnd();
        bossbar.update();
        break;
    }
  }

  protected final void endForcing() {
    state = WAITING;
    restartTimer();
    bossbar.update();
  }

  protected abstract void handleCountdownEnd();

  @Override
  protected void handleCountdown() {
    bossbar.update();
  }

  @Override
  protected void onEnable() {
    bossbar.setContent(setupBossbar());
    bossbar.show();
  }

  @Override
  protected void onDisable() {
    bossbar.hide();
  }

  @Override
  public void loadGameState(@NotNull Document document) {
    super.loadGameState(document);
    if (document.isEmpty()) {
      state = WAITING;
    }
  }

  protected abstract void chooseForcing();

  protected abstract int getForcingTime();

  @NotNull
  protected abstract BiConsumer<BossBarInstance, Player> setupBossbar();

  public final int getState() {
    return state;
  }

}
