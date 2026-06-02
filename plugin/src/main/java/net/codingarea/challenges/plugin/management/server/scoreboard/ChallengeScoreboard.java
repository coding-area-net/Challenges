package net.codingarea.challenges.plugin.management.server.scoreboard;

import lombok.Getter;
import lombok.ToString;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.common.misc.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public final class ChallengeScoreboard {

  private final Map<Player, Objective> objectives = new ConcurrentHashMap<>();
  private BiConsumer<ScoreboardInstance, Player> content = (scoreboard, player) -> {
  };

  public void setContent(@NotNull BiConsumer<ScoreboardInstance, Player> content) {
    this.content = content;
  }

  public void applyHide(@NotNull Player player) {
    unregister(objectives.remove(player));
  }

  public void update() {
    for (Player player : new LinkedList<>(Bukkit.getOnlinePlayers())) {
      update(player);
    }
  }

  public synchronized void update(@NotNull Player player) {
    // synchronized method lock: prevent race condition resulting in client error/crash: by sending the objective
    // with the same name at the same time twice as the name is constant (1d2b97b)
    // java.lang.IllegalArgumentException: An objective with the name 'xxx' already exists! (client-side)
    if (!isShown()) {
      Logger.warn("Tried to update scoreboard which is not shown");
      return;
    }

    try {
      if (objectives.containsKey(player)) {
        unregister(objectives.remove(player));
      }

      ScoreboardInstance instance = new ScoreboardInstance();
      content.accept(instance, player);

      Collection<String> lines = instance.getLines();
      if (lines.isEmpty()) {
        return;
      }

      Scoreboard scoreboard = player.getScoreboard();
      if (Bukkit.getScoreboardManager() == null) {
        return;
      }
      if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
        player.setScoreboard(scoreboard = Bukkit.getScoreboardManager().getNewScoreboard());
      }

      String name = String.valueOf(player.getUniqueId().hashCode());
      // Unregister any old objective existing
      Objective oldObjective = scoreboard.getObjective(name);
      if (oldObjective != null) {
        unregister(oldObjective);
      }

      Objective objective = registerDummyObjective(scoreboard, name, String.valueOf(instance.getTitle()));
      int score = lines.size();
      for (String line : lines) {
        if (line.isEmpty()) line = StringUtils.repeat(' ', score + 1);
        score--;
        objective.getScore(line).setScore(score);
      }

      objective.setDisplaySlot(DisplaySlot.SIDEBAR);
      objectives.put(player, objective);

    } catch (Exception ex) {
      Logger.error("Unable to update scoreboard for player '{}'", player.getName(), ex);
    }
  }

  public void show() {
    Challenges.getInstance().getScoreboardManager().setCurrentScoreboard(this);
  }

  public void hide() {
    if (Challenges.getInstance().getScoreboardManager().getCurrentScoreboard() != this) return;
    Challenges.getInstance().getScoreboardManager().setCurrentScoreboard(null);
  }

  public boolean isShown() {
    return Challenges.getInstance().getScoreboardManager().isShown(this);
  }

  private void unregister(@Nullable Objective objective) {
    try {
      if (objective == null) return;
      objective.unregister();
    } catch (Exception ex) {
      Logger.error("Unable to unregister objective " + objective.getName());
    }
  }

  @NotNull
  @SuppressWarnings("deprecation")
  private Objective registerDummyObjective(@NotNull Scoreboard scoreboard, @NotNull String name, @NotNull String displayName) {
    try {
      return scoreboard.registerNewObjective(name, Criteria.DUMMY, displayName);
    } catch (Error ignored) {
      // replacement not yet available in this version, use deprecated method
      return scoreboard.registerNewObjective(name, "dummy", displayName);
    }
  }

  @ToString
  public static final class ScoreboardInstance {

    private final String[] lines = new String[15];
    @Getter
    private String title = Message.forName("scoreboard-title").asString();
    private int linesIndex = 0;

    private ScoreboardInstance() {
    }

    @NotNull
    public ScoreboardInstance addLine(@NotNull String text) {
      if (linesIndex >= lines.length)
        throw new IllegalStateException("All lines are already used! (" + lines.length + ")");
      lines[linesIndex++] = text;
      return this;
    }

    @NotNull
    public Collection<String> getLines() {
      List<String> list = new ArrayList<>();
      for (String line : lines) {
        if (line == null) continue;
        list.add(line);
      }
      return list;
    }

    @NotNull
    public ScoreboardInstance setTitle(@NotNull String title) {
      this.title = title;
      return this;
    }
  }

}
