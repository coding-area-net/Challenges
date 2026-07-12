package net.codingarea.challenges.plugin.challenges.custom.settings.sub.impl;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.ValueSetting;
import net.codingarea.challenges.plugin.challenges.type.IModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ModifierSetting extends ValueSetting implements IModifier {

  // TODO unify desc/name space with other selectable options!

  private final int min, max;
  private final Function<? super Integer, LocalizableMessage> settingsFormatGetter;

  private int tempValue;

  public ModifierSetting(@NotNull String key, @NotNull ItemStack displayItemPreset, @NotNull MessageKey messageKeyNamespace,
                         int min, int max) {
    this(key, displayItemPreset, messageKeyNamespace, min, max, ChallengeHelper::getSettingsDescriptionModifierValue);
  }

  public ModifierSetting(@NotNull String key, @NotNull ItemStack displayItemPreset, @NotNull MessageKey messageKeyNamespace,
                         int min, int max,
                         @NotNull Function<? super Integer, LocalizableMessage> settingsFormatGetter) {
    super(key, displayItemPreset, messageKeyNamespace);
    this.min = min;
    this.max = max;
    this.settingsFormatGetter = settingsFormatGetter;
  }

  @Override
  public String onClick(MenuClickInfo info, String value, int slotIndex) {

    int intValue = getIntValue(value);
    tempValue = intValue;

    ChallengeHelper.handleModifierClick(info, this);

    intValue = tempValue;
    tempValue = 0;
    return String.valueOf(intValue);
  }

  @Override
  public int getValue() {
    return tempValue;
  }

  @Override
  public void setValue(int value) {
    tempValue = value;
  }

  @Override
  public int getMinValue() {
    return min;
  }

  @Override
  public int getMaxValue() {
    return max;
  }

  @Override
  public void playValueChangeTitle() {
  }

  @NotNull
  @Override
  public ItemStack getSettingsItemPreset(@NotNull String value) {
    int intValue = getIntValue(value);
    return DefaultItems.createValuePreset(intValue);
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingsName(@NotNull String value) {
    int intValue = getIntValue(value);
    return settingsFormatGetter.apply(intValue);
  }

  public int getIntValue(String value) {

    try {
      return Integer.parseInt(value);
    } catch (Exception exception) {
      Challenges.getInstance().getILogger().severe("Something went wrong while parsing the "
        + "value of subsetting " + getKey() + " with value " + value);
      Challenges.getInstance().getILogger().error("", exception);
    }

    return 0;
  }

}
