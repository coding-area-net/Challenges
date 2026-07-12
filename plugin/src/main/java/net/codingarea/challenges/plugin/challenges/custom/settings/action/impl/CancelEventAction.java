package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeExecutionData;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.ChallengeAction;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CancelEventAction extends ChallengeAction {

  public static boolean inCanceling;

  public CancelEventAction(String name) {
    super(name);
  }

  public static void onPreTrigger() {
    inCanceling = false;
  }

  public static boolean shouldCancel() {
    if (inCanceling) {
      inCanceling = false;
      return true;
    }
    return false;
  }

  @NotNull
  @Override
  public Material getMaterial() {
    return Material.BARRIER;
  }

  @Override
  public void execute(ChallengeExecutionData executionData, Map<String, String[]> subActions) {
    inCanceling = true;
  }

}
