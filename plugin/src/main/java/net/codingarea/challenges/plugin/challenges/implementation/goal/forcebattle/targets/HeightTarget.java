package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets;

import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.ExtremeForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
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
    public Message getNewTargetMessage() {
        return Message.forName("extreme-force-battle-new-height");
    }

    @Override
    public Message getCompletedMessage() {
        return Message.forName("extreme-force-battle-reached-height");
    }

    @Override
    public ExtremeForceBattleGoal.TargetType getType() {
        return ExtremeForceBattleGoal.TargetType.HEIGHT;
    }

    @Override
    public Message getScoreboardDisplayMessage() {
        return Message.forName("force-battle-height-target-display");
    }

}
