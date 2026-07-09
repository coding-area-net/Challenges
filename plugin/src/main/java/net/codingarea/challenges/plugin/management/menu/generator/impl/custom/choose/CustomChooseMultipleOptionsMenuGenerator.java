package net.codingarea.challenges.plugin.management.menu.generator.impl.custom.choose;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SelectableKey;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public abstract class CustomChooseMultipleOptionsMenuGenerator extends CustomChooseOptionMenuGenerator {

  public static final int FINISH_SLOT = 40;

  private final Collection<String> selectedKeys = new HashSet<>();

  protected CustomChooseMultipleOptionsMenuGenerator(@NotNull LocalizableMessage menuName, @NotNull Map<String, ? extends SelectableKey> items) {
    super(menuName, items);
  }

  public abstract void onSaveClick(@NotNull Player player, @NotNull String[] keys);

  @Override
  public void onItemClick(@NotNull Player player, @NotNull String key) {
    if (selectedKeys.contains(key)) {
      selectedKeys.remove(key);
    } else {
      selectedKeys.add(key);
    }
  }

  @Override
  public void handleElementClick(@NotNull SelectableKey element, @NotNull MenuClickInfo info) {
    super.handleElementClick(element, info);

    // re-render element
    setElementItemsAt(element, info.getInventory(), info.getSlot(), findLanguageProvider().getPlayerLanguage(info.getPlayer()));
  }

  @NotNull
  @Override
  public ItemStack createDisplayItem(@NotNull SelectableKey element, @NotNull Locale locale) {
    ItemBuilder item = element.getDisplayItem(locale);
    if (selectedKeys.contains(element.getKey())) {
      item.addEnchantment(MinecraftNameWrapper.UNBREAKING, 1);
      item.appendName(MessageKey.of("suffix.selected"));
    } else {
      item.appendName(MessageKey.of("suffix.unselected"));
    }
    return item.build();
  }

  @Override
  public void setInventoryDecoration(@NotNull Inventory inventory, int page, @NotNull Locale locale) {
    super.setInventoryDecoration(inventory, page, locale);
    inventory.setItem(FINISH_SLOT, new ItemBuilder(locale, Material.LIME_DYE, MessageKey.of("custom-sub-finish")).build());
  }

  @NotNull
  @Override
  public GeneratorMenuPosition createMenuPosition(int page, @NotNull Player player) {
    return new CustomChooseMultipleOptionsMenuPosition(page, player);
  }

  public class CustomChooseMultipleOptionsMenuPosition extends UncachedMultiPageMenuPosition {

    public CustomChooseMultipleOptionsMenuPosition(int page, @NotNull Player player) {
      super(page, player);
    }

    @Override
    public boolean handleMenuClick(@NotNull MenuClickInfo info) {
      if (info.getSlot() == FINISH_SLOT) {
        SoundSample.PLOP.play(info.getPlayer());
        onSaveClick(info.getPlayer(), selectedKeys.toArray(new String[0]));
        return true;
      }

      return super.handleMenuClick(info);
    }
  }

}
