package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.collection.IRandom;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifierGoal;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import net.codingarea.challenges.plugin.utils.misc.ParticleUtils;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World.Environment;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
@Since("2.1.0")
public class RaceGoal extends SettingModifierGoal {

	protected long seed = IRandom.create().getSeed();

	private Location goal;

	public RaceGoal() {
		super(MenuType.GOAL, 1, 30, 5);
		setCategory(SettingCategory.FASTEST_TIME);
	}

	@Override
	public void getWinnersOnEnd(@NotNull List<Player> winners) {
	}

	@Override
	protected void onEnable() {
		reloadGoalLocation();
		bossbar.setContent((bar, player) -> {
			bar.setColor(BarColor.GREEN);
			if (player.getWorld() == goal.getWorld()) {

				Location relativeGoal = goal.clone();
				relativeGoal.setY(player.getLocation().getY());
				relativeGoal.add(0.5, 0, 0.5);
				int distance = (int) Math.round(player.getLocation().distance(relativeGoal));

				bar.setTitle(Message.forName("bossbar-race-goal-info")
						.asString(goal.getBlockX(), goal.getBlockZ(), distance));
			} else {
				bar.setTitle(Message.forName("bossbar-race-goal")
						.asString(goal.getBlockX(), goal.getBlockZ()));
			}

		});
		bossbar.show();
	}

	@Override
	protected void onDisable() {
		bossbar.hide();
		goal = null;
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChangeChallengeValueTitle(this, Message.forName("subtitle-range-blocks").asString(getValue() * 100));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-range-blocks-description").asArray(getValue() * 100);
	}


	@Override
	protected void onValueChange() {
		reloadGoalLocation();
		bossbar.update();
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.STRUCTURE_VOID, Message.forName("item-race-goal"));
	}

	@ScheduledTask(ticks = 20)
	public void onSecond() {
		if (goal == null) return;
		bossbar.update();
		broadcast(player -> {
			Location relativeGoal = goal.clone();
			relativeGoal.setY(player.getLocation().getY());
			relativeGoal.add(0.5, 0, 0.5);
			ParticleUtils.drawLine(player, player.getLocation(), relativeGoal, MinecraftNameWrapper.REDSTONE_DUST, new DustOptions(
					Color.LIME, 1), 1, 0.5, 50);

			if (player.getWorld() != goal.getWorld()) return;
			if (player.getLocation().distance(relativeGoal) > 20) return;
			ParticleUtils.spawnParticleCircleAroundRadius(Challenges.getInstance(), relativeGoal,
				MinecraftNameWrapper.INSTANT_EFFECT, 0.75, 0.5);
		});
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (BlockUtils.isSameBlockLocationIgnoreHeight(event.getFrom(), event.getTo())) return;
		if (event.getTo().getWorld() != goal.getWorld()) return;
		if (event.getTo() == null) return;
		if (BlockUtils.isSameBlockLocationIgnoreHeight(event.getTo(), goal)) {
			Message.forName("race-goal-reached").broadcast(Prefix.CHALLENGES, NameHelper.getName(event.getPlayer()));
			ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED, () -> Collections.singletonList(event.getPlayer()));
			ParticleUtils.spawnParticleCircleAroundRadius(Challenges.getInstance(), event.getTo(), MinecraftNameWrapper.ENTITY_EFFECT, 0.75, 2);
		}
	}

	private void reloadGoalLocation() {

		World world = ChallengeAPI.getGameWorld(Environment.NORMAL);
		int y = 999;

		IRandom iRandom = IRandom.create(seed);
		// Some more calculations to prevent the result cords to always end with the same digits.
		int max = getValue() * 100 - iRandom.nextInt(getValue() - getValue() / 2);

		int x = iRandom.nextInt(max * 2) - max;
		int z = iRandom.nextInt(max * 2) - max;
		goal = new Location(world, x, y, z);
	}

	@Override
	public void loadGameState(@NotNull Document document) {
		if (!document.contains("seed")) {
			seed = IRandom.create().getSeed();
			return;
		}

		long seed = document.getLong("seed");
		if (this.seed == seed) return;
		this.seed = seed;

		if (!isEnabled()) return;
		reloadGoalLocation();
		bossbar.update();
	}

	@Override
	public void writeGameState(@NotNull Document document) {
		document.set("seed", seed);
	}

}
