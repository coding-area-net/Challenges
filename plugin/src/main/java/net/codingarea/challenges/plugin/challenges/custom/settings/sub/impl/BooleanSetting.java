package net.codingarea.challenges.plugin.challenges.custom.settings.sub.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.ValueSetting;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BooleanSetting extends ValueSetting {

  public BooleanSetting(@NotNull String key, @NotNull ItemStack displayItemPreset, @NotNull MessageKey messageKeyNamespace) {
    super(key, displayItemPreset, messageKeyNamespace);
  }

  @Override
  public String onClick(MenuClickInfo info, String value,
                        int slotIndex) {
    return value.equals("enabled") ? "disabled" : "enabled";
  }

  @NotNull
  @Override
  public ItemStack getSettingsItemPreset(@NotNull String value) {
    return value.equals("enabled") ? DefaultItems.createEnabledPreset() : DefaultItems.createDisabledPreset();
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingsName(@NotNull String value) {
    return value.equals("enabled") ? ChallengeHelper.getChallengeEnabledName() : ChallengeHelper.getChallengeDisabledName();
  }
}
