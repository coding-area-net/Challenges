package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class SettingModifierCollectionGoal extends ModifierCollectionGoal {

  public SettingModifierCollectionGoal(int min, int max, @NotNull Object... target) {
    super(min, max, target);
  }

  public SettingModifierCollectionGoal(int min, int max, int defaultValue, @NotNull Object... target) {
    super(min, max, defaultValue, target);
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

  @NotNull
  @Override
  public ItemStack getSettingsItem() {
    return isEnabled() ? super.getSettingsItem() : DefaultItem.disabled().build();
  }

  @NotNull
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
