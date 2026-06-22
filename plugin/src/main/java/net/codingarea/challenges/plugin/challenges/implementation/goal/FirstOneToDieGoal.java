package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Since("2.0")
public class FirstOneToDieGoal extends SettingGoal {

  private Player winner;

  public FirstOneToDieGoal() {
    super(SettingCategory.FASTEST_TIME, new ItemStack(Material.STONE_SWORD), "first-one-to-die-goal");
  }

  @Override
  public void getWinnersOnEnd(@NotNull List<Player> winners) {
    if (winner != null)
      winners.add(winner);
  }

  @Override
  protected void onDisable() {
    winner = null;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onDeath(@NotNull PlayerDeathEvent event) {
    if (!shouldExecuteEffect()) return;
    winner = event.getEntity();
    ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED);
  }

}
