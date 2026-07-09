package net.codingarea.challenges.plugin.challenges.custom.settings;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SelectableKey;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public interface IChallengeSetting extends SelectableKey {

  @NotNull
  SubSettingsBuilder getSubSettingsBuilder();

  @NotNull
  String getUniqueName();

  @NotNull
  ItemBuilder getDisplayItem(@NotNull Locale locale);

  @NotNull
  LocalizableMessage getSettingName();

  @NotNull
  LocalizableMessage getSettingDescription();

}
