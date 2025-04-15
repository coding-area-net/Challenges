package net.codingarea.challenges.plugin.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.0
 */
public class PlayerJumpEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final PlayerStatisticIncrementEvent event;

	public PlayerJumpEvent(@Nonnull Player who, PlayerStatisticIncrementEvent statisticIncrementEvent) {
		super(who);
		this.event = statisticIncrementEvent;
	}

	@Nonnull
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean b) {
		event.setCancelled(true);
	}

	@Nonnull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
