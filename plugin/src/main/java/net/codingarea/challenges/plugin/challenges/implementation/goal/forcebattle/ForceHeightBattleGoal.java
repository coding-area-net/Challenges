package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle;

import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets.HeightTarget;
import net.codingarea.challenges.plugin.challenges.type.abstraction.ForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ForceHeightBattleGoal extends ForceBattleGoal<HeightTarget> {

  public ForceHeightBattleGoal() {
    super(Message.forName("menu-force-height-battle-goal-settings"));
  }

  @Override
  protected HeightTarget[] getTargetsPossibleToFind() {
    return new HeightTarget[0]; //Not used
  }

  @Override
  protected HeightTarget getRandomTarget(Player player) {
    World world = player.getWorld();
    return new HeightTarget(globalRandom.range(BukkitReflectionUtils.getMinHeight(world), world.getMaxHeight()));
  }

  @Override
  public HeightTarget getTargetFromDocument(Document document, String path) {
    return new HeightTarget(document.getInt(path));
  }

  @Override
  public List<HeightTarget> getListFromDocument(Document document, String path) {
    return document.getIntegerList(path).stream().map(HeightTarget::new).collect(Collectors.toList());
  }

  @Override
  protected Message getLeaderboardTitleMessage() {
    return Message.forName("force-height-battle-leaderboard");
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.RABBIT_FOOT, Message.forName("item-force-height-battle-goal"));
  }

  @Override
  protected boolean shouldRegisterDupedTargetsSetting() {
    return false;
  }

  @ScheduledTask(ticks = 5, async = false, timerPolicy = TimerPolicy.STARTED)
  public void checkHeights() {
    if (!shouldExecuteEffect()) return;
    broadcastFiltered(player -> {
      HeightTarget target = currentTarget.get(player.getUniqueId());
      if (target != null && target.check(player)) {
        handleTargetFound(player);
      }
    });
  }
}
