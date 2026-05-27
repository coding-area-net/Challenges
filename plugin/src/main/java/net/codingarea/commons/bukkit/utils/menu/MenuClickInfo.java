package net.codingarea.commons.bukkit.utils.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuClickInfo {

  protected final Player player;
  protected final Inventory inventory;
  protected final boolean shiftClick;
  protected final boolean rightClick;
  protected final int slot;

  public MenuClickInfo(@NotNull Player player, @NotNull Inventory inventory, boolean shiftClick, boolean rightClick, int slot) {
    this.player = player;
    this.inventory = inventory;
    this.shiftClick = shiftClick;
    this.rightClick = rightClick;
    this.slot = slot;
  }

  @NotNull
  public Player getPlayer() {
    return player;
  }

  @NotNull
  public Inventory getInventory() {
    return inventory;
  }

  public boolean isRightClick() {
    return rightClick;
  }

  public boolean isLeftClick() {
    return !rightClick;
  }

  public boolean isShiftClick() {
    return shiftClick;
  }

  public int getSlot() {
    return slot;
  }

  @Nullable
  public ItemStack getClickedItem() {
    return inventory.getItem(slot);
  }

  @NotNull
  public Material getClickedMaterial() {
    return getClickedItem() == null ? Material.AIR : getClickedItem().getType();
  }

  @Override
  public String toString() {
    return "MenuClickInfo{" +
      "player=" + player +
      ", inventory=" + inventory +
      ", shiftClick=" + shiftClick +
      ", rightClick=" + rightClick +
      ", slot=" + slot +
      '}';
  }

}
