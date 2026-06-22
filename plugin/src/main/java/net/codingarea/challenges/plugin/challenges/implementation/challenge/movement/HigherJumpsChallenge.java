package net.codingarea.challenges.plugin.challenges.implementation.challenge.movement;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.PlayerJumpEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class HigherJumpsChallenge extends Setting {

  public HigherJumpsChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.MOVEMENT, new ItemStack(Material.RABBIT_FOOT), "higher-jumps-challenge");
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onJump(@NotNull PlayerJumpEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;

    int jumps = getPlayerData(event.getPlayer()).getInt("jumps") + 1;
    getPlayerData(event.getPlayer()).set("jumps", jumps);

    float y = jumps / 7f;
    event.getPlayer().setVelocity(new Vector().setY(y));
  }

}
