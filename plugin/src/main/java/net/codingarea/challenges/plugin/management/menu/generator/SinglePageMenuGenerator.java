package net.codingarea.challenges.plugin.management.menu.generator;

import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class SinglePageMenuGenerator extends AbstractMenuGenerator {

  // thread-safety: reads/writes only triggered by Player interaction Events on the main thread
  // if any async computation is done, the write access should be handed over to the main thread
  // org.bukkit.Inventory operations are also not thread-safe!
  protected final Map<Locale, Inventory> inventoryCache = new HashMap<>();

  @NotNull
  public abstract GeneratorMenuPosition createMenuPosition(@NotNull Player player);

  public abstract void initInventoryDecoration(@NotNull Inventory inventory, @NotNull Locale locale);

  public abstract void updateInventoryContent(@NotNull Inventory inventory, @NotNull Locale locale);

  @NotNull
  protected Inventory createEmptyInventory(@NotNull Locale locale) {
    return MessageKey.of("menu.title-format").createInventory(locale, getInventorySize(),
      getMenuName());
  }

  @NotNull
  @Override
  public final GeneratorMenuPosition createMenuPosition(int page, @NotNull Player player) {
    return createMenuPosition(player); // ignore page; single page
  }

  @NotNull
  @Override
  public final Inventory getOrInitInventory(@NotNull Locale locale, int page) {
    return inventoryCache.computeIfAbsent(locale, this::createAndInitInventory);
  }

  @Override
  public final void updatePages() {
    for (Map.Entry<Locale, Inventory> entry : inventoryCache.entrySet()) {
      updateInventoryContent(entry.getValue(), entry.getKey());
    }
  }

  @Override
  public void updateOrGeneratePages(@NotNull Locale locale) {
    Inventory inventory = inventoryCache.get(locale);
    if (inventory != null) {
      updateInventoryContent(inventory, locale);
    } else {
      getOrInitInventory(locale, 0); // will generate and cache the inventory
    }
  }

  @NotNull
  @CheckReturnValue
  protected Inventory createAndInitInventory(@NotNull Locale locale) {
    Inventory inventory = createEmptyInventory(locale);
    initInventoryDecoration(inventory, locale);
    setNavigationItems(inventory, 0, locale);
    updateInventoryContent(inventory, locale);
    return inventory;
  }

  @Override
  public void updatePage(int page) {
    if (page != 0) {
      throw new IllegalArgumentException("Invalid page number for SinglePageMenuGenerator: " + page);
    }
    updatePages();
  }

  @Override
  public final int getPageCount() {
    return 1;
  }
}
