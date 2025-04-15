package net.codingarea.challenges.plugin.content.loader;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.logging.ConsolePrint;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public final class LoaderRegistry {

	private static final AtomicInteger loading = new AtomicInteger(); // Keeps track of how many loaders are still loading
	private final Map<Class<? extends ContentLoader>, Subscribers> subscribers = new HashMap<>();
	private final Collection<ContentLoader> loaders;
	public LoaderRegistry(@Nonnull ContentLoader... loaders) {
		this.loaders = Arrays.asList(loaders);
	}

	private static void execute(@Nonnull Class<? extends ContentLoader> classOfLoader, @Nonnull Runnable action) {
		try {
			action.run();
		} catch (Exception ex) {
			Logger.error("Could not execute subscriber for {}", classOfLoader.getSimpleName(), ex);
		}
	}

	public void load() {
		if (isLoading()) {
			ConsolePrint.alreadyExecutingContentLoader();
			return;
		}

		loaders.forEach(this::executeLoader);
	}

	private void executeLoader(@Nonnull ContentLoader loader) {
		loading.incrementAndGet();
		loader.load();
		loading.decrementAndGet();
		handleCompleteLoading(loader.getClass());
	}

	private void handleCompleteLoading(@Nonnull Class<? extends ContentLoader> classOfLoader) {
		Logger.debug("{} finished loading. {} loader(s) left", classOfLoader.getSimpleName(), loading);

		if (loading.get() == 0)
			Logger.info("All loaders finished loading");

		if (Challenges.getInstance().isEnabled()) {
			Subscribers subscribers = this.subscribers.get(classOfLoader);
			if (subscribers == null) return;
			subscribers.execute();
		}
	}

	public void enable() {
		for (Subscribers subscribers : subscribers.values()) {
			if (subscribers.executed) continue;
			subscribers.execute();
		}
	}

	public void disable() {
		subscribers.clear();
	}

	public boolean isLoading() {
		return loading.get() > 0;
	}

	public void subscribe(@Nonnull Class<? extends ContentLoader> classOfLoader, @Nonnull Runnable action) {
		Subscribers subscribers = this.subscribers.computeIfAbsent(classOfLoader, key -> new Subscribers(classOfLoader));
		subscribers.actions.add(action);

		if (subscribers.executed)
			execute(classOfLoader, action);
	}

	@SuppressWarnings("unchecked")
	public <T extends ContentLoader> T getFirstLoaderByClass(@Nonnull Class<T> clazz) {
		for (ContentLoader loader : loaders) {
			if (loader.getClass().equals(clazz)) {
				return (T) loader;
			}
		}
		return null;
	}

	private static class Subscribers {

		private final Class<? extends ContentLoader> loader;
		private final Collection<Runnable> actions = new ArrayList<>(1);
		private boolean executed = false;

		public Subscribers(Class<? extends ContentLoader> loader) {
			this.loader = loader;
		}

		public void execute() {
			Logger.debug("Executing subscribers for {}", loader.getSimpleName());
			executed = true;
			for (Runnable subscriber : actions) {
				LoaderRegistry.execute(loader, subscriber);
			}
		}

	}
}
