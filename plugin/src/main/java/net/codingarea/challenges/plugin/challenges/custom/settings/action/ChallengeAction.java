package net.codingarea.challenges.plugin.challenges.custom.settings.action;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.commons.common.collection.IRandom;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Locale;
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

  public static LinkedHashMap<String, ItemStack> getMenuItems() {
    LinkedHashMap<String, ItemStack> map = new LinkedHashMap<>();

    for (ChallengeAction value : Challenges.getInstance().getCustomSettingsLoader().getActions().values()) {
      map.put(value.getUniqueName(), value.getDisplayItem(Locale.GERMAN).build()); // TODO
    }

    return map;
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingName() {
    return MessageKey.of("custom.action." + getRelativeMessageKey() + ".name");
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingDescription() {
    return MessageKey.of("custom.action." + getRelativeMessageKey() + ".desc");
  }
}
