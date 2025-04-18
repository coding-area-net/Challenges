package net.codingarea.challenges.plugin.challenges.custom.settings.action;

import net.anweisen.utilities.common.collection.IRandom;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeExecutionData;

import java.util.Map;

public interface IChallengeAction {

  IRandom random = IRandom.create();

  void execute(ChallengeExecutionData executionData, Map<String, String[]> subActions);

}
