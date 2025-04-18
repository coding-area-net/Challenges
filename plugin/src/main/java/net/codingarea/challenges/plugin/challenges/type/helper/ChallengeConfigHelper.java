package net.codingarea.challenges.plugin.challenges.type.helper;

import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.Challenges;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public final class ChallengeConfigHelper {

	private static final Document settingsDocument;

	static {
		settingsDocument = Challenges.getInstance().getConfigDocument().getDocument("challenge-settings");
	}

	private ChallengeConfigHelper() {
	}

	@Nonnull
	@CheckReturnValue
	public static Document getSettingsDocument() {
		return settingsDocument;
	}

}
