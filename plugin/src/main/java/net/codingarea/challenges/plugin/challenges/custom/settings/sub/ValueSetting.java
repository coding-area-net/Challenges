package net.codingarea.challenges.plugin.challenges.custom.settings.sub;

import lombok.Getter;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;

public abstract class ValueSetting {

  @Getter
  private final String key;
  private final LegacyItemBuilder itemBuilder;

  public ValueSetting(String key, LegacyItemBuilder itemBuilder) {
    this.key = key;
    this.itemBuilder = itemBuilder;
  }

  public LegacyItemBuilder createDisplayItem() {
    return itemBuilder;
  }

  public abstract String onClick(MenuClickInfo info, String value, int slotIndex);

  public LegacyItemBuilder getDisplayItem(String value) {
    return createDisplayItem().hideAttributes();
  }

  public abstract LegacyItemBuilder getSettingsItem(String value);

}
