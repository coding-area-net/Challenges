package net.codingarea.challenges.plugin.management.menu.generator.impl.custom.choose;

import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.UncachedMultiPageMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.custom.choose.CustomChooseItemMenuGenerator.KeyedItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Choose multiple pre-built items; toggles a selection marker and confirms via a finish button.
 * Replaces the legacy {@code ChooseMultipleItemGenerator}.
 */
public abstract class CustomChooseMultipleItemMenuGenerator extends UncachedMultiPageMenuGenerator<KeyedItem> {

  public static final int SIZE = 5 * 9;
  public static final int[] SLOTS = calcSlots(3, 7, SIZE);
  public static final int FINISH_SLOT = 40;

  private final LocalizableMessage menuName;
  private final List<String> selectedKeys = new LinkedList<>();

  protected CustomChooseMultipleItemMenuGenerator(@NotNull LocalizableMessage menuName, @NotNull LinkedHashMap<String, ItemStack> items) {
    super(CustomChooseItemMenuGenerator.toKeyedItems(items));
    this.menuType = MenuType.CUSTOM;
    this.menuName = menuName;
  }

  @NotNull
  @Override
  public LocalizableMessage getMenuName() {
    return menuName;
  }

  public abstract void onItemClick(@NotNull Player player, @NotNull String[] keys);

  @Override
  public void handleElementClick(@NotNull KeyedItem element, @NotNull MenuClickInfo info) {
    // handled by the custom menu position (toggle instead of confirm)
  }

  @NotNull
  @Override
  public ItemStack createDisplayItem(@NotNull KeyedItem element, @NotNull Locale locale) {
    ItemBuilder builder = new ItemBuilder(locale, element.item().clone()).hideAttributes();
    if (selectedKeys.contains(element.key())) {
      builder.addEnchantment(MinecraftNameWrapper.UNBREAKING, 1);
      builder.appendName(" §8┃ §2§l✔");
    } else {
      builder.appendName(" §8┃ §c✖");
    }
    return builder.build();
  }

  @Override
  public void setInventoryDecoration(@NotNull Inventory inventory, int page, @NotNull Locale locale) {
    super.setInventoryDecoration(inventory, page, locale);
    inventory.setItem(FINISH_SLOT, new ItemBuilder(locale, Material.LIME_DYE, MessageKey.of("custom-sub-finish")).build());
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
    return new ChooseMultipleMenuPosition(page);
  }

  public class ChooseMultipleMenuPosition extends GeneratorMenuPosition {

    public ChooseMultipleMenuPosition(int page) {
      super(page);
    }

    @Override
    public boolean handleMenuClick(@NotNull MenuClickInfo info) {
      if (info.getSlot() == FINISH_SLOT) {
        SoundSample.PLOP.play(info.getPlayer());
        onItemClick(info.getPlayer(), selectedKeys.toArray(new String[0]));
        return true;
      }

      int[] slots = getSlots();
      for (int i = 0; i < slots.length; i++) {
        if (info.getSlot() != slots[i]) continue;

        int index = getPage() * getMaxEntriesPerPage() + i;
        if (index >= elements.length) return false; // page not full

        String key = elements[index].key();
        if (selectedKeys.contains(key)) {
          selectedKeys.remove(key);
        } else {
          selectedKeys.add(key);
        }
        SoundSample.LOW_PLOP.play(info.getPlayer());
        openMenu(info.getPlayer(), getPage()); // re-render selection markers
        return true;
      }
      return false;
    }
  }

}
