package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle;

import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets.PositionTarget;
import net.codingarea.challenges.plugin.challenges.type.abstraction.ForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sehrschlechtYT | <a href="https://github.com/sehrschlechtYT">...</a>
 * @since 2.2.3
 */
public class ForcePositionBattleGoal extends ForceBattleGoal<PositionTarget> {
    public ForcePositionBattleGoal() {
        super(Message.forName("menu-force-position-battle-goal-settings"));
        registerSetting("radius", new NumberSubSetting(
                () -> new ItemBuilder(Material.DIAMOND_BOOTS, Message.forName("item-force-position-battle-radius")),
                value -> null,
                value -> "§e" + (value * 100),
                1,
                100,
                15
        ));
    }

    @NotNull
    @Override
    public ItemBuilder createDisplayItem() {
        return new ItemBuilder(Material.DIAMOND_BOOTS, Message.forName("item-force-position-battle-goal"));
    }

    @Override
    protected PositionTarget[] getTargetsPossibleToFind() {
        return new PositionTarget[0]; //Not used
    }

    @Override
    protected PositionTarget getRandomTarget(Player player) {
        return new PositionTarget(player, getRadius());
    }

    @Override
    public PositionTarget getTargetFromDocument(Document document, String path) {
        return new PositionTarget(
                document.getDocument(path).getDouble("x"),
                document.getDocument(path).getDouble("z")
        );
    }

    @Override
    public List<PositionTarget> getListFromDocument(Document document, String path) {
        return document.getDocumentList(path).stream().map(doc -> new PositionTarget(doc.getDouble("x"), doc.getDouble("z"))).collect(Collectors.toList());
    }

    @Override
    protected Message getLeaderboardTitleMessage() {
        return Message.forName("force-position-battle-leaderboard");
    }

    @Override
    protected boolean shouldRegisterDupedTargetsSetting() {
        return false;
    }

    @ScheduledTask(ticks = 5, async = false, timerPolicy = TimerPolicy.STARTED)
    public void checkPositions() {
        if (!shouldExecuteEffect()) return;
        broadcastFiltered(player -> {
            PositionTarget target = currentTarget.get(player.getUniqueId());
            if (target != null && target.check(player)) {
                handleTargetFound(player);
            }
        });
    }

    protected int getRadius() {
        return getSetting("radius").getAsInt() * 100;
    }
}
