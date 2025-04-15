package net.codingarea.challenges.plugin.management.scheduler.policy;

import net.codingarea.challenges.plugin.ChallengeAPI;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public enum TimerPolicy implements IPolicy {

	ALWAYS(() -> true),
	PAUSED(ChallengeAPI::isPaused),
	STARTED(ChallengeAPI::isStarted);

	private final BooleanSupplier check;

	TimerPolicy(@Nonnull BooleanSupplier check) {
		this.check = check;
	}

	@Override
	public boolean check(@Nonnull Object holder) {
		return check.getAsBoolean();
	}

}
