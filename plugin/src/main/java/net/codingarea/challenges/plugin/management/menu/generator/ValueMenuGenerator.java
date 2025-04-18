package net.codingarea.challenges.plugin.management.menu.generator;

import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.menu.MenuClickInfo;
import net.anweisen.utilities.bukkit.utils.menu.MenuPosition;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.ValueSetting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.position.GeneratorMenuPosition;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.Map;

@Getter
public abstract class ValueMenuGenerator extends MultiPageMenuGenerator {

  public static final int FINISH_SLOT = 31;

  private final Map<ValueSetting, String> settings;

  public ValueMenuGenerator(Map<ValueSetting, String> settings) {
    this.settings = settings;
  }

  @Override
  public MenuPosition getMenuPosition(int page) {
    return new GeneratorMenuPosition(this, page) {

      @Override
      public void handleClick(@Nonnull MenuClickInfo info) {

        if (info.getSlot() == FINISH_SLOT) {
          onSaveItemClick(info.getPlayer());
          SoundSample.PLOP.play(info.getPlayer());
          return;
        }

        if (InventoryUtils.handleNavigationClicking(generator, getNavigationSlots(page), page, info, () -> onBackToMenuItemClick(info.getPlayer()))) {
          return;
        }

        int slot = info.getSlot();

        int index = getItemsPerPage() * page + slot - 10;

        if (slot > 18) index -= 9;

        if (index >= settings.size() || index < 0) {
          SoundSample.CLICK.play(info.getPlayer());
          return;
        }

        ValueSetting[] array = settings.keySet().toArray(new ValueSetting[0]);

        ValueSetting key = array[index];
        String oldValue = settings.get(key);

        int itemIndex = slot >= 18 ? 1 : 0;
        String newValue = key.onClick(info, oldValue, itemIndex);
        settings.put(key, newValue);
        SoundSample.CLICK.play(info.getPlayer());
        generatePage(info.getInventory(), page);
        open(info.getPlayer(), page);
      }
    };
  }

  @Override
  public int getSize() {
    return 4 * 9;
  }

  @Override
  public int getPagesCount() {
    return (settings.size() / getItemsPerPage()) + 1;
  }

  public int getItemsPerPage() {
    return 4;
  }

  @Override
  public void generatePage(@Nonnull Inventory inventory, int page) {

    int startIndex = getItemsPerPage() * page;
    for (int i = startIndex; i < startIndex + getItemsPerPage() && i < settings.size(); i++) {
      int slot = i - (getItemsPerPage() * page) + 10;
      ValueSetting key = settings.keySet().toArray(new ValueSetting[0])[i];
      String value = settings.get(key);
      inventory.setItem(slot, key.getDisplayItem(value).build());
      inventory.setItem(slot + 9, key.getSettingsItem(value).build());
    }

    inventory.setItem(FINISH_SLOT, DefaultItem.create(Material.LIME_DYE, Message.forName("custom-sub-finish")).build());
  }

  @Override
  public int[] getNavigationSlots(int page) {
    return new int[]{27, 35};
  }

  @Override
  protected String getTitle(int page) {
    return InventoryTitleManager.getTitle(MenuType.CUSTOM, getSubTitles(page));
  }

  public abstract String[] getSubTitles(int page);

  public abstract void onSaveItemClick(Player player);

  public abstract void onBackToMenuItemClick(Player player);

}
