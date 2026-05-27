package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SettingModifierGoal extends SettingModifier implements IGoal {

  public SettingModifierGoal(@NotNull MenuType menu) {
    super(menu);
  }

  public SettingModifierGoal(@NotNull MenuType menu, int max) {
    super(menu, max);
  }

  public SettingModifierGoal(@NotNull MenuType menu, int min, int max) {
    super(menu, min, max);
  }

  public SettingModifierGoal(@NotNull MenuType menu, int min, int max, int defaultValue) {
    super(menu, min, max, defaultValue);
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

}
