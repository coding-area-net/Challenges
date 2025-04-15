package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle;

import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets.DamageTarget;
import net.codingarea.challenges.plugin.challenges.type.abstraction.ForceBattleGoal;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sehrschlechtYT | <a href="https://github.com/sehrschlechtYT">...</a>
 * @since 2.2.3
 */
public class ForceDamageBattleGoal extends ForceBattleGoal<DamageTarget> {

    public ForceDamageBattleGoal() {
        super(Message.forName("menu-force-damage-battle-goal-settings"));
    }

    @NotNull
    @Override
    public ItemBuilder createDisplayItem() {
        return new ItemBuilder(Material.TOTEM_OF_UNDYING, Message.forName("item-force-damage-battle-goal"));
    }

    @Override
    protected DamageTarget[] getTargetsPossibleToFind() {
        return new DamageTarget[0]; //Not used
    }

    @Override
    protected DamageTarget getRandomTarget(Player player) {
        return new DamageTarget(globalRandom.range(1, 19));
    }

    @Override
    public DamageTarget getTargetFromDocument(Document document, String path) {
        return new DamageTarget(document.getInt(path));
    }

    @Override
    public List<DamageTarget> getListFromDocument(Document document, String path) {
        return document.getIntegerList(path).stream().map(DamageTarget::new).collect(Collectors.toList());
    }

    @Override
    protected Message getLeaderboardTitleMessage() {
        return Message.forName("force-damage-battle-leaderboard");
    }

    @Override
    protected boolean shouldRegisterDupedTargetsSetting() {
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(@Nonnull EntityDamageEvent event) {
        if (!shouldExecuteEffect()) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (ignorePlayer(player)) return;
        if (currentTarget.get(player.getUniqueId()) == null) return;
        DamageTarget target = currentTarget.get(player.getUniqueId());
        int damage = (int) ChallengeHelper.getFinalDamage(event);
        if (damage != target.getTarget()) return;
        handleTargetFound(player);
    }

}
