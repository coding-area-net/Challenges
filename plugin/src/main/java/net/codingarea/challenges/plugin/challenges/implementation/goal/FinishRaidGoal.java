package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.RequireVersion;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Since("2.0")
@RequireVersion(MinecraftVersion.V1_16)
public class FinishRaidGoal extends SettingGoal {

  public FinishRaidGoal() {
    super(SettingCategory.FASTEST_TIME, new ItemStack(Material.CROSSBOW), "finish-raid");
  }


  @Override
  public void getWinnersOnEnd(@NotNull List<Player> winners) {
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onRaidFinish(@NotNull RaidFinishEvent event) {
    if (!shouldExecuteEffect()) return;
    if (event.getRaid().getStatus() != RaidStatus.VICTORY) return;
    ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED, event::getWinners);
  }

}
