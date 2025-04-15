package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets;

import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.ExtremeForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sehrschlechtYT | <a href="https://github.com/sehrschlechtYT">...</a>
 * @since 2.2.3
 */
public class AdvancementTarget extends ForceTarget<Advancement> {

    public AdvancementTarget(Advancement target) {
        super(target);
    }

    @Override
    public boolean check(Player player) {
        return player.getAdvancementProgress(target).isDone();
    }

    public static List<Advancement> getPossibleAdvancements() {
        List<Advancement> advancements = new ArrayList<>();
        Bukkit.getServer().advancementIterator().forEachRemaining(advancement -> {
            String string = advancement.getKey().toString();
            if (!string.contains(":recipes/") && !string.endsWith("root")) {
                advancements.add(advancement);
            }
        });
        return advancements;
    }

    @Override
    public Object toMessage() {
        return target;
    }

    @Override
    public String getName() {
        return BukkitStringUtils.getAdvancementTitle(target).toPlainText();
    }

    @Override
    public Message getNewTargetMessage() {
        return Message.forName("force-advancement-battle-new-advancement");
    }

    @Override
    public Message getCompletedMessage() {
        return Message.forName("force-advancement-battle-completed");
    }

    @Override
    public ExtremeForceBattleGoal.TargetType getType() {
        return ExtremeForceBattleGoal.TargetType.ADVANCEMENT;
    }

    @Override
    public Message getScoreboardDisplayMessage() {
        return Message.forName("force-battle-advancement-target-display");
    }

    @Override
    public Object getTargetSaveObject() {
        return target.getKey().toString();
    }

}
