package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public class EntityDeathTrigger extends ChallengeTrigger {

  public EntityDeathTrigger(String name) {
    super(name, SubSettingsHelper.createEntityTypeSettingsBuilder(true, true));
  }

  @Override
  public Material getMaterial() {
    return Material.BONE;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onDeath(@NotNull EntityDeathEvent event) {
    createData()
      .entity(event.getEntity())
      .entityType(event.getEntityType())
      .execute();
  }

}
