package net.codingarea.challenges.plugin.challenges.type;

import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class EmptyChallenge implements IChallenge {

  private final MenuType menuType;

  public EmptyChallenge(@Nonnull MenuType menuType) {
    this.menuType = menuType;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }

  @Override
  public void restoreDefaults() {

  }

  @Override
  public void handleShutdown() {

  }

  @NotNull
  @Override
  public String getUniqueGamestateName() {
    return getUniqueName();
  }

  @NotNull
  @Override
  public String getUniqueName() {
    return "empty";
  }

  @NotNull
  @Override
  public MenuType getType() {
    return menuType;
  }

  @Nullable
  @Override
  public SettingCategory getCategory() {
    return null;
  }

  @NotNull
  @Override
  public ItemStack getDisplayItem() {
    return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE, "§0").build();
  }

  @NotNull
  @Override
  public ItemStack getSettingsItem() {
    return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE, "§0").build();
  }

  @Override
  public void handleClick(@NotNull ChallengeMenuClickInfo info) {
    SoundSample.CLICK.play(info.getPlayer());
  }

  @Override
  public void writeSettings(@NotNull Document document) {

  }

  @Override
  public void loadSettings(@NotNull Document document) {

  }

  @Override
  public void writeGameState(@NotNull Document document) {

  }

  @Override
  public void loadGameState(@NotNull Document document) {

  }

}
