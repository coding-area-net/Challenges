package net.codingarea.challenges.plugin.challenges.custom.settings.trigger;

import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeExecutionData;
import org.bukkit.event.Listener;

public interface IChallengeTrigger extends Listener {

	default ChallengeExecutionData createData() {
		return new ChallengeExecutionData(this);
	}

}
