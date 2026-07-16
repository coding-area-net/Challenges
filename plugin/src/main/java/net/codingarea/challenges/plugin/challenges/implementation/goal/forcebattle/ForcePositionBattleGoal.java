package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle;

import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets.PositionTarget;
import net.codingarea.challenges.plugin.challenges.type.abstraction.ForceBattleGoal;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.commons.common.config.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class ForcePositionBattleGoal extends ForceBattleGoal<PositionTarget> {

  private final NumberSubSetting radiusSetting;

  public ForcePositionBattleGoal() {
    super(new ItemStack(Material.DIAMOND_BOOTS), "force-position-battle");
    radiusSetting = registerSetting("radius", new NumberSubSetting(
      new ItemStack(Material.DIAMOND_BOOTS), getChallengeMessageKey("sub.radius"),
      1, 100, 15, value -> ChallengeHelper.getSettingsDescriptionRadiusBlocks(value * 100)
    ));
  }

  @Override
  protected PositionTarget[] getTargetsPossibleToFind() {
    return new PositionTarget[0]; //Not used
  }

  @Override
  protected PositionTarget getRandomTarget(Player player) {
    return new PositionTarget(player, getRadius());
  }

  @Override
  public PositionTarget getTargetFromDocument(Document document, String path) {
    return new PositionTarget(
      document.getDocument(path).getDouble("x"),
      document.getDocument(path).getDouble("z")
    );
  }

  @Override
  public List<PositionTarget> getListFromDocument(Document document, String path) {
    return document.getDocumentList(path).stream().map(doc -> new PositionTarget(doc.getDouble("x"), doc.getDouble("z"))).collect(Collectors.toList());
  }

  @Override
  protected Message getLeaderboardTitleMessage() {
    return Message.forName("force-position-battle-leaderboard");
  }

  @Override
  protected boolean shouldRegisterDupedTargetsSetting() {
    return false;
  }

  @ScheduledTask(ticks = 5, async = false, timerPolicy = TimerPolicy.STARTED)
  public void checkPositions() {
    if (!shouldExecuteEffect()) return;
    broadcastFiltered(player -> {
      PositionTarget target = currentTarget.get(player.getUniqueId());
      if (target != null && target.check(player)) {
        handleTargetFound(player);
      }
    });
  }

  protected int getRadius() {
    return radiusSetting.getAsInt() * 100;
  }
}
