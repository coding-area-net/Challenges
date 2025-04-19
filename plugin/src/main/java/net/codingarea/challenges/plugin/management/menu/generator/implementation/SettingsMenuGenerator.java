package net.codingarea.challenges.plugin.management.menu.generator.implementation;

import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.management.menu.generator.ChallengeMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;

import javax.annotation.Nonnull;

public class SettingsMenuGenerator extends ChallengeMenuGenerator {

  public static final int[] SLOTS = {10, 11, 12, 13, 14, 15, 16};
  public static final int[] NAVIGATION_SLOTS = {27, 35};
  public static final int SIZE = 4 * 9;

  @Override
  public int[] getSlots() {
    return SLOTS;
  }

  @Override
  public int getSize() {
    return SIZE;
  }

  @Override
  public int[] getNavigationSlots(int page) {
    return NAVIGATION_SLOTS;
  }

  @Override
  public void executeClickAction(@Nonnull IChallenge challenge, @Nonnull MenuClickInfo info, int itemIndex) {
    if (itemIndex <= 1) {
      challenge.handleClick(new ChallengeMenuClickInfo(info, itemIndex == 0));
    }

  }

}
