package net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;

public class OneDurabilityChallenge extends Setting {

	public OneDurabilityChallenge() {
		super(MenuType.CHALLENGES);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.WOODEN_HOE, Message.forName("item-one-durability-challenge"));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClick(@Nonnull InventoryClickEvent event) {
		if (!shouldExecuteEffect()) return;
		if (event.getWhoClicked() instanceof Player && ignorePlayer((Player) event.getWhoClicked()))
			return;
		if (event.getCurrentItem() != null) setDurability(event.getCurrentItem());
		if (event.getCursor() != null) setDurability(event.getCursor());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (event.getItem() == null) return;
		setDurability(event.getItem());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(player)) return;
		setDurability(event.getItem().getItemStack());
	}

	private void setDurability(@Nonnull ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta instanceof Damageable) {
			meta.setUnbreakable(false);
			int durability = item.getType().getMaxDurability() - 1;
			((Damageable) meta).setDamage(durability);
			item.setItemMeta(meta);
		}
	}

}
