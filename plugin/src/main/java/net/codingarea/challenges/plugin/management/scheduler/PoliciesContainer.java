package net.codingarea.challenges.plugin.management.scheduler;

import net.codingarea.challenges.plugin.management.scheduler.policy.IPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public class PoliciesContainer {

	private final List<IPolicy> policies = new ArrayList<>();

	public PoliciesContainer(@Nonnull ScheduledTask annotation) {
		addPolicies(
				annotation.challengePolicy(),
				annotation.timerPolicy(),
				annotation.playerPolicy(),
				annotation.worldPolicy(),
				annotation.freshnessPolicy()
		);
	}

	public PoliciesContainer(@Nonnull TimerTask annotation) {
		addPolicies(
				annotation.challengePolicy(),
				annotation.playerPolicy(),
				annotation.worldPolicy(),
				annotation.freshnessPolicy()
		);
	}

	private void addPolicies(@Nonnull IPolicy... policies) {
		this.policies.addAll(Arrays.asList(policies));
	}

	public boolean allPoliciesAreTrue(@Nonnull Object holder) {
		for (IPolicy policy : policies) {
			if (!policy.isApplicable(holder)) continue;
			if (!policy.check(holder))
				return false;
		}
		return true;
	}

}
