package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SelectableKey;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class ConsumeItemTrigger extends ChallengeTrigger {

  public ConsumeItemTrigger(String name) {
    super(name, SubSettingsBuilder.createChooseMultipleItem(SubSettingsHelper.ITEM).fill(builder -> {
      for (Material material : ExperimentalUtils.getMaterials()) {
        if (material.isEdible()) {
          builder.addSetting(SelectableKey.of(material.name(), material, material));
        }
      }
    }));
  }

  @Override
  public Material getMaterial() {
    return Material.COOKED_BEEF;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onConsumeItem(PlayerItemConsumeEvent event) {
    createData()
      .entity(event.getPlayer())
      .event(event)
      .data(SubSettingsHelper.ITEM, SubSettingsHelper.ANY, event.getItem().getType().name())
      .execute();
  }

}
