package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.challenges.type.IModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.commons.common.config.Document;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ModifierCollectionGoal extends CollectionGoal implements IModifier {

  private final int max, min;
  private final int defaultValue;
  private int value;

  public ModifierCollectionGoal(@Nullable SettingCategory category, int min, int max,
                                @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey,
                                @NotNull Object[] target) {
    this(category, min, max, min, displayItemPreset, nameMessageKey, target);
  }

  public ModifierCollectionGoal(@Nullable SettingCategory category, int min, int max, int defaultValue,
                                @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey,
                                @NotNull Object[] target) {
    super(category, displayItemPreset, nameMessageKey, target);
    if (max < min) throw new IllegalArgumentException("max < min");
    if (min < 0) throw new IllegalArgumentException("min < 0");
    if (defaultValue > max) throw new IllegalArgumentException("defaultValue > max");
    if (defaultValue < min) throw new IllegalArgumentException("defaultValue < min");
    this.max = max;
    this.min = min;
    this.value = defaultValue;
    this.defaultValue = defaultValue;
  }

  @Override
  public void restoreDefaults() {
    setValue(defaultValue);
  }

  @Override
  public void handleClick(@NotNull ChallengeMenuClickInfo info) {
    ChallengeHelper.handleModifierClick(info, this);
  }

//  @Override // TODO why was this overridden??
//  public boolean isEnabled() {
//    return true;
//  }

  @Override
  public void setEnabled(boolean enabled) {
    if (isEnabled() == enabled) return;
    GoalHelper.handleSetEnabled(this, enabled);
    super.setEnabled(enabled);
  }

  @Override
  public final int getValue() {
    return value;
  }

  @Override
  public void setValue(int value) {
    if (value > max) throw new IllegalArgumentException("value > max");
    if (value < min) throw new IllegalArgumentException("value < min");
    this.value = value;

    if (isEnabled()) onValueChange();

    updateItems();
  }

  @Override
  public final int getMaxValue() {
    return max;
  }

  @Override
  public final int getMinValue() {
    return min;
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeValueTitle(this);
  }

  protected void onValueChange() {
  }

  @Override
  public void loadSettings(@NotNull Document document) {
    setValue(document.getInt("value", value));
  }

  @Override
  public void writeSettings(@NotNull Document document) {
    document.set("value", value);
  }

}
