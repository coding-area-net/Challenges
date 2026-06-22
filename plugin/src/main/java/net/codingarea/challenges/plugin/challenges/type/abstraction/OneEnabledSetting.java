package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class OneEnabledSetting extends Setting {

  private final String typeId;

  public OneEnabledSetting(@NotNull MenuType menu, @Nullable SettingCategory category, @NotNull String typeId,
                           @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, displayItemPreset, nameMessageKey);
    this.typeId = typeId;
  }

  public OneEnabledSetting(@NotNull MenuType menu, @Nullable SettingCategory category, boolean enabledByDefault, @NotNull String typeId,
                           @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, enabledByDefault, displayItemPreset, nameMessageKey);
    this.typeId = typeId;
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (isEnabled()) disableOthers();
  }

  protected final void disableOthers() {
    Challenges.getInstance().getChallengeManager().getChallenges().stream()
      .filter(challenge -> challenge != this)
      .filter(challenge -> challenge instanceof OneEnabledSetting)
      .map(challenge -> (OneEnabledSetting) challenge)
      .filter(setting -> setting.typeId.equals(typeId))
      .forEach(setting -> setting.setEnabled(false));
  }

}
