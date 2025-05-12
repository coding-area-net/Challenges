package net.codingarea.commons.common.concurrent.task;

import net.codingarea.commons.common.collection.NamedThreadFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.Function;

public class CompletableTask<V> implements Task<V> {

	static final ExecutorService SERVICE = Executors.newCachedThreadPool(new NamedThreadFactory("TaskProcessor"));

	private final Collection<TaskListener<V>> listeners = new ArrayList<>();
	private final CompletableFuture<V> future;

	private Throwable failure;

	public CompletableTask() {
		this(new CompletableFuture<>());
	}

	private CompletableTask(@Nonnull CompletableFuture<V> future) {
		this.future = future;
		this.future.exceptionally(ex -> {
			this.failure = ex;
			return null;
		});
	}

	@Nonnull
	public static <V> Task<V> callAsync(@Nonnull Callable<V> callable) {
		CompletableTask<V> task = new CompletableTask<>();
		SERVICE.execute(() -> {
			try {
				task.complete(callable.call());
			} catch (Throwable ex) {
				task.fail(ex);
			}
		});
		return task;
	}

	@Nonnull
	public static <V> Task<V> callSync(@Nonnull Callable<V> callable) {
		CompletableTask<V> task = new CompletableTask<>();
		try {
			task.complete(callable.call());
		} catch (Throwable ex) {
			task.fail(ex);
		}
		return task;
	}

	@Nonnull
	@Override
	public Task<V> addListener(@Nonnull TaskListener<V> listener) {
		if (future.isDone()) {
			V value = future.getNow(null);
			if (future.isCancelled() || value != null) {
				listener.onCancelled(this);
			} else if (failure != null) {
				listener.onFailure(this, failure);
			} else {
				listener.onComplete(this, null);
			}
			return this;
		}

		listeners.add(listener);
		return this;
	}

	@Nonnull
	@Override
	public Task<V> clearListeners() {
		this.listeners.clear();
		return this;
	}

	public void fail(Throwable throwable) {
		this.failure = throwable;
		this.future.completeExceptionally(throwable);
		for (TaskListener<V> listener : this.listeners) {
			listener.onFailure(this, throwable);
		}
	}

	@Override
	public V call() {
		if (this.future.isDone()) {
			return this.future.getNow(null);
		}
		throw new UnsupportedOperationException("Use #complete in the CompletableTask");
	}

	public void complete(@Nullable V value) {
		future.complete(value);
		if (value != null) {
			for (TaskListener<V> listener : listeners) {
				listener.onComplete(this, value);
			}
		} else {
			for (TaskListener<V> listener : listeners) {
				listener.onCancelled(this);
			}
		}

	}

	@Override
	public boolean cancel(boolean b) {
		if (this.future.isCancelled()) {
			return false;
		}

		if (this.future.cancel(b)) {
			for (TaskListener<V> listener : this.listeners) {
				listener.onCancelled(this);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isCancelled() {
		return this.future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return this.future.isDone();
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		return future.get();
	}

	@Override
	public V get(long timeout, @Nonnull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return future.get(timeout, unit);
	}

	@Nonnull
	@Override
	public <R> Task<R> map(@Nullable Function<? super V, ? extends R> mapper) {
		CompletableTask<R> task = new CompletableTask<>();
		this.future.thenAccept(v -> {
			try {
				task.complete(mapper == null ? null : mapper.apply(v));
			} catch (Throwable ex) {
				task.fail(ex);
			}
		});
		this.onFailure(task.future::completeExceptionally);
		this.onCancelled(otherTask -> task.cancel(true));
		return task;
	}

	@Nonnull
	@Override
	public CompletionStage<V> stage() {
		return future;
	}
}
