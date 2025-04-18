package net.codingarea.challenges.plugin.management.scheduler;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class ScheduledTaskConfig extends AbstractTaskConfig {

	private final int rate;

	ScheduledTaskConfig(@Nonnull ScheduledTask annotation) {
		this(annotation.ticks(), annotation.async());
	}

	ScheduledTaskConfig(@Nonnegative int rate, boolean async) {
		super(async);
		this.rate = rate;
	}

}
