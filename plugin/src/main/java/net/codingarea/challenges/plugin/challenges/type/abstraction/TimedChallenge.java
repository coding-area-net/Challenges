package net.codingarea.challenges.plugin.challenges.type.abstraction;

import lombok.Setter;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.common.config.Document;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TimedChallenge extends SettingModifier {

  private final boolean runAsync;
  @Setter
  protected int secondsUntilActivation;
  private int originalSecondsUntilActivation;
  private boolean timerStatus = false;
  private boolean startedBefore = false;

  public TimedChallenge(@NotNull MenuType menu, @Nullable SettingCategory category,
                        @NotNull ItemStack displayItemPreset, @NotNull String messageNameKey) {
    this(menu, category, true, displayItemPreset, messageNameKey);
  }

  public TimedChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int max,
                        @NotNull ItemStack displayItemPreset, @NotNull String messageNameKey) {
    this(menu, category, max, true, displayItemPreset, messageNameKey);
  }

  public TimedChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max,
                        @NotNull ItemStack displayItemPreset, @NotNull String messageNameKey) {
    this(menu, category, min, max, true, displayItemPreset, messageNameKey);
  }

  public TimedChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max, int defaultValue,
                        @NotNull ItemStack displayItemPreset, @NotNull String messageNameKey) {
    this(menu, category, min, max, defaultValue, true, displayItemPreset, messageNameKey);
  }

  public TimedChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, boolean runAsync,
                        @NotNull ItemStack displayItemPreset, @NotNull String messageNameKey) {
    super(menu, category, displayItemPreset, messageNameKey);
    this.runAsync = runAsync;
  }

  public TimedChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int max, boolean runAsync,
                        @NotNull ItemStack displayItemPreset, @NotNull String messageNameKey) {
    super(menu, category, max, displayItemPreset, messageNameKey);
    this.runAsync = runAsync;
  }

  public TimedChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max, boolean runAsync,
                        @NotNull ItemStack displayItemPreset, @NotNull String messageNameKey) {
    super(menu, category, min, max, displayItemPreset, messageNameKey);
    this.runAsync = runAsync;
  }

  public TimedChallenge(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max, int defaultValue, boolean runAsync,
                        @NotNull ItemStack displayItemPreset, @NotNull String messageNameKey) {
    super(menu, category, min, max, defaultValue, displayItemPreset, messageNameKey);
    this.runAsync = runAsync;
  }

  @Override
  public void setValue(int value) {
    super.setValue(value);
    if (!timerStatus) {
      restartTimer();
    }
  }

  // Don't execute async to prevent sync issues with timer
  @ScheduledTask(ticks = 20, async = false)
  public final void handleTimedChallengeSecond() {

    if (!startedBefore)
      restartTimer();

    if (timerStatus) {

      if (getTimerTrigger()) {
        secondsUntilActivation--;
        if (secondsUntilActivation <= 0) {
          secondsUntilActivation = 0;
          timerStatus = false;
          executeTimeActivation();
        } else {
          handleCountdown();
        }
      } else {
        Logger.debug("getTimerTrigger returned false for {}", this.getClass().getSimpleName());
      }
    }

  }

  public final void executeTimeActivation() {
    if (runAsync) {
      Bukkit.getScheduler().runTaskAsynchronously(plugin, this::onTimeActivation);
    } else {
      Bukkit.getScheduler().runTask(plugin, this::onTimeActivation);
    }
  }

  public final void shortCountDownTo(int seconds) {
    if (!timerStatus) throw new IllegalArgumentException("Countdown is not started");
    if (seconds > originalSecondsUntilActivation)
      throw new IllegalArgumentException("Cannot short countdown to a higher length than originally set");
    this.secondsUntilActivation = seconds;
  }

  public final boolean isTimerRunning() {
    return timerStatus;
  }

  public final int getSecondsLeftUntilNextActivation() {
    return secondsUntilActivation;
  }

  public final int getOriginalSecondsUntilActivation() {
    return originalSecondsUntilActivation;
  }

  protected float getProgress() {
    return getOriginalSecondsUntilActivation() == 0 ? 1 : (float) (getSecondsLeftUntilNextActivation()) / getOriginalSecondsUntilActivation();
  }

  protected void handleCountdown() {
  }

  protected boolean getTimerTrigger() {
    return true;
  }

  protected abstract int getSecondsUntilNextActivation();

  protected void restartTimer(int seconds) {
    Logger.debug("Restarting timer of {} with {} second(s)", this.getClass().getSimpleName(), seconds);

    startedBefore = true;
    secondsUntilActivation = seconds;
    originalSecondsUntilActivation = seconds;
    timerStatus = true;
  }

  protected void restartTimer() {
    restartTimer(getSecondsUntilNextActivation());
  }

  @Override
  public void loadGameState(@NotNull Document document) {
    if (document.isEmpty()) {
      startedBefore = true;
      timerStatus = true;
      restartTimer();
    } else if (document.contains("time")) {
      startedBefore = true;
      timerStatus = true;
      secondsUntilActivation = document.getInt("time");
      Logger.debug("Starting timer of {} from gamestate value with {} second(s)", this.getClass().getSimpleName(), secondsUntilActivation);
    }
  }

  @Override
  public void writeGameState(@NotNull Document document) {
    if (secondsUntilActivation != originalSecondsUntilActivation) {
      document.set("time", secondsUntilActivation);
    }
  }

  protected abstract void onTimeActivation();

}
