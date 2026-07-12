package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeExecutionData;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.ChallengeAction;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.extraworld.JumpAndRunChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class JumpAndRunAction extends ChallengeAction {

  public JumpAndRunAction(String name) {
    super(name);
  }

  @NotNull
  @Override
  public Material getMaterial() {
    return Material.ACACIA_STAIRS;
  }

  @Override
  public void execute(
    ChallengeExecutionData executionData,
    Map<String, String[]> subActions) {
    AbstractChallenge.getFirstInstance(JumpAndRunChallenge.class).startWorldChallenge();
  }

}
