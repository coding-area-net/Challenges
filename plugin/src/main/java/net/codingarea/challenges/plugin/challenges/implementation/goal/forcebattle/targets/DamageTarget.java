package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets;

import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.ExtremeForceBattleGoal;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import org.bukkit.entity.Player;

public class DamageTarget extends ForceTarget<Integer> {

  public DamageTarget(Integer target) {
    super(target);
  }

  @Override
  public boolean check(Player player) {
    return false;
  }

  @Override
  public Object toMessage() {
    return (double) target / 2;
  }

  @Override
  public String getName() {
    return String.valueOf((double) target / 2);
  }

  @Override
  public MessageKey getNewTargetMessage() {
    return MessageKey.of("extreme-force-battle-new-damage");
  }

  @Override
  public MessageKey getCompletedMessage() {
    return MessageKey.of("extreme-force-battle-took-damage");
  }

  @Override
  public ExtremeForceBattleGoal.TargetType getType() {
    return ExtremeForceBattleGoal.TargetType.DAMAGE;
  }

  @Override
  public MessageKey getScoreboardDisplayMessage() {
    return MessageKey.of("force-battle-damage-target-display");
  }

}
