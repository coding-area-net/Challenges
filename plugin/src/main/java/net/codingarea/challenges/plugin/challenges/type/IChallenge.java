package net.codingarea.challenges.plugin.challenges.type;

import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.management.challenges.ChallengeManager;
import net.codingarea.challenges.plugin.management.challenges.entities.GamestateSaveable;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IChallenge extends GamestateSaveable {

  /**
   * Returns if this challenge is enabled.
   * This value is used in checks and policies like {@link net.codingarea.challenges.plugin.management.scheduler.policy.ChallengeStatusPolicy} which are used for tasks.
   * If this challenge does not have clear boolean value, you may always return {@code true}
   *
   * @return if this challenge is enabled
   */
  boolean isEnabled();

  /**
   * Restores the default settings of this challenge.
   * This will be executed when the /config reset command is executed.
   *
   * @see ChallengeManager#restoreDefaults()
   */
  void restoreDefaults();

  /**
   * This method will be executed when the challenges plugin is being disabled.
   * This will occur when the server is shutdown or reloaded.
   * This method is intended for removing temporarily content or calling disable logic.
   *
   * @see ChallengeManager#shutdownChallenges()
   */
  void handleShutdown();

  /**
   * Returns the internal name of this challenges.
   * This name will be used for saving and assigning the right settings or gamestate.
   * If multiple instances of class are registered, they must not return the same value.
   * Multiple challenges should not have the same name.
   *
   * @return the internal name of this challenge
   */
  @Nonnull
  String getUniqueName();

  /**
   * This challenge will be displayed in the menu for the given {@link MenuType}.
   * This has to always return the same value.
   * If {@link MenuType#isUsable()} is {@code false}, an {@link IllegalArgumentException} will be thrown when registering this challenge.
   *
   * @return the target menu for the challenge
   */
  @Nonnull
  MenuType getType();

  @Nullable
  SettingCategory getCategory();

  @Nonnull
  ItemStack getDisplayItem();

  @Nonnull
  ItemStack getSettingsItem();

  void handleClick(@Nonnull ChallengeMenuClickInfo info);

  void writeSettings(@Nonnull Document document);

  void loadSettings(@Nonnull Document document);

}
