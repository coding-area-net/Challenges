package net.codingarea.challenges.plugin.management.menu.generator;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.menu.MenuClickInfo;
import net.anweisen.utilities.bukkit.utils.menu.MenuPosition;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.MainCustomMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.position.GeneratorMenuPosition;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class ChooseMultipleItemGenerator extends MultiPageMenuGenerator {

	public static final int FINISH_SLOT = 40;

	private final LinkedHashMap<String, ItemStack> items;
	private final List<String> selectedKeys;

	public ChooseMultipleItemGenerator(LinkedHashMap<String, ItemStack> items) {
		this.items = items;
		selectedKeys = new LinkedList<>();
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

				if (info.getSlot() == FINISH_SLOT) {
					onItemClick(info.getPlayer(), selectedKeys.toArray(new String[0]));
					SoundSample.PLOP.play(info.getPlayer());
					return;
				}

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

				if (selectedKeys.contains(itemKey)) {
					selectedKeys.remove(itemKey);
				} else {
					selectedKeys.add(itemKey);
				}
				generatePage(info.getInventory(), page);
				SoundSample.LOW_PLOP.play(info.getPlayer());
			}
		};
	}

	@Override
	public int getSize() {
		return 5 * 9;
	}

	@Override
	public int getPagesCount() {
		return (items.size() / getItemsPerPage()) + 1;
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
			ItemBuilder itemBuilder = new ItemBuilder(items.get(key)).clone();
			itemBuilder.hideAttributes();

			if (selectedKeys.contains(key)) {
				itemBuilder.addEnchantment(MinecraftNameWrapper.UNBREAKING, 1);
				itemBuilder.appendName(" §8┃ §2§l✔");
			} else {
				itemBuilder.appendName(" §8┃ §c✖");
			}

			lastSlot = getNextMiddleSlot(lastSlot);
			inventory.setItem(lastSlot, itemBuilder.build());
			lastSlot++;
		}

		inventory.setItem(FINISH_SLOT, DefaultItem.create(Material.LIME_DYE, Message.forName("custom-sub-finish")).build());
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

	public abstract void onItemClick(Player player, String[] itemKeys);

	public abstract void onBackToMenuItemClick(Player player);

}
