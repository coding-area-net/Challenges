package net.codingarea.challenges.plugin.spigot.events;

import lombok.Getter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.0
 */
@Getter
public class PlayerPickupItemEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final Item item;
	private final int remaining;
	private boolean cancel = false;

	public PlayerPickupItemEvent(@Nonnull Player player, @Nonnull Item item, int remaining) {
		super(player);
		this.item = item;
		this.remaining = remaining;
	}

	@Nonnull
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	@Nonnull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}