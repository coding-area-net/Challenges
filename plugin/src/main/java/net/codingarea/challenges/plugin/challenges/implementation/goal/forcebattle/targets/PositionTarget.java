package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets;

import net.anweisen.utilities.common.collection.IRandom;
import net.anweisen.utilities.common.collection.pair.Tuple;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.ExtremeForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PositionTarget extends ForceTarget<Tuple<Double, Double>> {

    public PositionTarget(double x, double z) {
        super(Tuple.of(x, z));
    }

    public PositionTarget(Player player, int radius) {
        super(Tuple.of(
                player.getLocation().getBlockX() + (double) IRandom.singleton().range(-radius, radius),
                player.getLocation().getBlockZ() + (double) IRandom.singleton().range(-radius, radius)
        ));
    }

    public PositionTarget(Player player) {
        this(player, 1500);
    }

    @Override
    public boolean check(Player player) {
        Location playerLocation = player.getLocation().clone();
        playerLocation.setY(0);
        Location targetLocation = new Location(playerLocation.getWorld(), target.getFirst(), 0, target.getSecond());
        return playerLocation.distance(targetLocation) < 5;
    }

    @Override
    public Object toMessage() {
        return getName();
    }

    @Override
    public String getName() {
        return Message.forName("extreme-force-battle-position").asString(target.getFirst(), target.getSecond());
    }

    @Override
    public Message getNewTargetMessage() {
        return Message.forName("extreme-force-battle-new-position");
    }

    @Override
    public Message getCompletedMessage() {
        return Message.forName("extreme-force-battle-reached-position");
    }

    @Override
    public ExtremeForceBattleGoal.TargetType getType() {
        return ExtremeForceBattleGoal.TargetType.POSITION;
    }

    @Override
    public Message getScoreboardDisplayMessage() {
        return Message.forName("force-battle-position-target-display");
    }

    @Override
    public Object getTargetSaveObject() {
        return Document.create().set("x", target.getFirst()).set("z", target.getSecond());
    }
}
