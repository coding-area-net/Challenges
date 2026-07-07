package net.codingarea.challenges.plugin.management.menu.generator.impl.custom.choose;

import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.UncachedMultiPageMenuGenerator;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Displays a static, keyed set of pre-built {@link ItemStack}s to choose one from.
 * Replaces the legacy {@code ChooseItemGenerator}. The item content is passed in pre-built,
 * as its (legacy) localization happens outside this generator.
 */
public abstract class CustomChooseItemMenuGenerator extends UncachedMultiPageMenuGenerator<CustomChooseItemMenuGenerator.KeyedItem> {

  public static final int SIZE = 5 * 9;
  public static final int[] SLOTS = calcSlots(3, 7, SIZE);

  private final LocalizableMessage menuName;

  protected CustomChooseItemMenuGenerator(@NotNull LocalizableMessage menuName, @NotNull LinkedHashMap<String, ItemStack> items) {
    super(toKeyedItems(items));
    this.menuType = MenuType.CUSTOM;
    this.menuName = menuName;
  }

  protected static KeyedItem[] toKeyedItems(@NotNull LinkedHashMap<String, ItemStack> items) {
    KeyedItem[] array = new KeyedItem[items.size()];
    int i = 0;
    for (Map.Entry<String, ItemStack> entry : items.entrySet()) {
      array[i++] = new KeyedItem(entry.getKey(), entry.getValue());
    }
    return array;
  }

  @NotNull
  @Override
  public LocalizableMessage getMenuName() {
    return menuName;
  }

  public abstract void onItemClick(@NotNull Player player, @NotNull String key);

  @Override
  public void handleElementClick(@NotNull KeyedItem element, @NotNull MenuClickInfo info) {
    onItemClick(info.getPlayer(), element.key());
  }

  @NotNull
  @Override
  public ItemStack createDisplayItem(@NotNull KeyedItem element, @NotNull Locale locale) {
    return element.item();
  }

  @Override
  public int[] getSlots() {
    return SLOTS;
  }

  @Override
  public int getInventorySize() {
    return SIZE;
  }

  @NotNull
  @Override
  public GeneratorMenuPosition createMenuPosition(int page, @NotNull Player player) {
    return new ChooseItemMenuPosition(page);
  }

  public class ChooseItemMenuPosition extends GeneratorMenuPosition {

    public ChooseItemMenuPosition(int page) {
      super(page);
    }

    @Override
    public boolean handleMenuClick(@NotNull MenuClickInfo info) {
      int[] slots = getSlots();
      for (int i = 0; i < slots.length; i++) {
        if (info.getSlot() != slots[i]) continue;

        int index = getPage() * getMaxEntriesPerPage() + i;
        if (index >= elements.length) return false; // page not full

        SoundSample.PLOP.play(info.getPlayer());
        onItemClick(info.getPlayer(), elements[index].key());
        return true;
      }
      return false;
    }
  }

  public record KeyedItem(@NotNull String key, @NotNull ItemStack item) {
  }

}
