package net.codingarea.challenges.plugin.utils.misc;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.AnimationFrame;
import net.codingarea.commons.common.collection.IRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

  public static void fillInventory(@NotNull Inventory inventory, @Nullable ItemStack item) {
    for (int i = 0; i < inventory.getSize(); i++) {
      inventory.setItem(i, item);
    }
  }

  public static void fillInventory(@NotNull Inventory inventory, @Nullable ItemStack item, @NotNull int... slots) {
    for (int i : slots) {
      inventory.setItem(i, item);
    }
  }

  public static void setNavigationItemsToInventory(@NotNull List<Inventory> inventories, @NotNull int[] navigationSlots) {
    setNavigationItemsToInventory(inventories, navigationSlots, true);
  }

  public static void setNavigationItemsToInventory(@NotNull List<Inventory> inventories, @NotNull int[] navigationSlots, boolean goBackExit) {
    setNavigationItems(inventories, navigationSlots, goBackExit, InventorySetter.INVENTORY);
  }

  public static void setNavigationItemsToFrame(@NotNull List<AnimationFrame> frames, @NotNull int[] navigationSlots) {
    setNavigationItemsToFrame(frames, navigationSlots, true);
  }

  public static void setNavigationItemsToFrame(@NotNull List<AnimationFrame> inventories, @NotNull int[] navigationSlots, boolean goBackExit) {
    setNavigationItems(inventories, navigationSlots, goBackExit, InventorySetter.FRAME);
  }

  public static void setNavigationItemsToFrame(@NotNull AnimationFrame frame, @NotNull int[] navigationSlots, boolean goBackExit, int index, int size) {
    setNavigationItems(frame, navigationSlots, goBackExit, InventorySetter.FRAME, index, size);
  }

  public static <I> void setNavigationItems(@NotNull List<I> inventories, @NotNull int[] navigationSlots, boolean goBackExit, @NotNull InventorySetter<I> setter) {
    for (int i = 0; i < inventories.size(); i++) {
      setNavigationItems(inventories.get(i), navigationSlots, goBackExit, setter, i, inventories.size());
    }
  }

  public static <I> void setNavigationItems(@NotNull I inventory, @NotNull int[] navigationSlots, boolean goBackExit, @NotNull InventorySetter<I> setter, int index, int size) {
    setNavigationItems(inventory, navigationSlots, goBackExit, setter, index, size, DefaultItem.navigateBack(), DefaultItem.navigateNext());
  }

  public static <I> void setNavigationItems(@NotNull I inventory, @NotNull int[] navigationSlots, boolean goBackExit, @NotNull InventorySetter<I> setter, int index, int size, LegacyItemBuilder navigateBack, LegacyItemBuilder navigateNext) {
    if (navigationSlots.length >= 1) {
      LegacyItemBuilder left = index == 0 && goBackExit ? DefaultItem.navigateBackMainMenu() : navigateBack;
      setter.set(inventory, navigationSlots[0], left);
    }
    if (navigationSlots.length >= 2 && index < (size - 1))
      setter.set(inventory, navigationSlots[1], navigateNext);
  }

  public static boolean isEmpty(@NotNull Inventory inventory) {
    for (ItemStack content : inventory.getContents()) {
      if (content != null) return false;
    }
    return true;
  }

  public static int getRandomEmptySlot(@NotNull Inventory inventory) {
    List<Integer> emptySlots = new ArrayList<>();

    for (int slot = 0; slot < inventory.getSize(); slot++) {
      if (inventory.getItem(slot) == null) {
        emptySlots.add(slot);
      }

    }

    if (emptySlots.isEmpty()) return -1;
    return emptySlots.get(ThreadLocalRandom.current().nextInt(emptySlots.size()));
  }

  public static int getRandomFullSlot(@NotNull Inventory inventory) {
    List<Integer> fullSlots = new ArrayList<>();

    for (int slot = 0; slot < inventory.getSize(); slot++) {
      ItemStack item = inventory.getItem(slot);
      if (item != null && !item.isSimilar(LegacyItemBuilder.BLOCKED_ITEM)) {
        fullSlots.add(slot);
      }
    }

    if (fullSlots.isEmpty()) return -1;

    return fullSlots.get(ThreadLocalRandom.current().nextInt(fullSlots.size()));
  }

  public static int getRandomSlot(@NotNull Inventory inventory) {
    List<Integer> slots = new ArrayList<>();

    for (int slot = 0; slot < inventory.getSize(); slot++) {
      ItemStack item = inventory.getItem(slot);
      if (item != null && item.isSimilar(LegacyItemBuilder.BLOCKED_ITEM)) continue;
      slots.add(slot);

    }

    if (slots.isEmpty()) return -1;
    return slots.get(ThreadLocalRandom.current().nextInt(slots.size()));
  }

  public static void dropItemByPlayer(@NotNull Location location, @NotNull ItemStack itemStack) {
    if (location.getWorld() == null) return;
    Item droppedItem = location.getWorld().dropItem(location.clone().add(0, 1.4, 0), itemStack);
    droppedItem.setVelocity(location.getDirection().multiply(0.4));
  }

  public static void dropOrGiveItem(@NotNull Inventory inventory, @NotNull Location location, @NotNull Material material) {
    dropOrGiveItem(inventory, location, new ItemStack(material));
  }

  public static void dropOrGiveItem(@NotNull Inventory inventory, @NotNull Location location, @NotNull ItemStack itemStack) {
    location = location.clone();
    if (inventory.firstEmpty() == -1) {
      if (location.getWorld() == null)
        location.setWorld(ChallengeAPI.getGameWorld(Environment.NORMAL));
      location.getWorld().dropItem(location, itemStack);
      return;
    }
    inventory.addItem(itemStack);
  }

  public static void removeRandomItem(@NotNull Inventory inventory) {
    int slot = InventoryUtils.getRandomFullSlot(inventory);
    if (slot == -1) return;
    inventory.setItem(slot, null);
  }

  public static void giveItem(@NotNull Player player, @NotNull ItemStack itemStack) {
    giveItem(player.getInventory(), player.getLocation(), itemStack);
  }

  public static void giveItem(@NotNull Inventory inventory, @NotNull Location locationToDrop, @NotNull ItemStack itemStack) {
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

  @FunctionalInterface
  public interface InventorySetter<I> {

    InventorySetter<AnimationFrame> FRAME = AnimationFrame::setItem;
    InventorySetter<Inventory> INVENTORY = (inventory, slot, item) -> inventory.setItem(slot, item.build());

    void set(@NotNull I inventory, int slot, @NotNull LegacyItemBuilder item);

  }

}
