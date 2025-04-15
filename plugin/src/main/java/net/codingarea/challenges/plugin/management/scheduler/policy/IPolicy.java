package net.codingarea.challenges.plugin.management.scheduler.policy;

import javax.annotation.Nonnull;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public interface IPolicy {

	boolean check(@Nonnull Object holder);

	default boolean isApplicable(@Nonnull Object holder) {
		return true;
	}

}
