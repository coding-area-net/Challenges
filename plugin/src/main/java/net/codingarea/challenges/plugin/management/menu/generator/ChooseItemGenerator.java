package net.codingarea.challenges.plugin.management.menu.generator;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.menu.MenuClickInfo;
import net.anweisen.utilities.bukkit.utils.menu.MenuPosition;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.MainCustomMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.position.GeneratorMenuPosition;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.LinkedHashMap;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public abstract class ChooseItemGenerator extends MultiPageMenuGenerator {

	private final LinkedHashMap<String, ItemStack> items;

	public ChooseItemGenerator(LinkedHashMap<String, ItemStack> items) {
		this.items = items;
	}

	private static int getNextMiddleSlot(@Nonnegative int currentSlot) {
		if (currentSlot >= 53) return currentSlot;
		if (isSideSlot(currentSlot)) return getNextMiddleSlot(currentSlot + 1);
		return currentSlot;
	}

	private static boolean isSideSlot(@Nonnegative int slot) {
		return slot % 9 == 0 || slot % 9 == 8;
	}

	private static boolean isTopOrBottomSlot(@Nonnegative int slot) {
		return slot < 9 || slot > 35;
	}

	@Override
	public MenuPosition getMenuPosition(int page) {
		return new GeneratorMenuPosition(this, page) {

			@Override
			public void handleClick(@Nonnull MenuClickInfo info) {

				if (InventoryUtils.handleNavigationClicking(generator, getNavigationSlots(page), page, info, () -> onBackToMenuItemClick(info.getPlayer()))) {
					return;
				}

				int slot = info.getSlot();
				int index = getItemsPerPage() * page + slot - 10;

				if (slot > 18) index -= 2;
				if (slot > 27) index -= 2;

				if (index >= items.size()) {
					SoundSample.CLICK.play(info.getPlayer());
					return;
				}

				String[] array = items.keySet().toArray(new String[0]);

				if (isSideSlot(slot) || isTopOrBottomSlot(slot)) {
					SoundSample.CLICK.play(info.getPlayer());
					return;
				}

				String itemKey = array[index];

				onItemClick(info.getPlayer(), itemKey);
				SoundSample.PLOP.play(info.getPlayer());

			}
		};
	}

	@Override
	public int getSize() {
		return 5 * 9;
	}

	@Override
	public int getPagesCount() {
		return (int) Math.ceil((float) items.size() / getItemsPerPage());
	}

	public int getItemsPerPage() {
		return 3 * 7;
	}

	@Override
	public void generatePage(@Nonnull Inventory inventory, int page) {

		int lastSlot = 10;
		int startIndex = getItemsPerPage() * page;
		for (int i = startIndex; i < startIndex + getItemsPerPage() && i < items.size(); i++) {
			String key = items.keySet().toArray(new String[0])[i];
			ItemStack itemStack = items.get(key);
			lastSlot = getNextMiddleSlot(lastSlot);
			inventory.setItem(lastSlot, itemStack);
			lastSlot++;
		}

	}

	@Override
	public int[] getNavigationSlots(int page) {
		return MainCustomMenuGenerator.NAVIGATION_SLOTS;
	}

	@Override
	protected String getTitle(int page) {
		return InventoryTitleManager.getTitle(MenuType.CUSTOM, getSubTitles(page));
	}

	public abstract String[] getSubTitles(int page);

	public abstract void onItemClick(Player player, String itemKey);

	public abstract void onBackToMenuItemClick(Player player);

}
