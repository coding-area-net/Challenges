package net.codingarea.challenges.plugin.challenges.custom.settings.sub;

import lombok.Getter;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public abstract class ValueSetting {

  @Getter
  private final String key;
  private final ItemStack displayItemPreset;
  private final MessageKey messageKeyNamespace;

  public ValueSetting(@NotNull String key, @NotNull ItemStack displayItemPreset, @NotNull MessageKey messageKeyNamespace) {
    this.key = key;
    this.displayItemPreset = displayItemPreset;
    this.messageKeyNamespace = messageKeyNamespace;
  }

  public abstract String onClick(MenuClickInfo info, String value, int slotIndex);

  @NotNull
  public ItemBuilder getDisplayItem(@NotNull String value, @NotNull Locale locale) {
    return DefaultItems.createChallengeDisplayFormat(displayItemPreset, getValueSettingName(), getValueSettingDescription(), locale);
  }

  @NotNull
  public ItemBuilder getSettingsItem(@NotNull String value, @NotNull Locale locale) {
    return DefaultItems.createChallengeSettingFormat(getSettingsItemPreset(value), getSettingsName(value), null, locale);
  }

  @NotNull
  public abstract ItemStack getSettingsItemPreset(@NotNull String value);

  @NotNull
  public LocalizableMessage getSubName() {
    return getValueSettingMessageKey("sub-name");
  }

  @NotNull
  public LocalizableMessage getValueSettingName() {
    return getValueSettingMessageKey("name");
  }

  @Nullable
  public LocalizableMessage getValueSettingDescription() {
    return getValueSettingMessageKey("desc");
  }

  @NotNull
  protected MessageKey getValueSettingMessageKey(@NotNull String keySuffix) {
    return messageKeyNamespace.getChildKey(keySuffix);
  }

  @NotNull
  public abstract LocalizableMessage getSettingsName(@NotNull String value);

}
