package net.codingarea.challenges.plugin.management.menu.generator.impl.custom.choose;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SelectableKey;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.UncachedMultiPageSelectMenuGenerator;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

public abstract class CustomChooseOptionMenuGenerator extends UncachedMultiPageSelectMenuGenerator<SelectableKey> {

  public static final int SIZE = 5 * 9;
  public static final int[] SLOTS = calcSlots(3, 7, SIZE);

  private final LocalizableMessage menuName;

  protected CustomChooseOptionMenuGenerator(@NotNull LocalizableMessage menuName, @NotNull Map<String, ? extends SelectableKey> items) {
    super(items.values().toArray(new SelectableKey[0]));
    this.menuType = MenuType.CUSTOM;
    this.menuName = menuName;
  }

  @NotNull
  @Override
  public LocalizableMessage getMenuName() {
    return menuName;
  }

  public abstract void onItemClick(@NotNull Player player, @NotNull String key);

  @Override
  public void handleElementClick(@NotNull SelectableKey element, @NotNull MenuClickInfo info) {
    onItemClick(info.getPlayer(), element.getKey());
  }

  @NotNull
  @Override
  public ItemStack createDisplayItem(@NotNull SelectableKey element, @NotNull Locale locale) {
    return element.getDisplayItem(locale).build();
  }

  @Override
  public int[] getSlots() {
    return SLOTS;
  }

  @Override
  public int getInventorySize() {
    return SIZE;
  }

}
