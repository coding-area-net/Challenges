package net.codingarea.challenges.plugin.management.menu.generator.impl;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.SingleAnimatedMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.challenge.ChallengesMenuGenerator;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.AnimatedInventory;
import net.codingarea.commons.bukkit.utils.animation.AnimationFrame;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MainMenuGenerator extends SingleAnimatedMenuGenerator {

  public static final int SIZE = 5 * 9;
  public static final int[] GUI_SLOTS = {30, 32, 19, 25, 11, 15, 4};

  @NotNull
  @Override
  public MenuPosition createMenuPosition() {
    return new MainMenuPosition();
  }

  @NotNull
  @Override
  public AnimatedInventory createAnimatedInventory(@NotNull Locale locale) {
    AnimatedInventory gui = new AnimatedInventory(MessageKey.of("menu-main-title").asComponent(locale), SIZE, MenuPosition.HOLDER);
    gui.createAndAdd().fill(ItemBuilder.FILL_ITEM);
    gui.cloneLastAndAdd().setContrast(39, 41);
    gui.cloneLastAndAdd().setContrast(38, 42);
    gui.cloneLastAndAdd().setContrast(37, 43);
    gui.cloneLastAndAdd().setContrast(28, 34);
    gui.cloneLastAndAdd().setContrast(27, 35);
    gui.cloneLastAndAdd().setContrast(18, 26);
    gui.cloneLastAndAdd().setContrast(9, 17);
    gui.cloneLastAndAdd().setContrast(10, 16);
    gui.cloneLastAndAdd().setContrast(1, 7);
    gui.cloneLastAndAdd().setContrast(2, 6);

    MenuType[] values = MenuType.values();
    for (int i = 0; i < values.length; i += 2) {
      AnimationFrame frame = gui.cloneLastAndAdd();

      frame.setItem(GUI_SLOTS[i], createDisplayItem(locale, values[i]));

      if (values.length > i + 1) {
        frame.setItem(GUI_SLOTS[i + 1], createDisplayItem(locale, values[i + 1]));
      }
    }

    return gui;
  }

  @NotNull
  protected ItemBuilder createDisplayItem(@NotNull Locale locale, @NotNull MenuType menuType) {
    ItemBuilder item = new ItemBuilder(locale, menuType.getDisplayItemMaterial(), MessageKey.of("menu.item-format"),
      menuType.getDisplayName());

    // TODO centralize suffix logic
    if (menuType.getMenuGenerator() instanceof ChallengesMenuGenerator generator) {
      if (generator.hasAnyNewChallenges()) {
        item.appendName(MessageKey.of("suffix.new-challenge"));
      } else if (generator.hasAnyUpdatedChallenges()) {
        item.appendName(MessageKey.of("suffix.updated-challenge"));
      }
    }

    return item;
  }

  public class MainMenuPosition implements MenuPosition {

    @Override
    public void handleClick(@NotNull MenuClickInfo info) {
      SoundSample.CLICK.play(info.getPlayer());

      for (int i = 0; i < GUI_SLOTS.length; i++) {
        int current = GUI_SLOTS[i];
        if (current == info.getSlot()) {
          MenuType type = MenuType.values()[i];
          Challenges.getInstance().getMenuManager().openMenu(info.getPlayer(), type, 0);
          return;
        }
      }
    }

  }
}
