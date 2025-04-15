package net.codingarea.challenges.plugin.spigot.events;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.0
 */
public abstract class InventoryClickEventWrapper extends Event {

	private final InventoryClickEvent event;

	public InventoryClickEventWrapper(@Nonnull InventoryClickEvent event) {
		this.event = event;
	}

	@Nullable
	public Inventory getClickedInventory() {
		return event.getClickedInventory();
	}

	@Nonnull
	public Inventory getInventory() {
		return event.getInventory();
	}

	@Nonnull
	public InventoryView getView() {
		return event.getView();
	}

	@Nonnull
	public ClickType getClick() {
		return event.getClick();
	}

	@Nonnull
	public HumanEntity getWhoClicked() {
		return event.getWhoClicked();
	}

	public int getSlot() {
		return event.getSlot();
	}

	public int getRawSlot() {
		return event.getRawSlot();
	}

	@Nonnull
	public InventoryAction getAction() {
		return event.getAction();
	}

	@Nullable
	public ItemStack getCursor() {
		return event.getCursor();
	}

	public int getHotbarButton() {
		return event.getHotbarButton();
	}

	@Nonnull
	public SlotType getSlotType() {
		return event.getSlotType();
	}

	@Nonnull
	public Result getResult() {
		return event.getResult();
	}

	public void setResult(@Nonnull Result result) {
		event.setResult(result);
	}

	@Nullable
	public ItemStack getCurrentItem() {
		return event.getCurrentItem();
	}

	public void setCurrentItem(@Nullable ItemStack item) {
		event.setCurrentItem(item);
	}

	@Nonnull
	public List<HumanEntity> getViewers() {
		return event.getViewers();
	}

	@Nonnull
	public InventoryClickEvent getEvent() {
		return event;
	}

	public boolean isCancelled() {
		return event.isCancelled();
	}

	public void setCancelled(boolean cancel) {
		event.setCancelled(cancel);
	}

	public boolean isRightClick() {
		return event.isRightClick();
	}

	public boolean isLeftClick() {
		return event.isLeftClick();
	}

	public boolean isShiftClick() {
		return event.isShiftClick();
	}

}