package net.codingarea.challenges.plugin.management.menu.generator;

import net.anweisen.utilities.bukkit.utils.menu.MenuPosition;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils.InventorySetter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public abstract class MultiPageMenuGenerator extends MenuGenerator {

	protected final List<Inventory> inventories = new ArrayList<>();

	@Nonnull
	protected Inventory createNewInventory(int page) {
		Inventory inventory = Bukkit.createInventory(MenuPosition.HOLDER, getSize(), getTitle(page));
		InventoryUtils.fillInventory(inventory, ItemBuilder.FILL_ITEM);
		inventories.add(inventory);
		return inventory;
	}

	protected String getTitle(@Nonnegative int page) {
		return InventoryTitleManager.getTitle(getMenuType(), page);
	}

	public abstract int getSize();

	public abstract int getPagesCount();

	public abstract void generatePage(@Nonnull Inventory inventory, int page);

	public abstract int[] getNavigationSlots(@Nonnegative int page);

	@Override
	public void generateInventories() {
		inventories.clear();

		for (int page = 0; page < getPagesCount(); page++) {
			Inventory inventory = createNewInventory(page);
			generatePage(inventory, page);
		}

		for (int i = 0; i < inventories.size(); i++) {
			addNavigationItems(inventories.get(i), i);
		}

		reopenInventoryForPlayers();

	}

	@Override
	public List<Inventory> getInventories() {
		return inventories;
	}

	public void addNavigationItems(@Nonnull Inventory inventory, int page) {
		InventoryUtils.setNavigationItems(inventory,
				getNavigationSlots(page), true,
				InventorySetter.INVENTORY, page, inventories.size(),
				DefaultItem.navigateBack().clone().setLore("", "§7Shift §8» §7-5"),
				DefaultItem.navigateNext().clone().setLore("", "§7Shift §8» §7+5"));
	}

}
