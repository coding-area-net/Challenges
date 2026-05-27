package net.codingarea.challenges.plugin.management.stats;

import lombok.Getter;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.management.scheduler.policy.ChallengeStatusPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.spigot.listener.StatsListener;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.database.exceptions.DatabaseException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class StatsManager implements Listener {

  @Getter
  private final boolean enabled, noStatsAfterCheating;

  private final Map<UUID, PlayerStats> cache = new ConcurrentHashMap<>();

  private List<PlayerStats> cachedLeaderboard;
  private long leaderboardCacheTimestamp;

  public StatsManager() {
    enabled = Challenges.getInstance().getConfigDocument().getBoolean("save-player-stats");
    noStatsAfterCheating = enabled && Challenges.getInstance().getConfigDocument().getBoolean("no-stats-after-cheating");
  }

  @NotNull
  public static Comparator<PlayerStats> getStatsComparator(@NotNull Statistic statistic) {
    return Comparator.<PlayerStats>comparingDouble(value -> value.getStatisticValue(statistic)).reversed();
  }

  public void register() {
    if (enabled) {
      StatsListener listener = new StatsListener();
      ChallengeAPI.registerScheduler(this, listener);
      Challenges.getInstance().registerListener(this, listener);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onLeave(@NotNull PlayerQuitEvent event) {
    PlayerStats cached = cache.remove(event.getPlayer().getUniqueId());
    if (cached == null) return;
    store(event.getPlayer().getUniqueId(), cached);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(@NotNull PlayerJoinEvent event) {
    getStats(event.getPlayer()); // Cache stats
  }

  @ScheduledTask(ticks = 30 * 20, challengePolicy = ChallengeStatusPolicy.ALWAYS)
  public void storeCached() {
    for (Entry<UUID, PlayerStats> entry : cache.entrySet()) {
      store(entry.getKey(), entry.getValue());
    }
  }

  private void store(@NotNull UUID uuid, @NotNull PlayerStats stats) {
    try {
      Challenges.getInstance().getDatabaseManager().getDatabase()
        .insertOrUpdate("challenges")
        .where("uuid", uuid)
        .set("stats", stats.asDocument())
        .execute();
      Logger.debug("Saved stats for {}: {}", uuid, stats);
    } catch (DatabaseException ex) {
      Logger.error("Could not save player stats for {}", uuid, ex);
    }
  }

  @NotNull
  public PlayerStats getStats(@NotNull Player player) {
    return getStats(player.getUniqueId(), player.getName());
  }

  @NotNull
  public PlayerStats getStats(@NotNull UUID uuid, @NotNull String name) {
    PlayerStats cached = cache.get(uuid);
    if (cached != null) return cached;

    try {
      PlayerStats stats = getStatsFromDatabase(uuid, name);
      if (Bukkit.getPlayer(uuid) != null) {
        cache.put(uuid, stats);
        Logger.debug("Loaded stats for uuid {}: {}", uuid, stats);
      }
      return stats;
    } catch (DatabaseException ex) {
      Logger.error("Could not get player stats for {}", uuid, ex);
      return new PlayerStats(uuid, name);
    }
  }

  @NotNull
  private PlayerStats getStatsFromDatabase(@NotNull UUID uuid, @NotNull String name) throws DatabaseException {
    return Challenges.getInstance().getDatabaseManager().getDatabase()
      .query("challenges")
      .select("stats", "name")
      .where("uuid", uuid)
      .execute().first()
      .map(result -> new PlayerStats(uuid, Objects.requireNonNull(result.getString("name")), result.getDocument("stats")))
      .orElse(new PlayerStats(uuid, name));
  }

  @NotNull
  private List<PlayerStats> getAllStats() throws DatabaseException {
    if (cachedLeaderboard != null && System.currentTimeMillis() - leaderboardCacheTimestamp < 3 * 60 * 1000) {
      return cachedLeaderboard;
    }

    leaderboardCacheTimestamp = System.currentTimeMillis();
    return cachedLeaderboard = getAllStats0();
  }

  @NotNull
  private List<PlayerStats> getAllStats0() throws DatabaseException {
    return Challenges.getInstance().getDatabaseManager().getDatabase()
      .query("challenges")
      .select("uuid", "stats", "name")
      .execute().all()
      .filter(result -> result.getUUID("uuid") != null)
      .map(result -> new PlayerStats(Objects.requireNonNull(result.getUUID("uuid")), Objects.requireNonNull(result.getString("name")), result.getDocument("stats")))
      .collect(Collectors.toList());
  }

  @NotNull
  public LeaderboardInfo getLeaderboardInfo(@NotNull UUID uuid) {
    try {
      List<PlayerStats> stats = getAllStats();
      LeaderboardInfo info = new LeaderboardInfo();
      for (Statistic statistic : Statistic.values()) {
        int place = determineIndex(new ArrayList<>(stats), PlayerStats::getPlayerUUID, uuid, getStatsComparator(statistic)) + 1;
        info.setPlace(statistic, place);
      }

      return info;
    } catch (DatabaseException ex) {
      Logger.error("Could not get player leaderboard information for {}", uuid, ex);
      return new LeaderboardInfo();
    }
  }

  @NotNull
  public List<PlayerStats> getLeaderboard(@NotNull Statistic statistic) {
    try {
      List<PlayerStats> stats = getAllStats();
      stats.sort(getStatsComparator(statistic));
      return stats;
    } catch (Exception ex) {
      Logger.error("Could not get leaderboard in {}", statistic, ex);
      return new ArrayList<>();
    }
  }

  private <T, U> int determineIndex(@NotNull List<T> list, @NotNull Function<T, U> extractor, @NotNull U target, @NotNull Comparator<T> sort) {
    list.sort(sort);
    int index = 0;
    for (T t : list) {
      U u = extractor.apply(t);
      if (target.equals(u)) return index;
      index++;
    }
    return index;
  }

  public boolean hasDatabaseConnection() {
    return Challenges.getInstance().getDatabaseManager().getDatabase() != null && Challenges.getInstance().getDatabaseManager().isConnected();
  }

}
