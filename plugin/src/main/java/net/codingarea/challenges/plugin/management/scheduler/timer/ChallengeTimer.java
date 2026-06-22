package net.codingarea.challenges.plugin.management.scheduler.timer;

import lombok.Getter;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.impl.TimerMenuGenerator;
import net.codingarea.challenges.plugin.management.scheduler.policy.PlayerCountPolicy;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.config.FileDocument;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ChallengeTimer {

  @Getter
  private final TimerFormat format;
  private final boolean specificStartSounds, defaultStartSound;
  @Getter
  private long time = 0;
  @Getter
  private boolean countingUp = true;
  @Getter
  private boolean paused = true;
  private boolean hidden = false;
  private boolean sentEmpty;

  public ChallengeTimer() {

    Document pluginConfig = Challenges.getInstance().getConfigDocument();
    specificStartSounds = pluginConfig.getBoolean("enable-specific-start-sounds");
    defaultStartSound = pluginConfig.getBoolean("enable-default-start-sounds");

    Document formatConfig = pluginConfig.getDocument("timer.format");
    format = new TimerFormat(formatConfig);

    Challenges.getInstance().getScheduler().register(this);
  }

  public void enable() {
    updateTimeRule();
  }

  private void updateTimeRule() {
    for (World world : ChallengeAPI.getGameWorlds()) {
      // overhaul post-1.21: DO_DAYLIGHT_CYCLE -> ADVANCE_TIME
      world.setGameRule(MinecraftNameWrapper.DAYLIGHT_CYCLE, !paused);
    }
  }

  @ScheduledTask(ticks = 20, async = false, timerPolicy = TimerPolicy.ALWAYS, playerPolicy = PlayerCountPolicy.ALWAYS)
  public void onTimerSecond() {

    if (!paused) {
      if (countingUp) time++;
      else time--;

      if (time <= 0) {
        time = 0;
        countingUp = true;
        handleHitZero();
      }
    }

    updateActionbar();

  }

  @ScheduledTask(ticks = 20, timerPolicy = TimerPolicy.PAUSED)
  public void playPausedParticles() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (AbstractChallenge.ignorePlayer(player)) continue;
      Location location = player.getLocation();
      if (location.getWorld() == null) continue;
      location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 1);
    }
  }

  private void handleHitZero() {
    ChallengeAPI.endChallenge(ChallengeEndCause.TIMER_HIT_ZERO);
  }

  public void resume() {
    if (!paused) return;
    paused = false;

    updateActionbar();
    updateTimeRule();

    MessageKey.of("timer-was-started").broadcast(Prefix.TIMER);
    Challenges.getInstance().getScheduler().fireTimerStatusChange();
    Challenges.getInstance().getTitleManager().sendTimerStatusTitle(MessageKey.of("title-timer-started"));
    Challenges.getInstance().getServerManager().setNotFresh();

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getGameMode() != GameMode.CREATIVE)
        player.setGameMode(GameMode.SURVIVAL);
    }

    IGoal currentGoal = Challenges.getInstance().getChallengeManager().getCurrentGoal();
    if (currentGoal != null && specificStartSounds) {
      currentGoal.getStartSound().broadcast();
    } else if (defaultStartSound) {
      SoundSample.DRAGON_BREATH.broadcast();
    }

  }

  public void pause(boolean playInGameEffects) {
    if (paused) return;
    paused = true;

    updateActionbar();
    updateTimeRule();

    Challenges.getInstance().getScheduler().fireTimerStatusChange();
    if (playInGameEffects) {
      Challenges.getInstance().getTitleManager().sendTimerStatusTitle(MessageKey.of("title-timer-paused"));
      MessageKey.of("timer-was-paused").broadcast(Prefix.TIMER);
      SoundSample.BASS_OFF.broadcast();
    }
  }

  public void toggle() {
    if (isStarted()) {
      pause(true);
    } else {
      resume();
    }
  }

  public void reset() {
    if (!countingUp) pause(true);
    time = 0;
    countingUp = true;
    updateActionbar();
  }

  public void updateActionbar() {
    if (sentEmpty && hidden) return;
    if (hidden) sentEmpty = true;
    if (!hidden) {
      this.getCurrentActionbarMessage().broadcastActionBar(getFormattedTime());
    }
  }

  @NotNull
  private MessageKey getCurrentActionbarMessage() {
    if (paused) return MessageKey.of("stopped-message");
    if (countingUp) return MessageKey.of("count-up-message");
    return MessageKey.of("count-down-message");
  }

  public synchronized void loadSession() {
    FileDocument config = Challenges.getInstance().getConfigManager().getSessionConfig();
    time = config.getInt("timer.seconds");
    countingUp = config.getBoolean("timer.countingUp", true);
    hidden = config.getBoolean("timer.hidden", false);
  }

  public synchronized void saveSession(boolean async) {
    FileDocument config = Challenges.getInstance().getConfigManager().getSessionConfig();
    config.set("timer.seconds", time);
    config.set("timer.countingUp", countingUp);
    config.set("timer.hidden", hidden);
    config.save(async);
  }

  public void addSeconds(int amount) {
    time += amount;
    if (time < 0)
      time = 0;
    updateActionbar();
  }

  public void setSeconds(long seconds) {
    this.time = seconds;
    updateActionbar();
  }

  public void setHidden(boolean hide) {
    this.sentEmpty = false;
    this.hidden = hide;
    updateActionbar();
  }

  @NotNull
  public String getFormattedTime() {
    return format.format(time);
  }

  @NotNull
  public TimerStatus getStatus() {
    return paused ? TimerStatus.PAUSED : TimerStatus.RUNNING;
  }

  public boolean isStarted() {
    return !paused;
  }

  public void setCountingUp(boolean countingUp) {
    if (this.countingUp == countingUp) return;

    this.countingUp = countingUp;
    updateActionbar();
    TimerMenuGenerator menuGenerator = (TimerMenuGenerator) MenuType.TIMER.getMenuGenerator();
    menuGenerator.updatePage(TimerMenuGenerator.PAGE_STATE);
    MessageKey.of("timer-mode-set-" + (countingUp ? "up" : "down")).broadcast(Prefix.TIMER);
    SoundSample.BASS_ON.broadcast();
  }

}
