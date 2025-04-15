package net.codingarea.challenges.plugin.challenges.implementation.material;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 1.0
 */
public class BlockMaterialSetting extends Setting {

	private final String name;
	private final ItemBuilder preset;
	private final Object[] replacements;
	private final Material[] materials;

	public BlockMaterialSetting(@Nonnull String name, @Nonnull ItemBuilder preset, @Nonnull Object[] replacements, @Nonnull Material... materials) {
		super(MenuType.ITEMS, true);
		this.name = name;
		this.preset = preset;
		this.replacements = replacements;
		this.materials = materials;
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return preset.clone().applyFormat(Message.forName(name).asItemDescription(replacements));
	}

	private boolean blockMaterial(Material material) {
		return Arrays.asList(materials).contains(material);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(@Nonnull PlayerInteractEvent event) {
		if (isEnabled()) return;
		if (ChallengeAPI.isWorldInUse()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (!blockMaterial(event.getMaterial())) {
			if (event.getClickedBlock() == null) return;
			if (!blockMaterial(event.getClickedBlock().getType())) return;
			event.setCancelled(true);
			return;
		}
		event.setCancelled(true);

		dropMaterial(event.getPlayer().getLocation(), event.getPlayer().getInventory());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInventoryClick(@Nonnull PlayerInventoryClickEvent event) {
		if (isEnabled()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (ChallengeAPI.isWorldInUse()) return;
		if (event.getCurrentItem() == null) return;
		if (event.getClickedInventory() == null) return;
		if (event.getClickedInventory().getHolder() != event.getPlayer()) return;
		if (!blockMaterial(event.getCurrentItem().getType())) return;
		event.setCancelled(true);

		dropMaterial(event.getPlayer().getLocation(), event.getClickedInventory());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerPickupItem(@Nonnull PlayerPickupItemEvent event) {
		if (isEnabled()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (ChallengeAPI.isWorldInUse()) return;
		if (!blockMaterial(event.getItem().getItemStack().getType())) return;
		event.setCancelled(true);
	}

	public void dropMaterial(@Nonnull Location location, @Nonnull Inventory inventory) {
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack item = inventory.getItem(slot);
			if (item == null) continue;
			if (!blockMaterial(item.getType())) continue;
			InventoryUtils.dropItemByPlayer(location, item);
			inventory.setItem(slot, null);
		}

	}

	@NotNull
	@Override
	public String getUniqueName() {
		return "blockmaterial" + materials[0].name().toLowerCase();
	}

}