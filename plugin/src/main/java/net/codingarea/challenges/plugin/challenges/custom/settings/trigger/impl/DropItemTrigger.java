package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DropItemTrigger extends ChallengeTrigger {

  public DropItemTrigger(String name) {
    super(name);
  }

  @Override
  public Material getMaterial() {
    return Material.DROPPER;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onItemDrop(PlayerDropItemEvent event) {
    createData()
      .entity(event.getPlayer())
      .event(event)
      .execute();
  }

}
