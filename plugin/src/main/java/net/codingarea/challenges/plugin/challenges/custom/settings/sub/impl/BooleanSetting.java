package net.codingarea.challenges.plugin.challenges.custom.settings.sub.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.ValueSetting;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;

public class BooleanSetting extends ValueSetting {


  public BooleanSetting(String key, LegacyItemBuilder itemBuilder) {
    super(key, itemBuilder);
  }

  @Override
  public String onClick(MenuClickInfo info, String value,
                        int slotIndex) {
    return value.equals("enabled") ? "disabled" : "enabled";
  }

  @Override
  public LegacyItemBuilder getSettingsItem(String value) {
    return value.equals("enabled") ? DefaultItem.enabled() : DefaultItem.disabled();
  }

}
