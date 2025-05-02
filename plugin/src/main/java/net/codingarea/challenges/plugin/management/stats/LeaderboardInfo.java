package net.codingarea.challenges.plugin.management.stats;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

public final class LeaderboardInfo {

  private final Map<Statistic, Integer> values = new EnumMap<>(Statistic.class);

  public void setPlace(@Nonnull Statistic statistic, @Nonnegative int place) {
    values.put(statistic, place);
  }

  public int getPlace(@Nonnull Statistic statistic) {
    return values.getOrDefault(statistic, 1);
  }

}
