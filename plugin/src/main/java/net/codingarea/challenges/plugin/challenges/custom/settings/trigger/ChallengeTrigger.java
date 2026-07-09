package net.codingarea.challenges.plugin.challenges.custom.settings.trigger;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SelectableKey;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
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

  public static LinkedHashMap<String, SelectableKey> getMenuItems() {
    LinkedHashMap<String, SelectableKey> map = new LinkedHashMap<>();

    for (ChallengeTrigger value : Challenges.getInstance().getCustomSettingsLoader().getTriggers().values()) {
      map.put(value.getUniqueName(), value); // TODO
    }

    return map;
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingName() {
    return MessageKey.of("custom.trigger." + getRelativeMessageKey() + ".name");
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingDescription() {
    return MessageKey.of("custom.trigger." + getRelativeMessageKey() + ".desc");
  }

}
