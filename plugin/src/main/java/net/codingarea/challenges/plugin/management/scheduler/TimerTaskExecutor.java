package net.codingarea.challenges.plugin.management.scheduler;

import net.codingarea.challenges.plugin.Challenges;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
final class TimerTaskExecutor extends AbstractTaskExecutor {

	private final TimerTaskConfig config;

	TimerTaskExecutor(@Nonnull TimerTaskConfig config) {
		this.config = config;
	}

	public void execute() {
		if (config.isAsync())
			Bukkit.getScheduler().runTaskAsynchronously(Challenges.getInstance(), this);
		else if (!Bukkit.isPrimaryThread())
			Bukkit.getScheduler().runTask(Challenges.getInstance(), this);
		else this.run();
	}

	@Nonnull
	@Override
	public TimerTaskConfig getConfig() {
		return config;
	}

}
