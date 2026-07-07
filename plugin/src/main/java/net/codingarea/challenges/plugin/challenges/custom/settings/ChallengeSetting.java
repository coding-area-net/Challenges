package net.codingarea.challenges.plugin.challenges.custom.settings;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Supplier;

public abstract class ChallengeSetting implements IChallengeSetting {

  private final String name;
  private final SubSettingsBuilder subSettingsBuilder;

  public ChallengeSetting(String name, SubSettingsBuilder subSettingsBuilder) {
    this.name = name;
    this.subSettingsBuilder = subSettingsBuilder.build();
  }

  public ChallengeSetting(String name) {
    this(name, SubSettingsBuilder.createEmpty());
  }

  public ChallengeSetting(String name, Supplier<SubSettingsBuilder> builderSupplier) {
    this(name, builderSupplier.get());
  }

  public String getRelativeMessageKey() {
    return name.toLowerCase();
  }

  @NotNull
  @Override
  public ItemBuilder getDisplayItem(@NotNull Locale locale) {
    return DefaultItems.createChallengeDisplayFormat(new ItemStack(getMaterial()), getSettingName(), getSettingDescription(), locale);
  }

  @NotNull
  @Override
  public SubSettingsBuilder getSubSettingsBuilder() {
    return subSettingsBuilder;
  }

  @NotNull
  @Override
  public String getUniqueName() {
    return name;
  }

}
