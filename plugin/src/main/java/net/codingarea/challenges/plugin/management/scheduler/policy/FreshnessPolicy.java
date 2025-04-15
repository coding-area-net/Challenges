package net.codingarea.challenges.plugin.management.scheduler.policy;

import net.codingarea.challenges.plugin.ChallengeAPI;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public enum FreshnessPolicy implements IPolicy {

	ALWAYS(() -> true),
	FRESH(ChallengeAPI::isFresh),
	NOT_FRESH(() -> !ChallengeAPI.isFresh());

	private final BooleanSupplier check;

	FreshnessPolicy(@Nonnull BooleanSupplier check) {
		this.check = check;
	}

	@Override
	public boolean check(@Nonnull Object holder) {
		return check.getAsBoolean();
	}

}
