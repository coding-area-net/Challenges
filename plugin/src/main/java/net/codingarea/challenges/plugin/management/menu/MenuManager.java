package net.codingarea.challenges.plugin.management.menu;

import lombok.Getter;
import net.codingarea.commons.bukkit.utils.animation.AnimatedInventory;
import net.codingarea.commons.bukkit.utils.animation.AnimationFrame;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.management.menu.generator.ChallengeMenuGenerator;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class MenuManager {

  public static final String MANAGE_GUI_PERMISSION = "challenges.manage";
  public static final int[] GUI_SLOTS = {30, 32, 19, 25, 11, 15, 4};
  @Getter
  private final boolean displayNewInFront;
  private final boolean permissionToManageGUI;
  private AnimatedInventory gui;
  private boolean generated = false;

  public MenuManager() {
    ChallengeAPI.subscribeLoader(LanguageLoader.class, this::generateMenus);
    ChallengeAPI.subscribeLoader(LanguageLoader.class, this::generateMainMenu);
    displayNewInFront = Challenges.getInstance().getConfigDocument().getBoolean("display-new-in-front");
    permissionToManageGUI = Challenges.getInstance().getConfigDocument().getBoolean("manage-settings-permission");
    generateMainMenu();
  }

  public void generateMainMenu() {

    gui = new AnimatedInventory(InventoryTitleManager.getMainMenuTitle(), 5 * 9, MenuPosition.HOLDER);
    gui.addFrame(new AnimationFrame(5 * 9).fill(ItemBuilder.FILL_ITEM));
    gui.cloneLastAndAdd().setAccent(39, 41);
    gui.cloneLastAndAdd().setAccent(38, 42);
    gui.cloneLastAndAdd().setAccent(37, 43);
    gui.cloneLastAndAdd().setAccent(28, 34);
    gui.cloneLastAndAdd().setAccent(27, 35);
    gui.cloneLastAndAdd().setAccent(18, 26);
    gui.cloneLastAndAdd().setAccent(9, 17);
    gui.cloneLastAndAdd().setAccent(10, 16);
    gui.cloneLastAndAdd().setAccent(1, 7);
    gui.cloneLastAndAdd().setAccent(2, 6);

    MenuType[] values = MenuType.values();
    for (int i = 0; i < values.length; i += 2) {

      AnimationFrame frame = gui.getLastFrame().clone();

      MenuType first = values[i];
      frame.setItem(GUI_SLOTS[i], new ItemBuilder(first.getDisplayItem()).name(
        DefaultItem.getItemPrefix() + first.getDisplayName()).hideAttributes());

      if (values.length > i + 1) {
        MenuType second = values[i + 1];
        frame.setItem(GUI_SLOTS[i + 1], new ItemBuilder(second.getDisplayItem()).name(
          DefaultItem.getItemPrefix() + second.getDisplayName()).hideAttributes());
      }

      gui.addFrame(frame);
    }

  }

  public void generateMenus() {

    for (MenuType value : MenuType.values()) {
      value.executeWithGenerator(ChallengeMenuGenerator.class, ChallengeMenuGenerator::resetChallengeCache);
    }

    for (IChallenge challenge : Challenges.getInstance().getChallengeManager().getChallenges()) {
      MenuType type = challenge.getType();
      type.executeWithGenerator(ChallengeMenuGenerator.class, gen -> gen.addChallengeToCache(challenge));
    }

    for (MenuType value : MenuType.values()) {
      value.getMenuGenerator().generateInventories();
    }

    generated = true;
  }

  public void openGUI(@Nonnull Player player) {
    SoundSample.PLOP.play(player);
    MenuPosition.set(player, new MainMenuPosition());
    gui.open(player, Challenges.getInstance());
  }

  public void openGUIInstantly(@Nonnull Player player) {
    MenuPosition.set(player, new MainMenuPosition());
    gui.openNotAnimated(player, true, Challenges.getInstance());
  }

  /**
   * @return If the specified menu page could be opened.
   * The menu may not be opened, when there are no challenges registered to that menu or the languages are not loaded
   */
  public boolean openMenu(@Nonnull Player player, @Nonnull MenuType type, int page) {
    if (!generated) {
      SoundSample.BASS_OFF.play(player);
      player.sendMessage(Prefix.CHALLENGES + "§cCould not open gui, languages are not loaded");
      player.sendMessage(Prefix.CHALLENGES + "§cIs the plugin set up correctly?");
      return false;
    }

    type.getMenuGenerator().open(player, page);

    return true;
  }

  public void playNoPermissionsEffect(@Nonnull Player player) {
    SoundSample.BASS_OFF.play(player);
    Message.forName("no-permission").send(player, Prefix.CHALLENGES);
  }

  public boolean permissionToManageGUI() {
    return permissionToManageGUI;
  }

  private class MainMenuPosition implements MenuPosition {

    @Override
    public void handleClick(@Nonnull MenuClickInfo info) {

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
