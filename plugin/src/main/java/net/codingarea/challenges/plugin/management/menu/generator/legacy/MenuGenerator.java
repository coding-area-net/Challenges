package net.codingarea.challenges.plugin.management.menu.generator.legacy;

import lombok.Getter;
import lombok.Setter;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.position.LegacyGeneratorMenuPosition;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;
import net.codingarea.commons.bukkit.utils.misc.CompatibilityUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
public abstract class MenuGenerator {

  // ONLY MODIFY IF YOU KNOW WHAT YOU ARE DOING
  private MenuType menuType;

  public abstract void generateInventories();

  public abstract List<Inventory> getInventories();

  @NotNull
  public abstract MenuPosition createMenuPosition(int page);

  public boolean hasInventoryOpen(Player player) {
    MenuPosition menuPosition = MenuPosition.get(player);
    return menuPosition instanceof LegacyGeneratorMenuPosition
      && CompatibilityUtils.getTopInventory(player).getType() != InventoryType.CRAFTING
      && ((LegacyGeneratorMenuPosition) menuPosition).getGenerator() == this;
  }

  public int getPage(Player player) {
    MenuPosition menuPosition = MenuPosition.get(player);
    if (menuPosition instanceof LegacyGeneratorMenuPosition)
      return ((LegacyGeneratorMenuPosition) menuPosition).getPage();
    return 0;
  }

  public void reopenInventoryForPlayers() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (hasInventoryOpen(player)) {
        open(player, getPage(player));
      }
    }
  }

  public void open(@NotNull Player player, int page) {
    List<Inventory> inventories = getInventories();
    if (inventories == null || inventories.isEmpty()) generateInventories();
    if (inventories == null || inventories.isEmpty()) return;
    if (page >= inventories.size()) page = inventories.size() - 1;
    Inventory inventory = inventories.get(page);
    MenuPosition.set(player, createMenuPosition(page));
    player.openInventory(inventory);
  }

}
