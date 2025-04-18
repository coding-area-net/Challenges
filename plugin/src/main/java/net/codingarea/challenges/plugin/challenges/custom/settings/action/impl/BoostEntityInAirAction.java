package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.EntityTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.EntityUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Map;

public class BoostEntityInAirAction extends EntityTargetAction {

  public BoostEntityInAirAction(String name) {
    super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(true).createValueChild().fill(builder -> {
      builder.addModifierSetting("strength",
        new ItemBuilder(Material.FEATHER, Message.forName("item-custom-action-boost_in_air-strength")),
        1, 1, 10,
        value -> Message.forName("amplifier").asString(),
        integer -> ""
      );
    }));
  }

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
