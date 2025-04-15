package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.spigot.events.PlayerIgnoreStatusChangeEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.0
 */
@Since("2.0")
public class SlotLimitSetting extends Modifier {

	public SlotLimitSetting() {
		super(MenuType.SETTINGS, 1, 36, 36);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.BARRIER, Message.forName("item-slot-limit-setting"));
	}

	@Override
	protected void onValueChange() {
		update();
	}

	@TimerTask(status = { TimerStatus.RUNNING })
	public void updateDelayed() {
		Bukkit.getScheduler().runTaskLater(plugin, this::update, 1);
	}

	@TimerTask(status = { TimerStatus.PAUSED }, async = false)
	public void update() {
		Bukkit.getOnlinePlayers().forEach(this::updateSlots);
	}

	private void updateSlots(@Nonnull Player player) {
		for (int i = 0; i < 36; i++) {
			if (ignorePlayer(player)) {
				unBlockSlot(player, i);
				continue;
			}
			if (isBlocked(i) && ChallengeAPI.isStarted()) {
				blockSlot(player, i);
			} else {
				unBlockSlot(player, i);
			}
		}
	}

	private boolean isBlocked(int slot) {
		if (slot > 35) return false;

		int value = getValue() - 1;

		if (slot >= 9 && slot <= 17) {
			slot += 9 * 2;
		} else if (slot >= 27) {
			slot -= 9 * 2;
		}

		return slot > value;
	}

	private void blockSlot(@Nonnull Player player, int slot) {
		if (ignorePlayer(player)) return;

		ItemStack item = player.getInventory().getItem(slot);
		if (item != null && !item.isSimilar(ItemBuilder.BLOCKED_ITEM)) {
			if (!Bukkit.isPrimaryThread()) {
				Bukkit.getScheduler().runTask(plugin, () -> {
					player.getWorld().dropItemNaturally(player.getLocation(), item);
				});
			} else {
				player.getWorld().dropItemNaturally(player.getLocation(), item);
			}
		}
		player.getInventory().setItem(slot, ItemBuilder.BLOCKED_ITEM);
	}

	private void unBlockSlot(@Nonnull Player player, int slot) {
		ItemStack item = player.getInventory().getItem(slot);
		if (item != null && item.isSimilar(ItemBuilder.BLOCKED_ITEM)) {
			player.getInventory().setItem(slot, null);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInventoryClick(@Nonnull PlayerInventoryClickEvent event) {
		if (!shouldExecuteEffect()) return;
		if (event.getClickedInventory() == null) return;
		if (event.getClickedInventory().getType() != InventoryType.PLAYER) return;
		if (isBlocked(event.getSlot())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerDropItem(@Nonnull PlayerDropItemEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (!event.getItemDrop().getItemStack().isSimilar(ItemBuilder.BLOCKED_ITEM)) return;
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onClick(@Nonnull BlockPlaceEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (!event.getItemInHand().isSimilar(ItemBuilder.BLOCKED_ITEM)) return;
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSwapItem(@Nonnull PlayerSwapHandItemsEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;

		if (event.getMainHandItem() != null && event.getMainHandItem().isSimilar(ItemBuilder.BLOCKED_ITEM)) {
			event.setCancelled(true);
		} else if (event.getOffHandItem() != null && event.getOffHandItem().isSimilar(ItemBuilder.BLOCKED_ITEM)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(@Nonnull PlayerDeathEvent event) {
		event.getDrops().removeIf(itemStack -> itemStack.isSimilar(ItemBuilder.BLOCKED_ITEM));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onRespawn(PlayerRespawnEvent event) {
		updateSlots(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerIgnoreStatusChange(PlayerIgnoreStatusChangeEvent event) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			updateSlots(event.getPlayer());
		});
	}

}