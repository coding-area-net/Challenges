package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class CollectionGoal extends SettingGoal {

  private final Map<UUID, List<String>> collections = new HashMap<>();
  protected Object[] target;

  public CollectionGoal(@Nonnull Object[] target) {
    super();
    this.target = target;
  }

  public CollectionGoal(boolean enabledByDefault, @Nonnull Object[] target) {
    super(enabledByDefault);
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
  public void getWinnersOnEnd(@Nonnull List<Player> winners) {
    AtomicInteger mostPoints = new AtomicInteger();
    Map<Player, Integer> points = getPoints(mostPoints, false);
    if (mostPoints.get() == 0) return; // Nobody won, nobody has anything

    for (Entry<Player, Integer> entry : points.entrySet()) {
      if (entry.getValue() != mostPoints.get()) continue;
      winners.add(entry.getKey());
    }
  }

  @Nonnull
  @CheckReturnValue
  protected Map<Player, Integer> getPoints(@Nonnull AtomicInteger mostPoints, boolean zeros) {
    return GoalHelper.createPointsFromValues(mostPoints, collections, (uuid, strings) -> getCollectionFiltered(uuid).size(), zeros);
  }

  protected void collect(@Nonnull Player player, @Nonnull Object item, @Nonnull Runnable success) {
    if (ignorePlayer(player)) return;
    List<String> collection = getCollectionRaw(player.getUniqueId());
    if (collection.contains(item.toString())) return;
    if (!Arrays.asList(target).contains(item)) return;
    collection.add(item.toString());
    success.run();
    checkCollects();
  }

  protected List<String> getCollectionFiltered(@Nonnull UUID uuid) {
    List<String> targetStringList = Arrays.stream(target).map(Object::toString).toList();
    return collections.computeIfAbsent(uuid, key -> new ArrayList<>()).stream().filter(targetStringList::contains).collect(Collectors.toList());
  }

  protected List<String> getCollectionRaw(@Nonnull UUID uuid) {
    return collections.computeIfAbsent(uuid, key -> new ArrayList<>());
  }

  protected void checkCollects() {
    scoreboard.update();
    for (Player player : Bukkit.getOnlinePlayers()) {
      checkCollects(getCollectionFiltered(player.getUniqueId()));
    }
  }

  protected void checkCollects(@Nonnull List<String> collection) {
    if (collection.size() >= target.length)
      ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED);
  }

  @Override
  public void loadGameState(@Nonnull Document document) {
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
  public void writeGameState(@Nonnull Document document) {
    super.writeGameState(document);

    Document scores = document.getDocument("scores");
    collections.forEach((uuid, collection) -> {
      scores.set(uuid.toString(), collection);
    });
  }

  protected void setTarget(@Nonnull Object... target) {
    this.target = target;
  }

}
