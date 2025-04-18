package net.codingarea.challenges.plugin.management.stats;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class PlayerStats {

  private final Map<Statistic, Double> values = new EnumMap<>(Statistic.class);
  private final UUID uuid;
  private final String name;

  public PlayerStats(@Nonnull UUID uuid, @Nonnull String name, @Nonnull Document document) {
    this.uuid = uuid;
    this.name = name;
    for (Statistic statistic : Statistic.values()) {
      values.put(statistic, document.getDouble(statistic.name()));
    }
  }

  public PlayerStats(@Nonnull UUID uuid, @Nonnull String name) {
    this.uuid = uuid;
    this.name = name;
  }

  public void incrementStatistic(@Nonnull Statistic statistic, double amount) {
    Logger.debug("Incrementing statistic {} by {} for {}", statistic, amount, name);
    double value = values.getOrDefault(statistic, 0d);
    values.put(statistic, value + amount);
  }

  @Nonnull
  public Document asDocument() {
    Document document = Document.create();
    for (Entry<Statistic, Double> entry : values.entrySet()) {
      document.set(entry.getKey().name(), entry.getValue());
    }
    return document;
  }

  public double getStatisticValue(@Nonnull Statistic statistic) {
    return values.getOrDefault(statistic, 0d);
  }

  @Nonnull
  public UUID getPlayerUUID() {
    return uuid;
  }

  @Nonnull
  public String getPlayerName() {
    return name;
  }

  @Override
  public String toString() {
    return "PlayerStats" + values;
  }

}
