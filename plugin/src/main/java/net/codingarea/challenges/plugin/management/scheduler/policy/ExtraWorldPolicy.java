package net.codingarea.challenges.plugin.management.scheduler.policy;

import net.codingarea.challenges.plugin.ChallengeAPI;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public enum ExtraWorldPolicy implements IPolicy {

	ALWAYS(() -> true),
	USED(ChallengeAPI::isWorldInUse),
	NOT_USED(() -> !ChallengeAPI.isWorldInUse());

	private final BooleanSupplier check;

	ExtraWorldPolicy(@Nonnull BooleanSupplier check) {
		this.check = check;
	}

	@Override
	public boolean check(@Nonnull Object holder) {
		return check.getAsBoolean();
	}

}
