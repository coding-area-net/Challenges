package net.codingarea.challenges.plugin.spigot.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.0
 */
@Getter
public class PlayerInventoryClickEvent extends InventoryClickEventWrapper {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;

	public PlayerInventoryClickEvent(@Nonnull InventoryClickEvent event) {
		super(event);
		player = ((Player) event.getWhoClicked());
	}

	@Nonnull
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Nonnull
	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

}