package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.PlayerTargetAction;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.effect.PermanentEffectOnDamageChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class AddPermanentEffectAction extends PlayerTargetAction {

  PermanentEffectOnDamageChallenge instance = AbstractChallenge
    .getFirstInstance(PermanentEffectOnDamageChallenge.class);

  public AddPermanentEffectAction(String name) {
    super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(false, true));
  }

  @Override
  public Material getMaterial() {
    return Material.MAGMA_CREAM;
  }

  @Override
  public void executeForPlayer(Player player, Map<String, String[]> subActions) {
    instance.addRandomEffect(player);
    instance.updateEffects();
  }

}
