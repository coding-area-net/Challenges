package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets;

import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.ExtremeForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import net.codingarea.challenges.plugin.utils.misc.EntityUtils;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MobTarget extends ForceTarget<EntityType> {

    public MobTarget(EntityType target) {
        super(target);
    }

    @Override
    public boolean check(Player player) {
        return false;
    }

    public static List<EntityType> getPossibleMobs() {
        List<EntityType> entityTypes = new ArrayList<>(Arrays.asList(EntityType.values()));
        entityTypes.removeIf(entityType -> !entityType.isSpawnable());
        entityTypes.removeIf(entityType -> !entityType.isAlive());
        Utils.removeEnums(entityTypes, "ILLUSIONER", "ARMOR_STAND", "ZOMBIE_HORSE", "GIANT");

        return entityTypes;
    }

    @Override
    public Object toMessage() {
        return target;
    }

    @Override
    public String getName() {
        return BukkitStringUtils.getEntityName(target).toPlainText();
    }

    @Override
    public Message getNewTargetMessage() {
        return Message.forName("force-mob-battle-new-mob");
    }

    @Override
    public Message getCompletedMessage() {
        return Message.forName("force-mob-battle-killed");
    }

    @Override
    public ExtremeForceBattleGoal.TargetType getType() {
        return ExtremeForceBattleGoal.TargetType.MOB;
    }

    @Override
    public Message getScoreboardDisplayMessage() {
        return Message.forName("force-battle-mob-target-display");
    }

    @Override
    public void updateDisplayStand(@NotNull ArmorStand armorStand) {
        Material spawnEgg = EntityUtils.getSpawnEgg(target);
        if (spawnEgg == null) {
            Objects.requireNonNull(armorStand.getEquipment()).setHelmet(null);
        } else {
            Objects.requireNonNull(armorStand.getEquipment()).setHelmet(new ItemStack(spawnEgg));
        }
    }

}
