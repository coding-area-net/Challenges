package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public abstract class CompletableForceChallenge extends AbstractForceChallenge {

	public CompletableForceChallenge(@Nonnull MenuType menu) {
		super(menu);
	}

	public CompletableForceChallenge(@Nonnull MenuType menu, int max) {
		super(menu, max);
	}

	public CompletableForceChallenge(@Nonnull MenuType menu, int min, int max) {
		super(menu, min, max);
	}

	public CompletableForceChallenge(@Nonnull MenuType menu, int min, int max, int defaultValue) {
		super(menu, min, max, defaultValue);
	}

	@Override
	protected final void handleCountdownEnd() {
		broadcastFailedMessage();
		endForcing();
		ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_FAILED);
	}

	protected final void completeForcing(@Nonnull Player player) {
		if (getState() != COUNTDOWN) return;
		broadcastSuccessMessage(player);
		endForcing();
		SoundSample.LEVEL_UP.broadcast();
	}

	protected abstract void broadcastFailedMessage();

	protected abstract void broadcastSuccessMessage(@Nonnull Player player);

}
