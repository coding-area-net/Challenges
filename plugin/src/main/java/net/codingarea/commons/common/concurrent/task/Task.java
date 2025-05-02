package net.codingarea.commons.common.concurrent.task;

import net.codingarea.commons.common.collection.WrappedException;
import net.codingarea.commons.common.function.ExceptionallyFunction;
import net.codingarea.commons.common.function.ExceptionallyRunnable;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A task that may complete (done / failed / cancelled) in the future or may already be done.
 *
 * For the completion can be listened by calling {@link #onComplete(Consumer)}, for the cancellation by {@link #onCancelled(Runnable)} and for failure by {@link #onFailure(Consumer)}.
 *
 * @see #asyncCall(Callable)
 * @see #completed(Object)
 */
public interface Task<V> extends Future<V>, Callable<V> {

	@Nonnull
	static ExecutorService getAsyncExecutor() {
		return CompletableTask.SERVICE;
	}

	@Nonnull
	static <V> Task<V> empty() {
		return completed(null);
	}

	@Nonnull
	static <V> Task<V> completed(@Nullable V value) {
		return new CompletedTask<>(value);
	}

	@Nonnull
	static Task<Void> completedVoid() {
		return empty();
	}

	@Nonnull
	static <V> Task<V> failed(@Nonnull Throwable failure) {
		return new CompletedTask<>(failure);
	}

	@Nonnull
	static <V> CompletableTask<V> completable() {
		return new CompletableTask<>();
	}

	@Nonnull
	static <V> Task<V> asyncCall(@Nonnull Callable<V> callable) {
		return CompletableTask.callAsync(callable);
	}

	@Nonnull
	static <V> Task<V> asyncSupply(@Nonnull Supplier<V> supplier) {
		return asyncCall(supplier::get);
	}

	@Nonnull
	static Task<Void> asyncRun(@Nonnull Runnable runnable) {
		return asyncCall(() -> { runnable.run(); return null; });
	}

	@Nonnull
	static Task<Void> asyncRunExceptionally(@Nonnull ExceptionallyRunnable runnable) {
		return asyncRun(runnable);
	}

	@Nonnull
	static <V> Task<V> syncCall(@Nonnull Callable<V> callable) {
		return CompletableTask.callSync(callable);
	}

	@Nonnull
	static <V> Task<V> syncSupply(@Nonnull Supplier<V> supplier) {
		return syncCall(supplier::get);
	}

	@Nonnull
	static Task<Void> syncRun(@Nonnull Runnable runnable) {
		return syncCall(() -> { runnable.run(); return null; });
	}

	@Nonnull
	static Task<Void> syncRunExceptionally(@Nonnull ExceptionallyRunnable runnable) {
		return syncRun(runnable);
	}

	@Nonnull
	default Task<V> onComplete(@Nonnull Runnable action) {
		return onComplete(v -> action.run());
	}

	@Nonnull
	default Task<V> onComplete(@Nonnull Consumer<? super V> action) {
		return onComplete((task, value) -> action.accept(value));
	}

	@Nonnull
	default Task<V> onComplete(@Nonnull BiConsumer<? super Task<V>, ? super V> action) {
		return addListener(new TaskListener<V>() {
			@Override
			public void onComplete(@Nonnull Task<V> task, @Nonnull V value) {
				action.accept(task, value);
			}
		});
	}

	@Nonnull
	default Task<V> onFailure(@Nonnull Runnable action) {
		return onFailure(ex -> action.run());
	}

	@Nonnull
	default Task<V> onFailure(@Nonnull Consumer<? super Throwable> action) {
		return onFailure((task, ex) -> action.accept(ex));
	}

	@Nonnull
	default Task<V> onFailure(@Nonnull BiConsumer<? super Task<V>, ? super Throwable> action) {
		return addListener(new TaskListener<V>() {
			@Override
			public void onFailure(@Nonnull Task<V> task, @Nonnull Throwable ex) {
				action.accept(task, ex);
			}
		});
	}

	@Nonnull
	default Task<V> throwOnFailure() {
		return onFailure(ex -> ex.printStackTrace());
	}

	@Nonnull
	default Task<V> onCancelled(@Nonnull Runnable action) {
		return onCancelled(task -> action.run());
	}

	@Nonnull
	default Task<V> onCancelled(@Nonnull Consumer<? super Task<V>> action) {
		return addListener(new TaskListener<V>() {
			@Override
			public void onCancelled(@Nonnull Task<V> task) {
				action.accept(task);
			}
		});
	}

	@Nonnull
	default Task<V> addListeners(@Nonnull TaskListener<V>... listeners) {
		for (TaskListener<V> listener : listeners)
			addListener(listener);

		return this;
	}

	@Nonnull
	Task<V> addListener(@Nonnull TaskListener<V> listener);

	@Nonnull
	Task<V> clearListeners();

	@Override
	V get() throws InterruptedException, ExecutionException;

	@Override
	V get(long timeout, @Nonnull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

	default V getOrDefault(V def) {
		try {
			return get();
		} catch (InterruptedException ex) {
			throw new WrappedException(ex);
		} catch (ExecutionException ex) {
			return def;
		}
	}

	default V getOrDefault(long timeout, @Nonnull TimeUnit unit, V def) {
		try {
			return this.get(timeout, unit);
		} catch (InterruptedException ex) {
			throw new WrappedException(ex);
		} catch (ExecutionException | TimeoutException ex) {
			return def;
		}
	}

	default V getBeforeTimeout(long timeout, @Nonnull TimeUnit unit) {
		try {
			return get(timeout, unit);
		} catch (ExecutionException | InterruptedException ex) {
			throw new WrappedException(ex);
		} catch (TimeoutException ex) {
			throw new IllegalStateException("Operation timed out (" + timeout + " " + unit + ")");
		}
	}

	@Nonnull
	<R> Task<R> map(@Nullable Function<? super V, ? extends R> mapper);

	@Nonnull
	default <R> Task<R> mapExceptionally(@Nullable ExceptionallyFunction<? super V, ? extends R> mapper) {
		return map(mapper);
	}

	@Nonnull
	default Task<Void> mapVoid() {
		return map(v -> null);
	}

	@Nonnull
	default <R> Task<R> map(@Nonnull Class<R> target) {
		return map(target::cast);
	}

	@Nonnull
	@CheckReturnValue
	CompletionStage<V> stage();

}
