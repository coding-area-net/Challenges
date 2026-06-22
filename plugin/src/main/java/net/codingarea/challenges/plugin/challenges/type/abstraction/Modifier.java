package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.commons.common.config.Document;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Modifier extends AbstractChallenge implements IModifier {

  private final int max, min;
  private final int defaultValue;
  private int value;

  public Modifier(@NotNull MenuType menu, @Nullable SettingCategory category, int max,
                  @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    this(menu, category, 1, max, displayItemPreset, nameMessageKey);
  }

  public Modifier(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max,
                  @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    this(menu, category, min, max, min, displayItemPreset, nameMessageKey);
  }

  public Modifier(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max, int defaultValue,
                  @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, displayItemPreset, nameMessageKey);
    if (max < min) throw new IllegalArgumentException("max < min");
    if (min < 0) throw new IllegalArgumentException("min < 0");
    if (defaultValue > max) throw new IllegalArgumentException("defaultValue > max");
    if (defaultValue < min) throw new IllegalArgumentException("defaultValue < min");
    this.max = max;
    this.min = min;
    this.value = defaultValue;
    this.defaultValue = defaultValue;
  }

  @NotNull
  @Override
  public ItemStack getSettingsItemPreset() {
    return DefaultItems.createValuePreset(value);
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingsName() {
    return MessageKey.of("challenge.settings-modifier-value").withArgs(value);
  }

  @Override
  public void restoreDefaults() {
    setValue(defaultValue);
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

    try {
      if (isEnabled()) onValueChange();
    } catch (Exception exception) {
      Challenges.getInstance().getILogger().error("Error while modifying value of Setting {}", getClass().getSimpleName(), exception);
    }

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
  public boolean isEnabled() {
    return true;
  }

  @Override
  public void handleClick(@NotNull ChallengeMenuClickInfo info) {
    ChallengeHelper.handleModifierClick(info, this);
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChangeChallengeValueTitle(this);
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
