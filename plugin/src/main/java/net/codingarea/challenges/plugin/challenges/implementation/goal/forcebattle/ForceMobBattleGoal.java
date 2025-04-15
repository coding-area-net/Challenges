package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle;

import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets.MobTarget;
import net.codingarea.challenges.plugin.challenges.type.abstraction.ForceBattleDisplayGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sehrschlechtYT | <a href="https://github.com/sehrschlechtYT">...</a>
 * @since 2.2.0
 */
@Since("2.2.0")
public class ForceMobBattleGoal extends ForceBattleDisplayGoal<MobTarget> {

	public ForceMobBattleGoal() {
		super(Message.forName("menu-force-mob-battle-goal-settings"));
	}

	@Override
	protected MobTarget[] getTargetsPossibleToFind() {
		List<EntityType> entityTypes = MobTarget.getPossibleMobs();
		return entityTypes.stream().map(MobTarget::new).toArray(MobTarget[]::new);
	}

	@Override
	protected Message getLeaderboardTitleMessage() {
		return Message.forName("force-mob-battle-leaderboard");
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.BOW, Message.forName("item-force-mob-battle-goal"));
	}

	@Override
	public MobTarget getTargetFromDocument(Document document, String path) {
		return new MobTarget(document.getEnum(path, EntityType.class));
	}

	@Override
	public List<MobTarget> getListFromDocument(Document document, String path) {
		return document.getEnumList(path, EntityType.class).stream().map(MobTarget::new).collect(Collectors.toList());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onKill(@Nonnull EntityDeathEvent event) {
		if (!shouldExecuteEffect()) return;
		LivingEntity entity = event.getEntity();
		Player killer = entity.getKiller();
		if (killer == null) return;
		if (ignorePlayer(killer)) return;
		if (currentTarget.get(killer.getUniqueId()) == null) return;
		if (entity.getType() == currentTarget.get(killer.getUniqueId()).getTarget()) {
			handleTargetFound(killer);
		}
	}

}
