package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.PointsGoal;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
@Since("2.1.0")
public class AllAdvancementGoal extends PointsGoal {

	private final List<Advancement> allAdvancements;
	private final int advancementCount;

	public AllAdvancementGoal() {
		setCategory(SettingCategory.FASTEST_TIME);
		allAdvancements = new LinkedList<>();
		Bukkit.getServer().advancementIterator().forEachRemaining(advancement -> {
			if (!advancement.getKey().toString().contains(":recipes/")) {
				allAdvancements.add(advancement);
			}
		});
		advancementCount = allAdvancements.size();
	}

	@Override
	protected void onEnable() {
		updateAdvancements();
		scoreboard.setContent(GoalHelper.createScoreboard(() ->
				getPoints(new AtomicInteger(), true), player -> {
			return Collections.singletonList(Message.forName("all-advancements-goal").asString(advancementCount));
		}));
		scoreboard.show();
	}

	@Override
	protected void onDisable() {
		scoreboard.hide();
	}

	@Override
	public void getWinnersOnEnd(@NotNull List<Player> winners) {
		broadcastFiltered(player -> {
			if (hasWon(player)) {
				winners.add(player);
			}
		});
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.BOOK, Message.forName("item-all-advancements-goal"));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAdvancement(PlayerAdvancementDoneEvent event) {
		if (event.getAdvancement().getKey().toString().contains(":recipes/")) return;
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		updateAdvancements(event.getPlayer());
		scoreboard.update();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		updateAdvancements(event.getPlayer());
		scoreboard.update();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onGamemodeChange(PlayerGameModeChangeEvent event) {
		if (!shouldExecuteEffect()) return;
		Bukkit.getScheduler().runTask(plugin, (Runnable) scoreboard::update);
	}

	protected void updateAdvancements() {
		broadcastFiltered(this::updateAdvancements);
	}

	protected void updateAdvancements(@Nonnull Player player) {
		int done = 0;
		for (Advancement advancement : allAdvancements) {
			AdvancementProgress progress = player.getAdvancementProgress(advancement);
			if (progress.isDone()) {
				done++;
			}
		}
		setPoints(player.getUniqueId(), done);
		checkForWinning(player);
	}

	protected void checkForWinning(Player player) {
		if (hasWon(player)) {
			ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED);
		}
	}

	protected boolean hasWon(@Nonnull Player player) {
		return getPoints(player.getUniqueId()) >= advancementCount;
	}

}
