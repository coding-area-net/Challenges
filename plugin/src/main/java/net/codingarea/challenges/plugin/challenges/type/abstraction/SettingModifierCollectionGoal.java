package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SettingModifierCollectionGoal extends ModifierCollectionGoal {

  public SettingModifierCollectionGoal(@Nullable SettingCategory category, int min, int max,
                                       @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey, @NotNull Object... target) {
    super(category, min, max, displayItemPreset, nameMessageKey, target);
  }

  public SettingModifierCollectionGoal(@Nullable SettingCategory category, int min, int max, int defaultValue,
                                       @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey, @NotNull Object... target) {
    super(category, min, max, defaultValue, displayItemPreset, nameMessageKey, target);
  }

  @Override
  public void handleClick(@NotNull ChallengeMenuClickInfo info) {
    if (info.isUpperItemClick() || !enabled) {
      setEnabled(!enabled);
      SoundSample.playStatusSound(info.getPlayer(), enabled);
      playStatusUpdateTitle();
    } else {
      super.handleClick(info);
    }
  }

  @Override
  public void restoreDefaults() {
    super.restoreDefaults();
    setEnabled(false);
  }

//  @NotNull
//  @Override
//  public ItemStack getSettingsItem() {
//    return isEnabled() ? super.getSettingsItem() : DefaultItem.disabled().build();
//  }
//
//  @NotNull
//  @Override
//  public LegacyItemBuilder createSettingsItem() {
//    return DefaultItem.enabled().setAmount(getValue());
//  }

  @Override
  public void handleShutdown() {
    super.handleShutdown();
    onDisable();
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }


  @Override
  public void writeSettings(@NotNull Document document) {
    super.writeSettings(document);
    document.set("enabled", enabled);
  }

  @Override
  public void loadSettings(@NotNull Document document) {
    super.loadSettings(document);
    setEnabled(document.getBoolean("enabled"));
  }

}
