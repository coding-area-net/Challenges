package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.common.config.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class CollectionGoal extends SettingGoal {

  private final Map<UUID, List<String>> collections = new HashMap<>();
  protected Object[] target;

  public CollectionGoal(@Nullable SettingCategory category, @NotNull ItemStack displayItemPreset,
                        @NotNull String nameMessageKey, @NotNull Object[] target) {
    super(category, displayItemPreset, nameMessageKey);
    this.target = target;
  }

  public CollectionGoal(@Nullable SettingCategory category, boolean enabledByDefault,
                        @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey, @NotNull Object[] target) {
    super(category, enabledByDefault, displayItemPreset, nameMessageKey);
    this.target = target;
  }

  @Override
  protected void onEnable() {
    scoreboard.setContent(GoalHelper.createScoreboard(() -> getPoints(new AtomicInteger(), true)));
    scoreboard.show();
  }

  @Override
  protected void onDisable() {
    scoreboard.hide();
  }

  @Override
  public void getWinnersOnEnd(@NotNull List<Player> winners) {
    AtomicInteger mostPoints = new AtomicInteger();
    Map<Player, Integer> points = getPoints(mostPoints, false);
    if (mostPoints.get() == 0) return; // Nobody won, nobody has anything

    for (Entry<Player, Integer> entry : points.entrySet()) {
      if (entry.getValue() != mostPoints.get()) continue;
      winners.add(entry.getKey());
    }
  }

  @NotNull
  protected Map<Player, Integer> getPoints(@NotNull AtomicInteger mostPoints, boolean zeros) {
    return GoalHelper.createPointsFromValues(mostPoints, collections, (uuid, strings) -> getCollectionFiltered(uuid).size(), zeros);
  }

  protected void collect(@NotNull Player player, @NotNull Object item, @NotNull Runnable success) {
    if (ignorePlayer(player)) return;
    List<String> collection = getCollectionRaw(player.getUniqueId());
    if (collection.contains(item.toString())) return;
    if (!Arrays.asList(target).contains(item)) return;
    collection.add(item.toString());
    success.run();
    checkCollects();
  }

  protected List<String> getCollectionFiltered(@NotNull UUID uuid) {
    List<String> targetStringList = Arrays.stream(target).map(Object::toString).collect(Collectors.toList());
    return collections.computeIfAbsent(uuid, key -> new ArrayList<>()).stream().filter(targetStringList::contains).collect(Collectors.toList());
  }

  protected List<String> getCollectionRaw(@NotNull UUID uuid) {
    return collections.computeIfAbsent(uuid, key -> new ArrayList<>());
  }

  protected void checkCollects() {
    scoreboard.update();
    for (Player player : Bukkit.getOnlinePlayers()) {
      checkCollects(getCollectionFiltered(player.getUniqueId()));
    }
  }

  protected void checkCollects(@NotNull List<String> collection) {
    if (collection.size() >= target.length)
      ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED);
  }

  @Override
  public void loadGameState(@NotNull Document document) {
    super.loadGameState(document);

    collections.clear();
    Document scores = document.getDocument("scores");
    for (String key : scores.keys()) {
      try {
        UUID uuid = UUID.fromString(key);
        List<String> collection = scores.getStringList(key);
        collections.put(uuid, collection);
      } catch (Exception ex) {
        Logger.error("Could not load scores for {}", key);
      }
    }

    if (scoreboard.isShown()) {
      scoreboard.update();
    }
  }

  @Override
  public void writeGameState(@NotNull Document document) {
    super.writeGameState(document);

    Document scores = document.getDocument("scores");
    collections.forEach((uuid, collection) -> {
      scores.set(uuid.toString(), collection);
    });
  }

  protected void setTarget(@NotNull Object... target) {
    this.target = target;
  }

}
