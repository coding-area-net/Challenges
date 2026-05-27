package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class SettingModifier extends Modifier {

  private boolean enabled;

  public SettingModifier(@NotNull MenuType menu) {
    super(menu);
  }

  public SettingModifier(@NotNull MenuType menu, int max) {
    super(menu, max);
  }

  public SettingModifier(@NotNull MenuType menu, int min, int max) {
    super(menu, min, max);
  }

  public SettingModifier(@NotNull MenuType menu, int min, int max, int defaultValue) {
    super(menu, min, max, defaultValue);
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
  public final boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    if (this.enabled == enabled) return;
    this.enabled = enabled;

    if (enabled) onEnable();
    else onDisable();

    updateItems();
  }

  public void playStatusUpdateTitle() {
    ChallengeHelper.playToggleChallengeTitle(this);
  }

  protected void onEnable() {
  }

  protected void onDisable() {
  }

  @Override
  public void handleShutdown() {
    super.handleShutdown();
    onDisable();
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
