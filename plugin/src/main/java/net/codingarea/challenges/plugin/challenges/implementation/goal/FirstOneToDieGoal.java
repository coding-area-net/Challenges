package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nonnull;
import java.util.List;

@Since("2.0")
public class FirstOneToDieGoal extends SettingGoal {

  private Player winner;

  public FirstOneToDieGoal() {
    super();
    setCategory(SettingCategory.FASTEST_TIME);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.STONE_SWORD, Message.forName("item-first-one-to-die-goal"));
  }

  @Override
  public void getWinnersOnEnd(@Nonnull List<Player> winners) {
    if (winner != null)
      winners.add(winner);
  }

  @Override
  protected void onDisable() {
    winner = null;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onDeath(@Nonnull PlayerDeathEvent event) {
    if (!shouldExecuteEffect()) return;
    winner = event.getEntity();
    ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED);
  }

}
