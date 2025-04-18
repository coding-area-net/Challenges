package net.codingarea.challenges.plugin.management.scheduler;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.misc.ReflectionUtils;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScheduleManager {

	private final Map<ScheduledTaskConfig, ScheduledTaskExecutor> scheduledTaskExecutorsByConfig = new ConcurrentHashMap<>();
	private final Map<TimerTaskConfig, TimerTaskExecutor> timerTaskExecutorsByConfig = new ConcurrentHashMap<>();
	private boolean started = false;

	public void register(@Nonnull Object... schedulers) {
		for (Object scheduler : schedulers) {
			register(scheduler);
		}
	}

	public void register(@Nonnull Object scheduler) {
		for (Method method : ReflectionUtils.getMethodsAnnotatedWith(scheduler.getClass(), ScheduledTask.class)) {
			if (method.getParameterCount() != 0) {
				Logger.warn("Could not register scheduler " + method);
				continue;
			}

			ScheduledTask annotation = method.getAnnotation(ScheduledTask.class);
			ScheduledFunction function = new ScheduledFunction(scheduler, method, new PoliciesContainer(annotation));

			Logger.debug("Registered scheduled task {}", function);
			register(function, new ScheduledTaskConfig(annotation));
		}
		for (Method method : ReflectionUtils.getMethodsAnnotatedWith(scheduler.getClass(), TimerTask.class)) {
			if (method.getParameterCount() != 0) {
				Logger.warn("Could not register scheduler {}", method);
				continue;
			}

			TimerTask annotation = method.getAnnotation(TimerTask.class);
			ScheduledFunction function = new ScheduledFunction(scheduler, method, new PoliciesContainer(annotation));

			Logger.debug("Registered timer task {}", function);
			register(function, new TimerTaskConfig(annotation));
		}
	}

	public void unregister(@Nonnull Object object) {
		for (ScheduledTaskExecutor scheduler : scheduledTaskExecutorsByConfig.values()) {
			scheduler.unregister(object);
		}
	}

	private void register(@Nonnull ScheduledFunction function, @Nonnull AbstractTaskConfig config) {
		if (config instanceof ScheduledTaskConfig) {
			ScheduledTaskConfig taskConfig = (ScheduledTaskConfig) config;
			if (taskConfig.getRate() < 1) {
				Logger.warn("Schedule rate cannot be less than 1; Could not register {}", function);
				return;
			}

			ScheduledTaskExecutor executor = getOrCreateScheduledTaskExecutor(taskConfig);
			executor.register(function);
		}
		if (config instanceof TimerTaskConfig) {
			TimerTaskConfig taskConfig = (TimerTaskConfig) config;

			TimerTaskExecutor executor = getOrCreateTimerTaskExecutor(taskConfig);
			executor.register(function);
		}
	}

	@Nonnull
	private ScheduledTaskExecutor getOrCreateScheduledTaskExecutor(@Nonnull ScheduledTaskConfig config) {
		ScheduledTaskExecutor executor = scheduledTaskExecutorsByConfig.get(config);
		if (executor != null) return executor;

		// Create a new task
		executor = new ScheduledTaskExecutor(config);
		if (started) executor.start();
		scheduledTaskExecutorsByConfig.put(config, executor);
		return executor;
	}

	@Nonnull
	private TimerTaskExecutor getOrCreateTimerTaskExecutor(@Nonnull TimerTaskConfig config) {
		TimerTaskExecutor executor = timerTaskExecutorsByConfig.get(config);
		if (executor != null) return executor;

		// Create a new task
		executor = new TimerTaskExecutor(config);
		timerTaskExecutorsByConfig.put(config, executor);
		return executor;
	}

	public void fireTimerStatusChange() {
		if (!started) return;
		for (TimerTaskExecutor executor : timerTaskExecutorsByConfig.values()) {
			if (!executor.getConfig().acceptsStatus(ChallengeAPI.getTimerStatus())) continue;
			executor.execute();
		}
	}

	public void stop() {
		started = false;
		scheduledTaskExecutorsByConfig.values().forEach(ScheduledTaskExecutor::stop);
		scheduledTaskExecutorsByConfig.clear();
		timerTaskExecutorsByConfig.clear();
	}

	public void start() {
		started = true;
		scheduledTaskExecutorsByConfig.values().forEach(ScheduledTaskExecutor::start);
	}

}
