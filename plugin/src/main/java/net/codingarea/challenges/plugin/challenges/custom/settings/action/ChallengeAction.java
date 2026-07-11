package net.codingarea.challenges.plugin.challenges.custom.settings.action;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SelectableKey;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.commons.common.collection.IRandom;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ChallengeAction extends ChallengeSetting implements IChallengeAction {

  protected static final IRandom random = IRandom.create();

  public ChallengeAction(String name, SubSettingsBuilder subSettingsBuilder) {
    super(name, subSettingsBuilder);
  }

  public ChallengeAction(String name) {
    super(name);
  }

  public ChallengeAction(String name, Supplier<SubSettingsBuilder> builderSupplier) {
    super(name, builderSupplier);
  }

  @NotNull
  public static Map<String, SelectableKey> getMenuItems() {
    return Collections.unmodifiableMap(Challenges.getInstance().getCustomSettingsLoader().getActions());
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
    return MessageKey.of("custom.action." + getRelativeMessageKey() + "." + keySuffix);
  }
}
