package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SettingGoal extends Setting implements IGoal {

  public SettingGoal(@Nullable SettingCategory category, @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(MenuType.GOAL, category, displayItemPreset, nameMessageKey);
  }

  public SettingGoal(@Nullable SettingCategory category, boolean enabledByDefault,
                     @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(MenuType.GOAL, category, enabledByDefault, displayItemPreset, nameMessageKey);
  }

  @NotNull
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

  @Override
  public void playStatusUpdateTitle() {
    ChallengeHelper.playGoalToggleTitle(this);
  }
}
