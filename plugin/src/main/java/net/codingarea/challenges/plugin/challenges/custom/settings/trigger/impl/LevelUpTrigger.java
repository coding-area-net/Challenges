package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class LevelUpTrigger extends ChallengeTrigger {

  public LevelUpTrigger(String name) {
    super(name);
  }

  @Override
  public Material getMaterial() {
    return Material.ENCHANTING_TABLE;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onLevelUp(PlayerLevelChangeEvent event) {
    if (event.getNewLevel() > event.getOldLevel()) {
      createData()
        .entity(event.getPlayer())
        .execute();
    }
  }

}
