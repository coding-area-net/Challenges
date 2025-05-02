package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class SettingModifierCollectionGoal extends ModifierCollectionGoal {

  public SettingModifierCollectionGoal(int min, int max, @Nonnull Object... target) {
    super(min, max, target);
  }

  public SettingModifierCollectionGoal(int min, int max, int defaultValue, @Nonnull Object... target) {
    super(min, max, defaultValue, target);
  }

  @Override
  public void handleClick(@Nonnull ChallengeMenuClickInfo info) {
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

  @Nonnull
  @Override
  public ItemStack getSettingsItem() {
    return isEnabled() ? super.getSettingsItem() : DefaultItem.disabled().build();
  }

  @Nonnull
  @Override
  public ItemBuilder createSettingsItem() {
    return DefaultItem.enabled().amount(getValue());
  }

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
  public void writeSettings(@Nonnull Document document) {
    super.writeSettings(document);
    document.set("enabled", enabled);
  }

  @Override
  public void loadSettings(@Nonnull Document document) {
    super.loadSettings(document);
    setEnabled(document.getBoolean("enabled"));
  }

}
