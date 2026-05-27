package net.codingarea.challenges.plugin.challenges.type.helper;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.commons.common.config.Document;
import org.jetbrains.annotations.NotNull;

public final class ChallengeConfigHelper {

  private static final Document settingsDocument;

  static {
    settingsDocument = Challenges.getInstance().getConfigDocument().getDocument("challenge-settings");
  }

  private ChallengeConfigHelper() {
  }

  @NotNull
  public static Document getSettingsDocument() {
    return settingsDocument;
  }

}
