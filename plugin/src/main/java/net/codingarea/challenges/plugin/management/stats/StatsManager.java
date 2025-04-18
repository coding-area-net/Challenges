package net.codingarea.challenges.plugin.management.stats;

import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.database.exceptions.DatabaseException;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.management.scheduler.policy.ChallengeStatusPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.spigot.listener.StatsListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
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

  @Nonnull
  public static Comparator<PlayerStats> getStatsComparator(@Nonnull Statistic statistic) {
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
  public void onLeave(@Nonnull PlayerQuitEvent event) {
    PlayerStats cached = cache.remove(event.getPlayer().getUniqueId());
    if (cached == null) return;
    store(event.getPlayer().getUniqueId(), cached);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(@Nonnull PlayerJoinEvent event) {
    getStats(event.getPlayer()); // Cache stats
  }

  @ScheduledTask(ticks = 30 * 20, challengePolicy = ChallengeStatusPolicy.ALWAYS)
  public void storeCached() {
    for (Entry<UUID, PlayerStats> entry : cache.entrySet()) {
      store(entry.getKey(), entry.getValue());
    }
  }

  private void store(@Nonnull UUID uuid, @Nonnull PlayerStats stats) {
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

  @Nonnull
  public PlayerStats getStats(@Nonnull Player player) {
    return getStats(player.getUniqueId(), player.getName());
  }

  @Nonnull
  public PlayerStats getStats(@Nonnull UUID uuid, @Nonnull String name) {
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

  @Nonnull
  private PlayerStats getStatsFromDatabase(@Nonnull UUID uuid, @Nonnull String name) throws DatabaseException {
    return Challenges.getInstance().getDatabaseManager().getDatabase()
      .query("challenges")
      .select("stats", "name")
      .where("uuid", uuid)
      .execute().first()
      .map(result -> new PlayerStats(uuid, Objects.requireNonNull(result.getString("name")), result.getDocument("stats")))
      .orElse(new PlayerStats(uuid, name));
  }

  @Nonnull
  private List<PlayerStats> getAllStats() throws DatabaseException {
    if (cachedLeaderboard != null && System.currentTimeMillis() - leaderboardCacheTimestamp < 3 * 60 * 1000) {
      return cachedLeaderboard;
    }

    leaderboardCacheTimestamp = System.currentTimeMillis();
    return cachedLeaderboard = getAllStats0();
  }

  @Nonnull
  private List<PlayerStats> getAllStats0() throws DatabaseException {
    return Challenges.getInstance().getDatabaseManager().getDatabase()
      .query("challenges")
      .select("uuid", "stats", "name")
      .execute().all()
      .filter(result -> result.getUUID("uuid") != null)
      .map(result -> new PlayerStats(Objects.requireNonNull(result.getUUID("uuid")), Objects.requireNonNull(result.getString("name")), result.getDocument("stats")))
      .collect(Collectors.toList());
  }

  @Nonnull
  public LeaderboardInfo getLeaderboardInfo(@Nonnull UUID uuid) {
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

  @Nonnull
  public List<PlayerStats> getLeaderboard(@Nonnull Statistic statistic) {
    try {
      List<PlayerStats> stats = getAllStats();
      stats.sort(getStatsComparator(statistic));
      return stats;
    } catch (Exception ex) {
      Logger.error("Could not get leaderboard in {}", statistic, ex);
      return new ArrayList<>();
    }
  }

  private <T, U> int determineIndex(@Nonnull List<T> list, @Nonnull Function<T, U> extractor, @Nonnull U target, @Nonnull Comparator<T> sort) {
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
