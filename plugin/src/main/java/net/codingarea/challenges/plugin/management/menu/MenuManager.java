package net.codingarea.challenges.plugin.management.menu;

import lombok.Getter;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.management.menu.generator.AbstractMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.MainMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.challenge.ChallengesMenuGenerator;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class MenuManager {

  public static final String MANAGE_GUI_PERMISSION = "challenges.manage";
  public static final int[] GUI_SLOTS = {30, 32, 19, 25, 11, 15, 4};
  @Getter
  private final boolean displayNewInFront;
  private final boolean permissionToManageGUI;
  private boolean generated = false;
  private final MainMenuGenerator mainMenu;

  public MenuManager() {
    mainMenu = new MainMenuGenerator();
    displayNewInFront = Challenges.getInstance().getConfigDocument().getBoolean("challenge-updates.new.in-front");
    permissionToManageGUI = Challenges.getInstance().getConfigDocument().getBoolean("manage-settings-permission");

    ChallengeAPI.subscribeLoader(LanguageLoader.class, this::generateMenus);
    ChallengeAPI.subscribeLoader(LanguageLoader.class, mainMenu::updatePages);
  }

  public void generateMenus() {
    for (MenuType value : MenuType.values()) {
      value.executeWithGenerator(ChallengesMenuGenerator.class, ChallengesMenuGenerator::resetCache);
    }

    for (IChallenge challenge : Challenges.getInstance().getChallengeManager().getChallenges()) {
      MenuType type = challenge.getType();
      type.executeWithGenerator(ChallengesMenuGenerator.class, gen -> gen.addToCache(challenge));
    }

    Locale language = Challenges.getInstance().getLoaderRegistry().findLoaderByClassOrThrow(LanguageLoader.class).getConfigLanguage();
    mainMenu.updateOrGeneratePages(language);
    for (MenuType value : MenuType.values()) {
      value.getMenuGenerator().updateOrGeneratePages(language);
    }

    generated = true;
  }

  public void openMainMenu(@NotNull Player player) {
    SoundSample.PLOP.play(player);
    MenuPosition.set(player, new MainMenuPosition());
    mainMenu.openMenu(player);
  }

  public void openMainMenuInstantly(@NotNull Player player) {
    MenuPosition.set(player, new MainMenuPosition());
    mainMenu.openMenuInstantly(player, true);
  }

  /**
   * @return If the specified menu page be opened.
   * The menu may not be opened, when there are no challenges registered to that menu or the languages are not loaded
   */
  public boolean openMenu(@NotNull Player player, @NotNull MenuType type, int page) {
    if (!generated) {
      SoundSample.BASS_OFF.play(player);
      player.sendMessage(Prefix.CHALLENGES + "§cCould not open gui, languages are not loaded");
      player.sendMessage(Prefix.CHALLENGES + "§cIs the plugin set up correctly?");
      return false;
    }

    type.getMenuGenerator().openMenu(player, page);

    return true;
  }

  public void reopenCurrentMenus() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      reopenCurrentMenu(player);
    }
  }

  public void reopenCurrentMenu(@NotNull Player player) {
    // applies language change
    MenuPosition position = MenuPosition.get(player);
    if (!(position instanceof AbstractMenuGenerator.GeneratorMenuPosition generatorPosition)) return;
    generatorPosition.getGenerator().openMenu(player, generatorPosition.getPage());
  }

  public void playNoPermissionsEffect(@NotNull Player player) {
    SoundSample.BASS_OFF.play(player);
    MessageKey.of("no-permission").send(player, Prefix.CHALLENGES);
  }

  public boolean permissionToManageGUI() {
    return permissionToManageGUI;
  }

  private class MainMenuPosition implements MenuPosition {

    @Override
    public void handleClick(@NotNull MenuClickInfo info) {

      for (int i = 0; i < GUI_SLOTS.length; i++) {
        int current = GUI_SLOTS[i];
        if (current == info.getSlot()) {
          MenuType type = MenuType.values()[i];
          if (openMenu(info.getPlayer(), type, 0))
            SoundSample.CLICK.play(info.getPlayer());
          return;
        }
      }

      SoundSample.CLICK.play(info.getPlayer());

    }

  }

}
