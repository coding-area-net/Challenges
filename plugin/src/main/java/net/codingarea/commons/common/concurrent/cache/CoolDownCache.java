package net.codingarea.commons.common.concurrent.cache;

import net.codingarea.commons.common.collection.RunnableTimerTask;
import net.codingarea.commons.common.logging.ILogger;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToLongFunction;

public class CoolDownCache<K> {

	protected final Map<K, Long> cache = new ConcurrentHashMap<>();
	protected final ILogger logger;
	protected final ToLongFunction<? super K> cooldownTime;
	protected final long cleanInterval;

	public CoolDownCache(@Nonnull ILogger logger, @Nonnegative long cleanInterval, @Nonnull String taskName, @Nonnull ToLongFunction<? super K> cooldownTime) {
		this.logger = logger;
		this.cooldownTime = cooldownTime;
		this.cleanInterval = cleanInterval;

		new Timer(taskName).schedule(new RunnableTimerTask(this::cleanCache), cleanInterval, cleanInterval);
	}

	public void cleanCache() {
		logger.debug("Cleaning cooldown cache");

		long now = System.currentTimeMillis();
		Collection<K> remove = new ArrayList<>();
		cache.forEach((key, time) -> {
			if (time == null || !isOnCoolDown(now, time, key)) {
				logger.trace("Removing {} from cooldown cache", key);
				remove.add(key);
			}
		});
		remove.forEach(cache::remove);
	}

	public boolean isOnCoolDown(long now, long suspect, @Nonnull K key) {
		long difference = now - suspect;
		return difference < getCoolDownTime(key);
	}

	public boolean isOnCoolDown(@Nonnull K key) {
		Long time = cache.get(key);
		if (time == null) return false;
		return isOnCoolDown(System.currentTimeMillis(), time, key);
	}

	public boolean checkCoolDown(@Nonnull K key) {
		boolean cooldown = isOnCoolDown(key);
		if (!cooldown) setOnCoolDown(key);
		return cooldown;
	}

	public void setOnCoolDown(@Nonnull K key) {
		cache.put(key, System.currentTimeMillis());
	}

	public long getCoolDown(@Nonnull K key) {
		Long time = cache.get(key);
		if (time == null) return 0;
		return System.currentTimeMillis() - time;
	}

	public float getCoolDownSeconds(@Nonnull K key) {
		return getCoolDown(key) / 1000f;
	}

	public long getCoolDownTime(@Nonnull K key) {
		return cooldownTime.applyAsLong(key);
	}

	public int size() {
		return cache.size();
	}

}
