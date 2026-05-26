package net.codingarea.commons.bukkit.utils.wrapper;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class ActionListener<E extends Event> implements Listener {

	private final Class<E> classOfEvent;
	private final Consumer<? super E> listener;
	private final EventPriority priority;
	@Getter
    private final boolean ignoreCancelled;

	public ActionListener(@Nonnull Class<E> classOfEvent, @Nonnull Consumer<? super E> listener, @Nonnull EventPriority priority, boolean ignoreCancelled) {
		this.classOfEvent = classOfEvent;
		this.listener = listener;
		this.priority = priority;
		this.ignoreCancelled = ignoreCancelled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ActionListener<?> that = (ActionListener<?>) o;
		return listener.equals(that.listener);
	}

	@Override
	public int hashCode() {
		return Objects.hash(listener);
	}

	@Nonnull
	public Consumer<? super E> getListener() {
		return listener;
	}

	@Nonnull
	public EventPriority getPriority() {
		return priority;
	}

	@Nonnull
	public Class<E> getClassOfEvent() {
		return classOfEvent;
	}

}
