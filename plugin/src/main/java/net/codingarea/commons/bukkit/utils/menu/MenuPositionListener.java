package net.codingarea.commons.bukkit.utils.menu;

import net.codingarea.commons.bukkit.utils.misc.CompatibilityUtils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;

public final class MenuPositionListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onClick(@Nonnull InventoryClickEvent event) {

		HumanEntity human = event.getWhoClicked();
		if (!(human instanceof Player)) return;
		Player player = (Player) human;

		Inventory inventory = event.getClickedInventory();
		if (inventory == null) return;

		if (inventory == CompatibilityUtils.getTopInventory(event)) {

			if (inventory.getHolder() != MenuPosition.HOLDER) return; // No menu inventory

			MenuPosition position = MenuPosition.get(player);
			if (position == null) return; // Currently in no menu

			event.setCancelled(true);
			position.handleClick(new MenuClickInfo(player, inventory, event.isShiftClick(), event.isRightClick(), event.getSlot()));

		} else if (event.isShiftClick()) { // Player inventory was clicked

			Inventory topInventory = event.getInventory();
			if (topInventory.getHolder() != MenuPosition.HOLDER) return; // No menu inventory

			event.setCancelled(true);

		}

	}

}
