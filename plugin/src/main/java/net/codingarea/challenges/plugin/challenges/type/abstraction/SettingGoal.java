package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SettingGoal extends Setting implements IGoal {

  public SettingGoal() {
    super(MenuType.GOAL);
  }

  public SettingGoal(boolean enabledByDefault) {
    super(MenuType.GOAL, enabledByDefault);
  }

  @Nonnull
  public SoundSample getStartSound() {
    return SoundSample.DRAGON_BREATH;
  }

  @Nullable
  @Override
  public SoundSample getWinSound() {
    return SoundSample.WIN;
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (isEnabled() == enabled) return;
    GoalHelper.handleSetEnabled(this, enabled);
    super.setEnabled(enabled);
  }

}
