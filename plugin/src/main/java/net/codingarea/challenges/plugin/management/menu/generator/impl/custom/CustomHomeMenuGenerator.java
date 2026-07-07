package net.codingarea.challenges.plugin.management.menu.generator.impl.custom;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.IChallengesMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.SinglePageMenuGenerator;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public class CustomHomeMenuGenerator extends SinglePageMenuGenerator implements IChallengesMenuGenerator {

  public static final int VIEW_SLOT = 21;
  public static final int CREATE_SLOT = 23;
  public static final int SIZE = 5 * 9;

  private final CustomListMenuGenerator viewGenerator = new CustomListMenuGenerator();

  @Override
  public void setMenuType(MenuType menuType) {
    // MenuType.CUSTOM instance not initialized when creating CustomListMenuGenerator
    super.setMenuType(menuType);
    viewGenerator.setMenuType(menuType);
  }

  @NotNull
  @Override
  public GeneratorMenuPosition createMenuPosition(@NotNull Player player) {
    return new CustomMainMenuPosition();
  }

  @Override
  public void setInventoryDecoration(@NotNull Inventory inventory, @NotNull Locale locale) {
    super.setInventoryDecoration(inventory, locale); // background fill items
    for (int i : new int[]{1, 2, 6, 7, 9, 10, 16, 17, 27, 28, 34, 35, 37, 38, 39, 41, 42, 43}) {
      inventory.setItem(i, ItemBuilder.FILL_ITEM_CONTRAST);
    }
  }

  @Override
  public void updateInventoryContent(@NotNull Inventory inventory, @NotNull Locale locale) {
    inventory.setItem(VIEW_SLOT, new ItemBuilder(locale, Material.BOOK, MessageKey.of("menu.custom.home.item-view")).build());
    inventory.setItem(CREATE_SLOT, new ItemBuilder(locale, Material.WRITABLE_BOOK, MessageKey.of("menu.custom.home.item-create")).build());
  }

  @Override
  public int getInventorySize() {
    return SIZE;
  }

  @Override
  public void addToCache(@NonNull IChallenge element) {
    viewGenerator.addToCache(element);
  }

  @Override
  public void removeFromCache(@NonNull IChallenge element) {
    viewGenerator.removeFromCache(element);
  }

  @Override
  public boolean isCached(@NonNull IChallenge element) {
    return viewGenerator.isCached(element);
  }

  @Override
  public int getCachedCount() {
    return viewGenerator.getCachedCount();
  }

  @Override
  public void resetCache() {
    viewGenerator.resetCache();
  }

  @Override
  public void updateElementDisplay(@NonNull IChallenge element) {
    viewGenerator.updateElementDisplay(element);
  }

  public class CustomMainMenuPosition extends SinglePageGeneratorMenuPosition {

    @Override
    public boolean handleMenuClick(@NotNull MenuClickInfo info) {
      if (info.getSlot() == VIEW_SLOT) {
        if (Challenges.getInstance().getCustomChallengesLoader().getCustomChallenges().isEmpty()) {
          MessageKey.of("custom-not-loaded").send(info.getPlayer(), Prefix.CUSTOM);
          SoundSample.BASS_OFF.play(info.getPlayer());
          return true;
        }
        viewGenerator.openMenu(info.getPlayer());
        SoundSample.PLOP.play(info.getPlayer());
        return true;
      } else if (info.getSlot() == CREATE_SLOT) {
        // TODO check permission, limit, abstract logic
        InfoMenuGenerator generator = new InfoMenuGenerator();
        generator.openMenu(info.getPlayer());
        SoundSample.PLOP.play(info.getPlayer());
        return true;
      }

      return false;
    }
  }

}
