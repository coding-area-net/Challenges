package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.EntityTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.utils.misc.EntityUtils;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BoostEntityInAirAction extends EntityTargetAction {

  public BoostEntityInAirAction(String name) {
    super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(true).createValueChild().fill(builder -> {
      builder.addModifierSetting("strength",
        new ItemStack(Material.FEATHER), MessageKey.of("custom.action.boost_in_air.sub.strength"),
        1, 1, 10,
        ChallengeHelper::getSettingsDescriptionModifierStrength
      );
    }));
  }

  @NotNull
  @Override
  public Material getMaterial() {
    return Material.FEATHER;
  }

  @Override
  public void executeFor(Entity entity, Map<String, String[]> subActions) {

    int strength = 1;
    try {
      strength = Integer.parseInt(subActions.get("strength")[0]);
    } catch (NumberFormatException exception) {
      Logger.error("", exception);
    }

    Vector velocityToAdd = new Vector(0, 1, 0).multiply(strength);
    Vector newVelocity = EntityUtils.getSucceedingVelocity(entity.getVelocity()).add(velocityToAdd);
    entity.setVelocity(newVelocity);
  }

}
