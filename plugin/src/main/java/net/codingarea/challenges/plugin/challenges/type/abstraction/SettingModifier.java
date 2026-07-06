package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.challenges.type.ISetting;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SettingModifier extends Modifier implements ISetting {

  private boolean enabled;

  public SettingModifier(@NotNull MenuType menu, @Nullable SettingCategory category,
                         @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, 64, displayItemPreset, nameMessageKey);
  }

  public SettingModifier(@NotNull MenuType menu, @Nullable SettingCategory category, int max,
                         @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, max, displayItemPreset, nameMessageKey);
  }

  public SettingModifier(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max,
                         @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, min, max, displayItemPreset, nameMessageKey);
  }

  public SettingModifier(@NotNull MenuType menu, @Nullable SettingCategory category, int min, int max, int defaultValue,
                         @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, min, max, defaultValue, displayItemPreset, nameMessageKey);
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
  public ItemStack getSettingsItemPreset() {
    return DefaultItems.createEnabledValuePreset(getValue());
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

  @Override
  public void playStatusUpdateTitle() {
    ChallengeHelper.playChallengeToggleTitle(this);
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
