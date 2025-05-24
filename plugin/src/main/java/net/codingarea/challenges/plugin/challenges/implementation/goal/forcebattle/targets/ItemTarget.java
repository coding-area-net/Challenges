package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets;

import net.codingarea.challenges.plugin.utils.item.ItemUtils;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.ExtremeForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sehrschlechtYT | https://github.com/sehrschlechtYT
 * @since 2.2.3
 */
public class ItemTarget extends ForceTarget<Material> {

    public ItemTarget(Material target) {
        super(target);
    }

    @Override
    public boolean check(Player player) {
        return player.getInventory().contains(target);
    }

    public static List<Material> getPossibleItems() {
        List<Material> materials = new ArrayList<>(Arrays.asList(ExperimentalUtils.getMaterials()));
        materials.removeIf(material -> !material.isItem());
        materials.removeIf(material -> !ItemUtils.isObtainableInSurvival(material));
        return materials;
    }

    @Override
    public Object toMessage() {
        return target;
    }

    @Override
    public String getName() {
        return BukkitStringUtils.getServerSideItemComponent(target).toPlainText();
    }

    @Override
    public Message getNewTargetMessage() {
        return Message.forName("force-item-battle-new-item");
    }

    @Override
    public Message getCompletedMessage() {
        return Message.forName("force-item-battle-found");
    }

    @Override
    public ExtremeForceBattleGoal.TargetType getType() {
        return ExtremeForceBattleGoal.TargetType.ITEM;
    }

    @Override
    public Message getScoreboardDisplayMessage() {
        return Message.forName("force-battle-item-target-display");
    }

    @Override
    public String toString() {
        return target.name();
    }

}
