package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import org.bukkit.entity.Player;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PointsGoal extends SettingGoal {

	private final Map<UUID, Integer> points = new HashMap<>();

	public PointsGoal() {
		super();
	}

	public PointsGoal(boolean enabledByDefault) {
		super(enabledByDefault);
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		scoreboard.setContent(GoalHelper.createScoreboard(() -> getPoints(new AtomicInteger(), true)));
		scoreboard.show();
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		scoreboard.hide();
	}

	@Override
	public void loadGameState(@Nonnull Document document) {
		super.loadGameState(document);

		Document scores = document.getDocument("scores");
		for (String key : scores.keys()) {
			try {
				UUID uuid = UUID.fromString(key);
				int value = scores.getInt(key);
				points.put(uuid, value);
			} catch (Exception ex) {
				Logger.error("Could not load scores for {}", key);
			}
		}
	}

	@Override
	public void writeGameState(@Nonnull Document document) {
		super.writeGameState(document);

		Document scores = document.getDocument("scores");
		points.forEach((uuid, points) -> scores.set(uuid.toString(), points));
	}

	@Override
	public void getWinnersOnEnd(@Nonnull List<Player> winners) {
		GoalHelper.getWinnersOnEnd(winners, getPoints(new AtomicInteger(), false));
	}

	@Nonnull
	@CheckReturnValue
	protected Map<Player, Integer> getPoints(@Nonnull AtomicInteger mostPoints, boolean zeros) {
		return GoalHelper.createPointsFromValues(mostPoints, points, (uuid, integer) -> integer, zeros);
	}

	protected void collect(@Nonnull Player player) {
		collect(player, 1);
	}

	protected void collect(@Nonnull Player player, int amount) {
		points.compute(player.getUniqueId(), (uuid, points) -> points == null ? amount : points + amount);
		scoreboard.update();
	}

	protected void setPoints(@Nonnull UUID uuid, int amount) {
		points.put(uuid, amount);
		scoreboard.update();
	}

	protected void addPoints(@Nonnull UUID uuid, int amount) {
		points.put(uuid, getPoints(uuid) + amount);
		scoreboard.update();
	}

	protected void removePoints(@Nonnull UUID uuid, int amount) {
		points.put(uuid, getPoints(uuid) - amount);
		scoreboard.update();
	}

	@CheckReturnValue
	protected int getPoints(@Nonnull UUID uuid) {
		Integer points = this.points.get(uuid);
		return points == null ? 0 : points;
	}

}
