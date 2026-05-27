package net.codingarea.challenges.plugin.management.stats;

import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.common.config.Document;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class PlayerStats {

  private final Map<Statistic, Double> values = new EnumMap<>(Statistic.class);
  private final UUID uuid;
  private final String name;

  public PlayerStats(@NotNull UUID uuid, @NotNull String name, @NotNull Document document) {
    this.uuid = uuid;
    this.name = name;
    for (Statistic statistic : Statistic.values()) {
      values.put(statistic, document.getDouble(statistic.name()));
    }
  }

  public PlayerStats(@NotNull UUID uuid, @NotNull String name) {
    this.uuid = uuid;
    this.name = name;
  }

  public void incrementStatistic(@NotNull Statistic statistic, double amount) {
    Logger.debug("Incrementing statistic {} by {} for {}", statistic, amount, name);
    double value = values.getOrDefault(statistic, 0d);
    values.put(statistic, value + amount);
  }

  @NotNull
  public Document asDocument() {
    Document document = Document.create();
    for (Entry<Statistic, Double> entry : values.entrySet()) {
      document.set(entry.getKey().name(), entry.getValue());
    }
    return document;
  }

  public double getStatisticValue(@NotNull Statistic statistic) {
    return values.getOrDefault(statistic, 0d);
  }

  @NotNull
  public UUID getPlayerUUID() {
    return uuid;
  }

  @NotNull
  public String getPlayerName() {
    return name;
  }

  @Override
  public String toString() {
    return "PlayerStats" + values;
  }

}
