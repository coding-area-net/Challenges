package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeExecutionData;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.ChallengeAction;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.extraworld.WaterMLGChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import org.bukkit.Material;

import java.util.Map;

public class WaterMLGAction extends ChallengeAction {

  public WaterMLGAction(String name) {
    super(name);
  }

  @Override
  public Material getMaterial() {
    return Material.WATER_BUCKET;
  }

  @Override
  public void execute(
    ChallengeExecutionData executionData,
    Map<String, String[]> subActions) {
    AbstractChallenge.getFirstInstance(WaterMLGChallenge.class).startWorldChallenge();
  }

}
