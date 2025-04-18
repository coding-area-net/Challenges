package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets;

import net.codingarea.challenges.plugin.utils.item.ItemUtils;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.ExtremeForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import net.codingarea.challenges.plugin.utils.misc.EntityUtils;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockTarget extends ForceTarget<Material> {

    public BlockTarget(Material target) {
        super(target);
    }

    @Override
    public boolean check(Player player) {
        return EntityUtils.isStandingOnBlock(player, target);
    }

    public static List<Material> getPossibleBlocks() {
        List<Material> materials = new ArrayList<>(Arrays.asList(ExperimentalUtils.getMaterials()));
        materials.removeIf(material -> !ItemUtils.blockIsAvailableInSurvival(material));
        return materials;
    }

    @Override
    public Object toMessage() {
        return target;
    }

    @Override
    public String getName() {
        return BukkitStringUtils.getItemName(target).toPlainText();
    }

    @Override
    public Message getNewTargetMessage() {
        return Message.forName("force-block-battle-new-block");
    }

    @Override
    public Message getCompletedMessage() {
        return Message.forName("force-block-battle-found");
    }

    @Override
    public ExtremeForceBattleGoal.TargetType getType() {
        return ExtremeForceBattleGoal.TargetType.BLOCK;
    }

    @Override
    public Message getScoreboardDisplayMessage() {
        return Message.forName("force-battle-block-target-display");
    }

    @Override
    public String toString() {
        return target.name();
    }

}
