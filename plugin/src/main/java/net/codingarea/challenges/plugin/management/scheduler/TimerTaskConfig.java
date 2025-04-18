package net.codingarea.challenges.plugin.management.scheduler;

import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;

import javax.annotation.Nonnull;
import java.util.Arrays;

public final class TimerTaskConfig extends AbstractTaskConfig {

	private final TimerStatus[] status;

	TimerTaskConfig(@Nonnull TimerTask annotation) {
		this(annotation.status(), annotation.async());
	}

	TimerTaskConfig(@Nonnull TimerStatus[] status, boolean async) {
		super(async);
		this.status = status;
	}

	@Nonnull
	public TimerStatus[] getStatus() {
		return status;
	}

	public boolean acceptsStatus(@Nonnull TimerStatus status) {
		return Arrays.asList(this.status).contains(status);
	}

}
