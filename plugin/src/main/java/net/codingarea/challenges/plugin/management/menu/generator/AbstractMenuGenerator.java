package net.codingarea.challenges.plugin.management.menu.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.LanguageProvider;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public abstract class AbstractMenuGenerator implements IMenuGenerator {

  @Getter
  @Setter
  protected MenuType menuType; // injected by MenuType constructor or set manually

  @NotNull
  public abstract GeneratorMenuPosition createMenuPosition(int page, @NotNull Player player);

  @NotNull
  public abstract Inventory getOrInitInventory(@NotNull Locale locale, int page);

  /**
   * @implSpec multiple of 9
   */
  public abstract int getInventorySize();

  /**
   * @implSpec should not contain any colors by default, inventory title will dynamically be formatted
   */
  @NotNull
  public LocalizableMessage getMenuName() {
    if (menuType == null)
      throw new IllegalStateException("MenuType not set in " + this.getClass() + ", was it not initialized by MenuType directly?");
    return menuType.getMenuName();
  }

  @NotNull
  protected LocalizableMessage createSubMenuTitle(@NotNull Object menuNameArg, @NotNull Object subMenuNameArg) {
    return MessageKey.of("menu.title-name-format-sub").withArgs(menuNameArg, subMenuNameArg);
  }

  @Override
  public void openMenu(@NotNull Player player, int page) {
    openMenuWithPositionOverwrite(player, page, createMenuPosition(page, player));
  }

  private void openMenuWithPositionOverwrite(@NotNull Player player, int page, @NotNull GeneratorMenuPosition menuPosition) {
    Locale locale = findLanguageProvider().getPlayerLanguage(player);
    Inventory inventory = getOrInitInventory(locale, page);
    MenuPosition.set(player, menuPosition);
    player.openInventory(inventory);
  }

  public int getNavigateNextSlot() {
    return getInventorySize() - 1; // bottom right
  }

  public int getNavigateBackSlot() {
    return getInventorySize() - 9; // bottom left
  }

  protected void setNavigationItems(@NotNull Inventory inventory, int page, @NotNull Locale locale) {
    if (page < getPageCount() - 1) {
      inventory.setItem(getNavigateNextSlot(), DefaultItems.createNavigateNext(locale).build());
    }

    if (page == 0) {
      inventory.setItem(getNavigateBackSlot(), DefaultItems.createNavigateBackMainMenu(locale).build());
    } else {
      inventory.setItem(getNavigateBackSlot(), DefaultItems.createNavigateBack(locale).build());
    }
  }

  protected void handleNavigateOutOfMenu(@NotNull Player player) {
    Challenges.getInstance().getMenuManager().openMainMenuInstantly(player);
  }

  @NotNull
  protected LanguageProvider findLanguageProvider() {
    return Challenges.getInstance().getTranslationManager().getLanguageProvider();
  }

  @NotNull
  public Collection<Player> retrieveViewers() {
    List<Player> viewers = new ArrayList<>(); // default capacity: 10
    for (Player player : Bukkit.getOnlinePlayers()) {
      MenuPosition position = MenuPosition.get(player);
      if (position instanceof GeneratorMenuPosition generatorPosition && generatorPosition.getGenerator() == this) {
        viewers.add(player);
      }
    }
    return viewers;
  }

  @Getter
  @AllArgsConstructor
  public abstract class GeneratorMenuPosition implements MenuPosition {

    protected final int page;

    @Override
    public void handleClick(@NotNull MenuClickInfo info) {
      int pageCount = getPageCount();
      if (page >= pageCount) { // should not happen; dynamically calculated page count changed
        SoundSample.CLICK.play(info.getPlayer());
        return;
      }

      int pageSwitchAmount = info.isShiftClick() ? 5 : 1;
      if (info.getSlot() == getNavigateBackSlot()) {
        if (page == 0) {
          SoundSample.OPEN.play(info.getPlayer());
          handleNavigateOutOfMenu(info.getPlayer());
        } else {
          SoundSample.CLICK.play(info.getPlayer());
          openMenu(info.getPlayer(), Math.max(page - pageSwitchAmount, 0));
        }
        return;
      } else if (info.getSlot() == getNavigateNextSlot()) {
        SoundSample.CLICK.play(info.getPlayer());
        int maxPageIndex = pageCount - 1;
        if (page < maxPageIndex) {
          openMenu(info.getPlayer(), Math.min(page + pageSwitchAmount, maxPageIndex));
        }
        return;
      }

      if (!handleMenuClick(info)) {
        SoundSample.CLICK.play(info.getPlayer());
      }
    }

    /**
     * @implSpec Return {@code true} if a valid slot handled by this Position was clicked,
     * otherwise return {@code false} to play default fallback behavior (click sound for invalid clicks)
     */
    public abstract boolean handleMenuClick(@NotNull MenuClickInfo info);

    protected void handleNavigateOutOfMenu(@NotNull Player player) {
      AbstractMenuGenerator.this.handleNavigateOutOfMenu(player);
    }

    @NotNull
    public AbstractMenuGenerator getGenerator() {
      return AbstractMenuGenerator.this;
    }

  }

  @Getter
  public abstract class HistoryAwareGeneratorMenuPosition extends GeneratorMenuPosition {

    private final GeneratorMenuPosition previousPosition;
    private volatile boolean forgotten = false;

    public HistoryAwareGeneratorMenuPosition(int page, @Nullable GeneratorMenuPosition previousPosition) {
      super(page);
      this.previousPosition = previousPosition;
    }

    public HistoryAwareGeneratorMenuPosition(int page, @NotNull Player player) {
      super(page);
      if (MenuPosition.get(player) instanceof GeneratorMenuPosition prevPosition) {
        while (prevPosition instanceof HistoryAwareGeneratorMenuPosition historyAwarePosition
          && shouldSkipPreviousPosition(historyAwarePosition)) {
          prevPosition = historyAwarePosition.getPreviousPosition();
        }
        this.previousPosition = prevPosition;
      } else {
        this.previousPosition = null;
      }
    }

    protected boolean shouldSkipPreviousPosition(@NotNull HistoryAwareGeneratorMenuPosition prevPosition) {
      // skip other pages of same menu (we only care about the menu before!) + skip all forgotten positions
      return prevPosition.isForgotten()
        || prevPosition.getGenerator() == AbstractMenuGenerator.this
        && prevPosition.getPreviousPosition() != null;
    }

    @Override
    protected void handleNavigateOutOfMenu(@NotNull Player player) {
      this.forgotten = true;

      if (previousPosition == null) {
        super.handleNavigateOutOfMenu(player);
        return;
      }

      AbstractMenuGenerator previousMenu = previousPosition.getGenerator();
      int previousPage = previousPosition.getPage();
      int previousMenuPageCount = previousMenu.getPageCount();
      if (previousPage < previousMenuPageCount) { // dynamic page count might have changed
        previousMenu.openMenuWithPositionOverwrite(player, previousPage, previousPosition);
      } else {
        previousMenu.openMenuWithPositionOverwrite(player, previousMenuPageCount - 1, previousPosition);
      }
    }
  }

}
