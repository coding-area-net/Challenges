package net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous;

import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.challenges.annotations.ExcludeFromRandomChallenges;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Since("2.0")
@ExcludeFromRandomChallenges
public class InvertHealthChallenge extends TimedChallenge {

  public InvertHealthChallenge() {
    super(MenuType.CHALLENGES, 1, 10, 5, false);
  }

  public static void invertHealth(Player player) {
    double health = player.getMaxHealth() - player.getHealth();
    if (health <= 0) {
      ChallengeHelper.kill(player);
      return;
    }
    player.setHealth(health);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.POPPY, Message.forName("item-invert-health-challenge"));
  }

  @Nullable
  @Override
  protected String[] getSettingsDescription() {
    return Message.forName("item-time-seconds-range-description").asArray(getValue() * 60 - 20, getValue() * 60 + 20);
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeSecondsRangeValueChangeTitle(this, getValue() * 60 - 20, getValue() * 60 + 20);
  }

  @Override
  protected int getSecondsUntilNextActivation() {
    return globalRandom.around(getValue() * 60, 20);
  }

  @Override
  protected void onTimeActivation() {
    SoundSample.PLOP.broadcast();
    Message.forName("health-inverted").broadcast(Prefix.CHALLENGES);
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (ignorePlayer(player)) continue;
      invertHealth(player);
    }
    restartTimer();
  }

}
