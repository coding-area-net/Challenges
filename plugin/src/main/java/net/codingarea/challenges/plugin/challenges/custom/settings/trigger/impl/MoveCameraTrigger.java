package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveCameraTrigger extends ChallengeTrigger {

  public MoveCameraTrigger(String name) {
    super(name);
  }

  @Override
  public Material getMaterial() {
    return Material.COMPASS;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onMove(PlayerMoveEvent event) {
    if (event.getTo() == null) return;
    if (event.getFrom().getDirection().equals(event.getTo().getDirection())) return;
    createData()
      .entity(event.getPlayer())
      .event(event)
      .execute();
  }

}
