package net.codingarea.challenges.plugin.challenges.custom.settings.trigger;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SelectableKey;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ChallengeTrigger extends ChallengeSetting implements IChallengeTrigger {

  public ChallengeTrigger(String name, SubSettingsBuilder subSettingsBuilder) {
    super(name, subSettingsBuilder);
  }

  public ChallengeTrigger(String name) {
    super(name);
  }

  public ChallengeTrigger(String name, Supplier<SubSettingsBuilder> builderSupplier) {
    super(name, builderSupplier);
  }

  @NotNull
  public static Map<String, SelectableKey> getMenuItems() {
    return Collections.unmodifiableMap(Challenges.getInstance().getCustomSettingsLoader().getTriggers());
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingName() {
    return getSettingMessageKey("name");
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingDescription() {
    return getSettingMessageKey("desc");
  }

  @NotNull
  protected LocalizableMessage getSettingMessageKey(@NotNull String keySuffix) {
    return MessageKey.of("custom.trigger." + getRelativeMessageKey() + "." + keySuffix);
  }

}
