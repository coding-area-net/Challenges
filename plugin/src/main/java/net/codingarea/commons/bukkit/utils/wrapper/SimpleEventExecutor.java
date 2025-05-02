package net.codingarea.commons.bukkit.utils.wrapper;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class SimpleEventExecutor implements EventExecutor {

	private final Class<?> classOfEvent;
	private final Consumer action;

	public <E extends Event> SimpleEventExecutor(@Nonnull Class<?> classOfEvent, @Nonnull Consumer<?> action) {
		this.classOfEvent = classOfEvent;
		this.action = action;
	}

	@Override
	public void execute(@Nonnull Listener listener, @Nonnull Event event) throws EventException {
		if (!classOfEvent.isAssignableFrom(event.getClass())) return;
		try {
			action.accept(event);
		} catch (Throwable ex) {
			throw new EventException(ex);
		}
	}

}
