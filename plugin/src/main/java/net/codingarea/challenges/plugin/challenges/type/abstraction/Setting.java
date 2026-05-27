package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import org.jetbrains.annotations.NotNull;


public abstract class Setting extends AbstractChallenge {

  private final boolean enabledByDefault;
  protected boolean enabled;

  public Setting(@NotNull MenuType menu) {
    this(menu, false);
  }

  public Setting(@NotNull MenuType menu, boolean enabledByDefault) {
    super(menu);
    this.enabledByDefault = enabledByDefault;
    setEnabled(enabledByDefault);
  }

  @Override
  public void handleClick(@NotNull ChallengeMenuClickInfo info) {
    setEnabled(!enabled);
    SoundSample.playStatusSound(info.getPlayer(), enabled);
    playStatusUpdateTitle();
  }

  @Override
  public void restoreDefaults() {
    setEnabled(enabledByDefault);
  }

  public void playStatusUpdateTitle() {
    ChallengeHelper.playToggleChallengeTitle(this);
  }

  @NotNull
  @Override
  public ItemBuilder createSettingsItem() {
    return DefaultItem.status(enabled);
  }

  protected void onEnable() {
  }

  protected void onDisable() {
  }

  @Override
  public void handleShutdown() {
    super.handleShutdown();

    if (isEnabled())
      onDisable();
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    if (this.enabled == enabled) return;
    this.enabled = enabled;

    try {
      if (enabled) onEnable();
      else onDisable();
    } catch (Exception exception) {
      Challenges.getInstance().getILogger().error("Error while {} Setting {}", enabled ? "enabling" : "disabling", getClass().getSimpleName(), exception);
    }

    updateItems();
  }

  @Override
  public void loadSettings(@NotNull Document document) {
    setEnabled(document.getBoolean("enabled", enabled));
  }

  @Override
  public void writeSettings(@NotNull Document document) {
    document.set("enabled", enabled);
  }

}
