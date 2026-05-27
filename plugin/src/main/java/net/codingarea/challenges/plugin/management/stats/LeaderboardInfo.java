package net.codingarea.challenges.plugin.management.stats;

import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public final class LeaderboardInfo {

  private final Map<Statistic, Integer> values = new EnumMap<>(Statistic.class);

  public void setPlace(@NotNull Statistic statistic, int place) {
    values.put(statistic, place);
  }

  public int getPlace(@NotNull Statistic statistic) {
    return values.getOrDefault(statistic, 1);
  }

}
