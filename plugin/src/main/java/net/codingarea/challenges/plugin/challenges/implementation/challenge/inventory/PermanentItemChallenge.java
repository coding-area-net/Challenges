package net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory;

import net.anweisen.utilities.bukkit.utils.misc.CompatibilityUtils;
import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;

@Since("2.0")
public class PermanentItemChallenge extends Setting {

  public PermanentItemChallenge() {
    super(MenuType.CHALLENGES);
    setCategory(SettingCategory.INVENTORY);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.VINE, Message.forName("item-permanent-item-challenge"));
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onInventoryClick(@Nonnull PlayerInventoryClickEvent event) {
    Player player = event.getPlayer();
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(player)) return;
    Inventory clickedInventory = event.getClickedInventory();
    if (event.getCursor() == null) return;
    if (clickedInventory == null) return;
    InventoryType type = CompatibilityUtils.getTopInventory(player).getType();
    if (type == InventoryType.WORKBENCH || type == InventoryType.CRAFTING) return;
    if (clickedInventory.getType() == InventoryType.CRAFTING) return;
    if (clickedInventory.getType() == InventoryType.PLAYER) {
      if (event.getInventory().getType() != InventoryType.PLAYER) {
        event.setCancelled(true);
      }
    }

  }

  @EventHandler
  public void onPlayerDropItem(@Nonnull PlayerDropItemEvent event) {
    if (!shouldExecuteEffect()) return;
    event.setCancelled(true);
  }

}
