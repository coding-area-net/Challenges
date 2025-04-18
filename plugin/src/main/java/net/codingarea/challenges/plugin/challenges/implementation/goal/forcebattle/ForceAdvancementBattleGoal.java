package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle;

import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets.AdvancementTarget;
import net.codingarea.challenges.plugin.challenges.type.abstraction.ForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Since("2.2.0")
public class ForceAdvancementBattleGoal extends ForceBattleGoal<AdvancementTarget> {

	public ForceAdvancementBattleGoal() {
		super(Message.forName("menu-force-advancement-battle-goal-settings"));
	}

	private void resetAdvancementProgress(Player player, Advancement advancement) {
		if (advancement == null) return;
		AdvancementProgress progress = player.getAdvancementProgress(advancement);
		progress.getAwardedCriteria().forEach(progress::revokeCriteria);
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.EXPERIENCE_BOTTLE, Message.forName("item-force-advancement-battle-goal"));
	}

	@Override
	protected AdvancementTarget[] getTargetsPossibleToFind() {
		List<Advancement> advancements = AdvancementTarget.getPossibleAdvancements();
		return advancements.stream().map(AdvancementTarget::new).toArray(AdvancementTarget[]::new);
	}

	@Override
	public AdvancementTarget getTargetFromDocument(Document document, String path) {
		String advancementKey = document.getString(path);
		try {
			NamespacedKey namespacedKey = BukkitReflectionUtils.fromString(advancementKey);
			return new AdvancementTarget(Bukkit.getAdvancement(namespacedKey));
		} catch (Exception exception) {
			// DON'T EXIST
		}
		return null;
	}

	@Override
	public List<AdvancementTarget> getListFromDocument(Document document, String path) {
		List<String> advancementKeys = document.getStringList(path);
		List<AdvancementTarget> advancementTargets = new ArrayList<>();
		for (String advancementKey : advancementKeys) {
			try {
				advancementTargets.add(new AdvancementTarget(Bukkit.getAdvancement(BukkitReflectionUtils.fromString(advancementKey))));
			} catch (Exception exception) {
				// DON'T EXIST
			}
		}
		return advancementTargets;
	}

	@Override
	protected Message getLeaderboardTitleMessage() {
		return Message.forName("force-advancement-battle-leaderboard");
	}

	@Override
	public void setRandomTarget(Player player) {
		super.setRandomTarget(player);
		resetAdvancementProgress(player, currentTarget.get(player.getUniqueId()).getTarget());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAdvancement(PlayerAdvancementDoneEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		Player player = event.getPlayer();
		if (event.getAdvancement() == currentTarget.get(player.getUniqueId()).getTarget()) {
			handleTargetFound(player);
		}
	}

	@Override
	public void handleTargetFound(Player player) {
		super.handleTargetFound(player);
		Advancement advancement = currentTarget.get(player.getUniqueId()).getTarget();
		resetAdvancementProgress(player, advancement);
	}
}
