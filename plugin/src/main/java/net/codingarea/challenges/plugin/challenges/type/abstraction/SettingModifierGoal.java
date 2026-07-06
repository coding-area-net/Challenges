package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SettingModifierGoal extends SettingModifier implements IGoal {

  public SettingModifierGoal(@NotNull MenuType menu, @Nullable SettingCategory category,
                             @NotNull ItemStack displayItemPreset, @NotNull String messageNameKey) {
    super(menu, category, displayItemPreset, messageNameKey);
  }

  public SettingModifierGoal(@NotNull MenuType menu, @Nullable SettingCategory category, int max,
                             @NotNull ItemStack displayItemPreset, @NotNull String messageNameKey) {
    super(menu, category, max, displayItemPreset, messageNameKey);
  }

  public SettingModifierGoal(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max,
                             @NotNull ItemStack displayItemPreset, @NotNull String messageNameKey) {
    super(menu, category, min, max, displayItemPreset, messageNameKey);
  }

  public SettingModifierGoal(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max, int defaultValue,
                             @NotNull ItemStack displayItemPreset, @NotNull String messageNameKey) {
    super(menu, category, min, max, defaultValue, displayItemPreset, messageNameKey);
  }

  @Override
  public final void setEnabled(boolean enabled) {
    if (isEnabled() == enabled) return;
    GoalHelper.handleSetEnabled(this, enabled);
    super.setEnabled(enabled);
  }

  @NotNull
  @Override
  public SoundSample getStartSound() {
    return SoundSample.DRAGON_BREATH;
  }

  @Nullable
  @Override
  public SoundSample getWinSound() {
    return SoundSample.WIN;
  }

  @Override
  public void handleClick(@NotNull ChallengeMenuClickInfo info) {
    if (info.isLowerItemClick() && isEnabled()) {
      ChallengeHelper.handleModifierClick(info, this);
    } else {
      setEnabled(!isEnabled());
      SoundSample.playStatusSound(info.getPlayer(), isEnabled());
      playStatusUpdateTitle();
    }
  }

  @Override
  public void playStatusUpdateTitle() {
    ChallengeHelper.playGoalToggleTitle(this);
  }
}
