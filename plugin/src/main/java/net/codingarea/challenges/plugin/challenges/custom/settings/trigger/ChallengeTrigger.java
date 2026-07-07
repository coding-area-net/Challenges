package net.codingarea.challenges.plugin.challenges.custom.settings.trigger;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.function.Supplier;

public abstract class ChallengeTrigger extends ChallengeSetting implements IChallengeTrigger {

  public ChallengeTrigger(String name,
                          SubSettingsBuilder subSettingsBuilder) {
    super(name, subSettingsBuilder);
  }

  public ChallengeTrigger(String name) {
    super(name);
  }

  public ChallengeTrigger(String name, Supplier<SubSettingsBuilder> builderSupplier) {
    super(name, builderSupplier);
  }

  public static LinkedHashMap<String, ItemStack> getMenuItems() {
    LinkedHashMap<String, ItemStack> map = new LinkedHashMap<>();

    for (ChallengeTrigger value : Challenges.getInstance().getCustomSettingsLoader().getTriggers().values()) {
      map.put(value.getUniqueName(), value.getDisplayItem(Locale.GERMAN).build()); // TODO
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
