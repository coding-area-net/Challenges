package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets;

import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.ExtremeForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
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
    public Message getNewTargetMessage() {
        return Message.forName("extreme-force-battle-new-damage");
    }

    @Override
    public Message getCompletedMessage() {
        return Message.forName("extreme-force-battle-took-damage");
    }

    @Override
    public ExtremeForceBattleGoal.TargetType getType() {
        return ExtremeForceBattleGoal.TargetType.DAMAGE;
    }

    @Override
    public Message getScoreboardDisplayMessage() {
        return Message.forName("force-battle-damage-target-display");
    }

}
