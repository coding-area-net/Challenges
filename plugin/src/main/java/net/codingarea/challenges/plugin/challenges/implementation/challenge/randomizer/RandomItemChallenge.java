package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.impl.RandomItemAction;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Since("2.0")
public class RandomItemChallenge extends TimedChallenge {

  public RandomItemChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.RANDOMIZER, 1, 60, 30, false, new ItemStack(Material.BEACON), "random-item-challenge");
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-time-seconds-description").asArray(getValue());
//  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeSecondsValueChangeTitle(this, getValue());
  }

  @Override
  protected int getSecondsUntilNextActivation() {
    return getValue();
  }

  @Override
  protected void onTimeActivation() {
    restartTimer();
    broadcastFiltered(RandomItemAction::giveRandomItemToPlayer);
  }

}
