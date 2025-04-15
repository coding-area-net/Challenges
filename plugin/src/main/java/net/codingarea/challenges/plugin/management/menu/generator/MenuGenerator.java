package net.codingarea.challenges.plugin.management.menu.generator;

import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.Setter;
import net.anweisen.utilities.bukkit.utils.menu.MenuPosition;
import net.anweisen.utilities.bukkit.utils.misc.CompatibilityUtils;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.position.GeneratorMenuPosition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
@Getter
@Setter
public abstract class MenuGenerator {

    // ONLY MODIFY IF YOU KNOW WHAT YOU ARE DOING
    private MenuType menuType;

	public abstract void generateInventories();

	public abstract List<Inventory> getInventories();

	public abstract MenuPosition getMenuPosition(@Nonnegative int page);

	public boolean hasInventoryOpen(Player player) {
		MenuPosition menuPosition = MenuPosition.get(player);
		return menuPosition instanceof GeneratorMenuPosition
			&& CompatibilityUtils.getTopInventory(player).getType() != InventoryType.CRAFTING
			&& ((GeneratorMenuPosition) menuPosition).getGenerator() == this;
	}

	public int getPage(Player player) {
		MenuPosition menuPosition = MenuPosition.get(player);
		if (menuPosition instanceof GeneratorMenuPosition)
			return ((GeneratorMenuPosition) menuPosition).getPage();
		return 0;
	}

	public void reopenInventoryForPlayers() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (hasInventoryOpen(player)) {
				open(player, getPage(player));
			}
		}
	}

	public void open(@Nonnull Player player, @Nonnegative int page) {
		List<Inventory> inventories = getInventories();
		if (inventories == null || inventories.isEmpty()) generateInventories();
		if (inventories == null || inventories.isEmpty()) return;
		if (page >= inventories.size()) page = inventories.size() - 1;
		Inventory inventory = inventories.get(page);
		MenuPosition.set(player, getMenuPosition(page));
		player.openInventory(inventory);
	}

}
