package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets;

import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.ExtremeForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ForceTarget<T> {
    protected final T target;

    protected ForceTarget(T target) {
        this.target = target;
    }

    public abstract boolean check(Player player);
    public abstract Object toMessage();
    public abstract String getName();
    public abstract Message getNewTargetMessage();
    public abstract Message getCompletedMessage();
    public abstract ExtremeForceBattleGoal.TargetType getType();
    public abstract Message getScoreboardDisplayMessage();

    @Override
    public String toString() {
        return target.toString();
    }

    public T getTarget() {
        return target;
    }

    public void updateDisplayStand(@NotNull ArmorStand armorStand) {
        if (target instanceof Material) {
            armorStand.getEquipment().setHelmet(new ItemStack((Material) target));
        } else {
            armorStand.getEquipment().setHelmet(null);
        }
    }

    public Object getTargetSaveObject() {
        return target.toString();
    }

}
