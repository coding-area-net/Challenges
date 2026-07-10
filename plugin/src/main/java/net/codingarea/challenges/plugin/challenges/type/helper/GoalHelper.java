package net.codingarea.challenges.plugin.challenges.type.helper;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeScoreboard.ScoreboardInstance;
import net.codingarea.commons.common.collection.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

public final class GoalHelper {

  public static final int LEADERBOARD_SIZE = 9;

  private GoalHelper() {
  }

  public static void handleSetEnabled(@NotNull IGoal goal, boolean enabled) {
    if (Challenges.getInstance().getChallengeManager().getCurrentGoal() != goal && enabled) {
      Challenges.getInstance().getChallengeManager().setCurrentGoal(goal);
      goal.playStatusUpdateTitle();
    } else if (Challenges.getInstance().getChallengeManager().getCurrentGoal() == goal && !enabled) {
      Challenges.getInstance().getChallengeManager().setCurrentGoal(null);
      goal.playStatusUpdateTitle();
    }
  }

  @NotNull
  public static SortedMap<Integer, List<Player>> createLeaderboardFromPoints(@NotNull Map<Player, Integer> points) {
    SortedMap<Integer, List<Player>> leaderboard = new TreeMap<>(Collections.reverseOrder());
    for (Entry<Player, Integer> entry : points.entrySet()) {
      List<Player> players = leaderboard.computeIfAbsent(entry.getValue(), key -> new ArrayList<>());
      players.add(entry.getKey());
    }
    return leaderboard;
  }

  @NotNull
  public static <V> Map<Player, Integer> createPointsFromValues(@NotNull AtomicInteger mostPoints, @NotNull Map<UUID, V> map, @NotNull ToIntBiFunction<UUID, V> mapper, boolean zeros) {
    Map<Player, Integer> result = new HashMap<>();
    if (zeros) ChallengeAPI.getIngamePlayers().forEach(player -> result.put(player, 0));
    for (Entry<UUID, V> entry : map.entrySet()) {
      Player player = Bukkit.getPlayer(entry.getKey());
      if (player == null)
        continue;
      // Ignore spectators when playing but show spectators after challenge end
      if (!AbstractChallenge.ignorePlayer(player) || (ChallengeAPI.isPaused() && player.getGameMode() == GameMode.SPECTATOR)) {
        int points = mapper.applyAsInt(entry.getKey(), entry.getValue());
        if (points == 0)
          continue;

        result.put(player, points);

        if (points >= mostPoints.get())
          mostPoints.set(points);
      }
    }
    return result;
  }

  public static <E> int determinePosition(@NotNull SortedMap<?, List<E>> map, @NotNull E target) {
    int position = 1;
    for (Entry<?, List<E>> entry : map.entrySet()) {
      if (entry.getValue().contains(target)) break;
      position++;
    }
    return position;
  }

  @NotNull
  public static BiConsumer<ScoreboardInstance, Player> createScoreboard(@NotNull Supplier<Map<Player, Integer>> points) {
    return createScoreboard(points, _ -> new LinkedList<>());
  }

  @NotNull
  public static BiConsumer<ScoreboardInstance, Player> createScoreboard(@NotNull Supplier<Map<Player, Integer>> points, Function<Player, List<LocalizableMessage>> additionalLines) {
    return (scoreboard, player) -> {
      SortedMap<Integer, List<Player>> leaderboard = GoalHelper.createLeaderboardFromPoints(points.get());
      int playerPlace = GoalHelper.determinePosition(leaderboard, player);

      scoreboard.addEmptyLine();
      scoreboard.addLine(MessageKey.of("your-place"), playerPlace);
      scoreboard.addEmptyLine();
      {
        int place = 1;
        int displayed = 0;
        for (Entry<Integer, List<Player>> entry : leaderboard.entrySet()) {
          List<Player> players = entry.getValue();
          for (Player current : players) {
            displayed++;
            if (displayed >= LEADERBOARD_SIZE) break;
            scoreboard.addLine(MessageKey.of("scoreboard-leaderboard"), place, current, NumberFormatter.MIDDLE_NUMBER.format(entry.getKey()));
          }
          if (displayed == LEADERBOARD_SIZE) break;
          place++;
        }
      }
      scoreboard.addEmptyLine();

      List<LocalizableMessage> lines = additionalLines.apply(player);
      if (!lines.isEmpty()) {
        int linesThatCanBeAdded = scoreboard.getRemainingLines() - 1;
        for (int i = 0; i < lines.size() && linesThatCanBeAdded > 0; i++) {
          linesThatCanBeAdded--;
          LocalizableMessage line = lines.get(i);
          scoreboard.addLine(line);
        }
      }
    };
  }

  public static void getWinnersOnEnd(@NotNull List<Player> winners, @NotNull Map<Player, Integer> points) {
    AtomicInteger mostPoints = new AtomicInteger();
    List<Player> currentWinners = new LinkedList<>();

    for (Entry<Player, Integer> entry : points.entrySet()) {
      if (entry.getValue() <= 0) continue;
      if (entry.getValue() == mostPoints.get()) {
        currentWinners.add(entry.getKey());
        continue;
      }
      if (entry.getValue() > mostPoints.get()) {
        mostPoints.set(entry.getValue());
        currentWinners.clear();
        currentWinners.add(entry.getKey());
      }
    }

    winners.addAll(currentWinners);
  }

}
