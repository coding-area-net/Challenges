package net.codingarea.challenges.plugin.utils.misc;

import net.anweisen.utilities.bukkit.utils.animation.AnimationFrame;
import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.menu.MenuClickInfo;
import net.anweisen.utilities.common.collection.IRandom;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.management.menu.generator.MenuGenerator;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @autho<a href="r">KxmischesDomi | https://github</a>.com/kxmischesdomi
 * @since 2.0
 */
public final class InventoryUtils {

	private static final IRandom random;
	private static final List<Material> items;

	static {
		random = IRandom.create();
		items = new ArrayList<>(Arrays.asList(ExperimentalUtils.getMaterials()));
		items.removeIf(material -> !material.isItem());
	}

	private InventoryUtils() {
	}

	public static void fillInventory(@Nonnull Inventory inventory, @Nullable ItemStack item) {
		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, item);
		}
	}

	public static void fillInventory(@Nonnull Inventory inventory, @Nullable ItemStack item, @Nonnull int... slots) {
		for (int i : slots) {
			inventory.setItem(i, item);
		}
	}

	public static void setNavigationItemsToInventory(@Nonnull List<Inventory> inventories, @Nonnull int[] navigationSlots) {
		setNavigationItemsToInventory(inventories, navigationSlots, true);
	}

	public static void setNavigationItemsToInventory(@Nonnull List<Inventory> inventories, @Nonnull int[] navigationSlots, boolean goBackExit) {
		setNavigationItems(inventories, navigationSlots, goBackExit, InventorySetter.INVENTORY);
	}

	public static void setNavigationItemsToFrame(@Nonnull List<AnimationFrame> frames, @Nonnull int[] navigationSlots) {
		setNavigationItemsToFrame(frames, navigationSlots, true);
	}

	public static void setNavigationItemsToFrame(@Nonnull List<AnimationFrame> inventories, @Nonnull int[] navigationSlots, boolean goBackExit) {
		setNavigationItems(inventories, navigationSlots, goBackExit, InventorySetter.FRAME);
	}

	public static void setNavigationItemsToFrame(@Nonnull AnimationFrame frame, @Nonnull int[] navigationSlots, boolean goBackExit, int index, int size) {
		setNavigationItems(frame, navigationSlots, goBackExit, InventorySetter.FRAME, index, size);
	}

	public static <I> void setNavigationItems(@Nonnull List<I> inventories, @Nonnull int[] navigationSlots, boolean goBackExit, @Nonnull InventorySetter<I> setter) {
		for (int i = 0; i < inventories.size(); i++) {
			setNavigationItems(inventories.get(i), navigationSlots, goBackExit, setter, i, inventories.size());
		}
	}

	public static <I> void setNavigationItems(@Nonnull I inventory, @Nonnull int[] navigationSlots, boolean goBackExit, @Nonnull InventorySetter<I> setter, int index, int size) {
		setNavigationItems(inventory, navigationSlots, goBackExit, setter, index, size, DefaultItem.navigateBack(), DefaultItem.navigateNext());
	}

	public static <I> void setNavigationItems(@Nonnull I inventory, @Nonnull int[] navigationSlots, boolean goBackExit, @Nonnull InventorySetter<I> setter, int index, int size, ItemBuilder navigateBack, ItemBuilder navigateNext) {
		if (navigationSlots.length >= 1) {
			ItemBuilder left = index == 0 && goBackExit ? DefaultItem.navigateBackMainMenu() : navigateBack;
			setter.set(inventory, navigationSlots[0], left);
		}
		if (navigationSlots.length >= 2 && index < (size - 1))
			setter.set(inventory, navigationSlots[1], navigateNext);
	}

	public static boolean isEmpty(@Nonnull Inventory inventory) {
		for (ItemStack content : inventory.getContents()) {
			if (content != null) return false;
		}
		return true;
	}

	public static int getRandomEmptySlot(@Nonnull Inventory inventory) {
		List<Integer> emptySlots = new ArrayList<>();

		for (int slot = 0; slot < inventory.getSize(); slot++) {
			if (inventory.getItem(slot) == null) {
				emptySlots.add(slot);
			}

		}

		if (emptySlots.isEmpty()) return -1;
		return emptySlots.get(ThreadLocalRandom.current().nextInt(emptySlots.size()));
	}

	public static int getRandomFullSlot(@Nonnull Inventory inventory) {
		List<Integer> fullSlots = new ArrayList<>();

		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack item = inventory.getItem(slot);
			if (item != null && !item.isSimilar(ItemBuilder.BLOCKED_ITEM)) {
				fullSlots.add(slot);
			}
		}

		if (fullSlots.isEmpty()) return -1;

		return fullSlots.get(ThreadLocalRandom.current().nextInt(fullSlots.size()));
	}

	public static int getRandomSlot(@Nonnull Inventory inventory) {
		List<Integer> slots = new ArrayList<>();

		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack item = inventory.getItem(slot);
			if (item != null && item.isSimilar(ItemBuilder.BLOCKED_ITEM)) continue;
			slots.add(slot);

		}

		if (slots.isEmpty()) return -1;
		return slots.get(ThreadLocalRandom.current().nextInt(slots.size()));
	}

	public static void dropItemByPlayer(@Nonnull Location location, @Nonnull ItemStack itemStack) {
		if (location.getWorld() == null) return;
		Item droppedItem = location.getWorld().dropItem(location.clone().add(0, 1.4, 0), itemStack);
		droppedItem.setVelocity(location.getDirection().multiply(0.4));
	}

	public static void dropOrGiveItem(@Nonnull Inventory inventory, @Nonnull Location location, @Nonnull Material material) {
		dropOrGiveItem(inventory, location, new ItemStack(material));
	}

	public static void dropOrGiveItem(@Nonnull Inventory inventory, @Nonnull Location location, @Nonnull ItemStack itemStack) {
		location = location.clone();
		if (inventory.firstEmpty() == -1) {
			if (location.getWorld() == null)
				location.setWorld(ChallengeAPI.getGameWorld(Environment.NORMAL));
			location.getWorld().dropItem(location, itemStack);
			return;
		}
		inventory.addItem(itemStack);
	}

	public static void removeRandomItem(@Nonnull Inventory inventory) {
		int slot = InventoryUtils.getRandomFullSlot(inventory);
		if (slot == -1) return;
		inventory.setItem(slot, null);
	}

	public static void giveItem(@Nonnull Player player, @Nonnull ItemStack itemStack) {
		giveItem(player.getInventory(), player.getLocation(), itemStack);
	}

	public static void giveItem(@Nonnull Inventory inventory, @Nonnull Location locationToDrop, @Nonnull ItemStack itemStack) {
		if (inventory.firstEmpty() == -1) {
			dropItemByPlayer(locationToDrop, itemStack);
			return;
		}
		inventory.addItem(itemStack);
	}

	public static ItemStack getRandomItem(boolean onlyOne, boolean respectMaxStackSize) {
		Material material = random.choose(items);
		int stackSize = onlyOne ? 1 : (respectMaxStackSize && material.getMaxStackSize() == 1 ? 1 : random.range(1, respectMaxStackSize ? material.getMaxStackSize() : 64));
		return new ItemStack(material, stackSize);
	}

	/**
	 * @return if a navigation item was clicked
	 */
	public static boolean handleNavigationClicking(MenuGenerator generator, int[] navigationSlots, int page, MenuClickInfo info, Runnable onDoorClick) {
		int pagesSwitching = info.isShiftClick() ? 5 : 1;
		if (navigationSlots.length >= 1 && info.getSlot() == navigationSlots[0]) {
			if (page <= 0) {
				if (page == 0) {
					onDoorClick.run();
				} else {
					SoundSample.CLICK.play(info.getPlayer());
				}
				return page == 0;
			} else {
				SoundSample.CLICK.play(info.getPlayer());
				generator.open(info.getPlayer(), Math.max(page - pagesSwitching, 0));
				return true;
			}
		} else if (navigationSlots.length >= 2 && info.getSlot() == navigationSlots[1]) {
			SoundSample.CLICK.play(info.getPlayer());
			if (page < (generator.getInventories().size())) {
				generator.open(info.getPlayer(), Math.min(page + pagesSwitching, generator.getInventories().size()));
				return true;
			}
			return false;
		}
		return false;
	}

	@FunctionalInterface
	public interface InventorySetter<I> {

		InventorySetter<AnimationFrame> FRAME = AnimationFrame::setItem;
		InventorySetter<Inventory> INVENTORY = (inventory, slot, item) -> inventory.setItem(slot, item.build());

		void set(@Nonnull I inventory, int slot, @Nonnull ItemBuilder item);

	}

}
