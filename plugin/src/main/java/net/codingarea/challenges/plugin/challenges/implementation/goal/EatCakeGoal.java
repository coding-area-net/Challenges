package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@Since("2.1.2")
public class EatCakeGoal extends SettingGoal {

  public EatCakeGoal() {
    super(SettingCategory.FASTEST_TIME, new ItemStack(Material.CAKE), "eat-cake");
  }

  @Override
  public void getWinnersOnEnd(@NotNull List<Player> winners) {

  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInteract(PlayerInteractEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getClickedBlock() == null) return;
    if (event.getClickedBlock().getType() != Material.CAKE) return;
    if (event.getPlayer().getFoodLevel() >= 20) return;
    // Execute on next tick to prevent hunger bar not filling up
    Bukkit.getScheduler().runTask(plugin, () -> {
      ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED, () -> Collections.singletonList(event.getPlayer()));
    });
  }

}
