package net.codingarea.challenges.plugin.spigot.command;

import net.anweisen.utilities.bukkit.utils.menu.MenuPosition;
import net.anweisen.utilities.bukkit.utils.menu.positions.SlottedMenuPosition;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class InvseeCommand implements PlayerCommand, Listener {

  private static final int
    startBottom = 36,
    endBottom = 53,
    helmetSlot = 46,
    chestPlateSlot = 47,
    leggingsSlot = 48,
    bootsSlot = 49,
    offHandSlot = 52;

  private final Map<Player, Inventory> inventories = new HashMap<>();

  @Override
  public void onCommand(@Nonnull Player player, @Nonnull String[] args) throws Exception {

    if (args.length < 1) {
      Message.forName("syntax").send(player, Prefix.CHALLENGES, "invsee <player>");
      return;
    }

    Player target = Bukkit.getPlayer(args[0]);

    if (target == null) {
      Message.forName("command-no-target").send(player, Prefix.CHALLENGES);
      return;
    }

    player.openInventory(getInventory(target));
    MenuPosition.set(player, new SlottedMenuPosition());
    Message.forName("command-invsee-open").send(player, Prefix.CHALLENGES, NameHelper.getName(target));
  }

  public Inventory getInventory(@Nonnull Player player) {
    if (inventories.containsKey(player)) {
      return inventories.get(player);
    }

    Inventory inventory = Bukkit.createInventory(MenuPosition.HOLDER, 6 * 9, InventoryTitleManager.getTitle(Message.forName("inventory-color").asString() + NameHelper.getName(player)));
    inventories.put(player, inventory);
    MenuPosition.set(player, event -> {
    });
    updateInventoryContents(inventory, player.getInventory());
    return inventory;
  }

  public void updateInventoryContents(@Nonnull Inventory inventory, @Nonnull PlayerInventory playerInventory) {
    inventory.clear();

    for (int slot = startBottom; slot <= endBottom; slot++) {
      inventory.setItem(slot, ItemBuilder.FILL_ITEM);
    }

    inventory.setItem(helmetSlot, playerInventory.getHelmet());
    inventory.setItem(chestPlateSlot, playerInventory.getChestplate());
    inventory.setItem(leggingsSlot, playerInventory.getLeggings());
    inventory.setItem(bootsSlot, playerInventory.getBoots());
    inventory.setItem(offHandSlot, playerInventory.getItemInOffHand());

    for (int slot = 0; slot < 36; slot++) {
      inventory.setItem(slot, playerInventory.getItem(slot));
    }

  }

  public void updateInventory(@Nonnull Player player) {
    if (!inventories.containsKey(player)) return;
    Inventory inventory = inventories.get(player);
    updateInventoryContents(inventory, player.getInventory());
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInventoryClick(@Nonnull PlayerInventoryClickEvent event) {
    if (event.getClickedInventory() == null) return;
    if (event.getClickedInventory().getHolder() != event.getPlayer()) return;
    Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), () -> updateInventory(event.getPlayer()), 1);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerDropItem(@Nonnull PlayerDropItemEvent event) {
    Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), () -> updateInventory(event.getPlayer()), 1);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerQuit(@Nonnull PlayerQuitEvent event) {
    Inventory inventory = inventories.remove(event.getPlayer());
    if (inventory == null) return;
    for (HumanEntity viewer : new ArrayList<>(inventory.getViewers())) {
      viewer.closeInventory();
    }

  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onClose(@Nonnull InventoryCloseEvent event) {
    if (event.getInventory().getHolder() != MenuPosition.HOLDER) return;
    Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), () -> {
      for (Entry<Player, Inventory> entry : inventories.entrySet()) {
        Inventory inventory = entry.getValue();
        if (inventory.getViewers().isEmpty()) {
          inventories.remove(entry.getKey());
        }
      }
    }, 1);
  }

}
