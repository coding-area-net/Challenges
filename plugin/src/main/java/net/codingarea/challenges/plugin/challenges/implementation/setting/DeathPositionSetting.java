package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class DeathPositionSetting extends Setting {

  private static final String POSITION_PREFIX = "death-";

  public DeathPositionSetting() {
    super(MenuType.SETTINGS, null, new ItemStack(Material.MUSIC_DISC_11), "death-position");
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onDeath(@NotNull PlayerDeathEvent event) {
    if (!shouldExecuteEffect()) return;

    PositionSetting setting = AbstractChallenge.getFirstInstance(PositionSetting.class);
    int index = 1;
    while (setting.containsPosition(POSITION_PREFIX + index)) {
      index++;
    }

    Player player = event.getEntity();
    player.performCommand("pos " + POSITION_PREFIX + index);
  }

}
