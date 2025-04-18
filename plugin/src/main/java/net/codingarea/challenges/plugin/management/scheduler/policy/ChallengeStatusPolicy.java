package net.codingarea.challenges.plugin.management.scheduler.policy;

import net.codingarea.challenges.plugin.challenges.type.IChallenge;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public enum ChallengeStatusPolicy implements IPolicy {

	ALWAYS(challenge -> true),
	DISABLED(challenge -> !challenge.isEnabled()),
	ENABLED(IChallenge::isEnabled);

	private final Predicate<IChallenge> check;

	ChallengeStatusPolicy(@Nonnull Predicate<IChallenge> check) {
		this.check = check;
	}

	@Override
	public boolean check(@Nonnull Object holder) {
		return check.test((IChallenge) holder);
	}

	@Override
	public boolean isApplicable(@Nonnull Object holder) {
		return holder instanceof IChallenge;
	}

}
