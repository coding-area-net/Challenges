package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Since("2.0")
public class RandomChallengeChallenge extends TimedChallenge {

  private AbstractChallenge lastUsed;

  public RandomChallengeChallenge() {
    super(MenuType.CHALLENGES, 3, 60, 6, false);
    setCategory(SettingCategory.RANDOMIZER);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.REDSTONE, Message.forName("item-random-challenge-challenge"));
  }

  @Nullable
  @Override
  protected String[] getSettingsDescription() {
    return Message.forName("item-time-seconds-description").asArray(getValue() * 10);
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeSecondsValueChangeTitle(this, getValue() * 10);
  }

  @Override
  protected void onEnable() {
    bossbar.setContent((bossbar, player) -> {
      if (lastUsed == null) {
        bossbar.setTitle(Message.forName("bossbar-random-challenge-waiting").asString());
        return;
      }
      bossbar.setProgress(getProgress());
      bossbar.setTitle(Message.forName("bossbar-random-challenge-current").asString(ChallengeHelper.getColoredChallengeName(lastUsed)));
    });
    bossbar.show();
  }

  @Override
  protected void onDisable() {
    if (lastUsed != null) {
      setEnabled(lastUsed, false);
      lastUsed = null;
    }
    bossbar.hide();
  }

  @Override
  protected int getSecondsUntilNextActivation() {
    return getValue() * 10;
  }

  @Override
  protected void handleCountdown() {
    bossbar.update();
  }

  @Override
  protected void onTimeActivation() {
    restartTimer();

    if (lastUsed != null) {
      setEnabled(lastUsed, false);
      lastUsed = null;
    }

    List<IChallenge> challenges = new ArrayList<>(Challenges.getInstance().getChallengeManager().getChallenges());
    challenges.remove(this);
    challenges.removeIf(challenge -> challenge.getType() != MenuType.CHALLENGES);
    challenges.removeIf(challenge -> !(challenge instanceof AbstractChallenge));
    challenges.removeIf(ChallengeHelper::canInstaKillOnEnable);
    challenges.removeIf(ChallengeHelper::isExcludedFromRandomChallenges);
    challenges.removeIf(IChallenge::isEnabled);
    if (challenges.isEmpty()) return;

    AbstractChallenge challenge = (AbstractChallenge) globalRandom.choose(challenges);
    String name = ChallengeHelper.getColoredChallengeName(challenge);
    Message.forName("random-challenge-enabled").broadcast(Prefix.CHALLENGES, name);

    setEnabled(challenge, true);
    lastUsed = challenge;
    bossbar.update();

  }

  private void setEnabled(@Nonnull IChallenge challenge, boolean enabled) {
    if (challenge instanceof Setting) {
      Setting setting = (Setting) challenge;
      setting.setEnabled(enabled);
    }
    if (challenge instanceof SettingModifier) {
      SettingModifier setting = (SettingModifier) challenge;
      setting.setEnabled(enabled);
    }

    if (enabled && challenge instanceof TimedChallenge) {
      TimedChallenge timedChallenge = (TimedChallenge) challenge;
      if (timedChallenge.isTimerRunning()) {
        int seconds = globalRandom.range(10, 20);
        if (seconds < timedChallenge.getSecondsLeftUntilNextActivation())
          timedChallenge.shortCountDownTo(seconds);
      }
    }
  }

}
