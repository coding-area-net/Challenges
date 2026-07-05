package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.annotation.ChallengeAnnotations;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Since("2.0")
public class RandomChallengeChallenge extends TimedChallenge {

  private AbstractChallenge lastUsed;

  public RandomChallengeChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.RANDOMIZER, 3, 60, 6, false, new ItemStack(Material.REDSTONE), "random-challenge");
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-time-seconds-description").asArray(getValue() * 10);
//  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeSecondsValueChangeTitle(this, getValue() * 10);
  }

  @Override
  protected void onEnable() {
    bossbar.setContent((bossbar, player) -> {
      if (lastUsed == null) {
        bossbar.setTitle(getChallengeMessageKey("bossbar-waiting"));
        return;
      }
      bossbar.setProgress(getProgress());
      bossbar.setTitle(getChallengeMessageKey("bossbar-current"), lastUsed.getChallengeName());
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
    challenges.removeIf(ChallengeAnnotations::isCanInstaKillOnEnable);
    challenges.removeIf(ChallengeAnnotations::isExcludedFromRandomChallenges);
    challenges.removeIf(IChallenge::isEnabled);
    if (challenges.isEmpty()) return;

    AbstractChallenge challenge = (AbstractChallenge) globalRandom.choose(challenges);
    getChallengeMessageKey("challenge-enabled").broadcast(Prefix.CHALLENGES, challenge.getChallengeName(), challenge.getChallengeDescription());

    setEnabled(challenge, true);
    lastUsed = challenge;
    bossbar.update();

  }

  private void setEnabled(@NotNull IChallenge challenge, boolean enabled) {
    if (challenge instanceof Setting setting) {
      setting.setEnabled(enabled);
    }
    if (challenge instanceof SettingModifier setting) {
      setting.setEnabled(enabled);
    }

    if (enabled && challenge instanceof TimedChallenge timedChallenge) {
      if (timedChallenge.isTimerRunning()) {
        int seconds = globalRandom.range(10, 20);
        if (seconds < timedChallenge.getSecondsLeftUntilNextActivation())
          timedChallenge.shortCountDownTo(seconds);
      }
    }
  }

}
