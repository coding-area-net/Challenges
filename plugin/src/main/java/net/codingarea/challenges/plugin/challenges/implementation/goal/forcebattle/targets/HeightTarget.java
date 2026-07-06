package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets;

import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.ExtremeForceBattleGoal;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import org.bukkit.entity.Player;

public class HeightTarget extends ForceTarget<Integer> {

  public HeightTarget(Integer target) {
    super(target);
  }

  @Override
  public boolean check(Player player) {
    return player.getLocation().getBlockY() == target;
  }

  @Override
  public Object toMessage() {
    return target;
  }

  @Override
  public String getName() {
    return target.toString();
  }

  @Override
  public MessageKey getNewTargetMessage() {
    return MessageKey.of("extreme-force-battle-new-height");
  }

  @Override
  public MessageKey getCompletedMessage() {
    return MessageKey.of("extreme-force-battle-reached-height");
  }

  @Override
  public ExtremeForceBattleGoal.TargetType getType() {
    return ExtremeForceBattleGoal.TargetType.HEIGHT;
  }

  @Override
  public MessageKey getScoreboardDisplayMessage() {
    return MessageKey.of("force-battle-height-target-display");
  }

}
