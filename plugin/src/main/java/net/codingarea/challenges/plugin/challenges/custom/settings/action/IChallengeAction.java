package net.codingarea.challenges.plugin.challenges.custom.settings.action;

import net.anweisen.utilities.common.collection.IRandom;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeExecutionData;

import java.util.Map;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public interface IChallengeAction {

	IRandom random = IRandom.create();

	void execute(ChallengeExecutionData executionData, Map<String, String[]> subActions);

}
