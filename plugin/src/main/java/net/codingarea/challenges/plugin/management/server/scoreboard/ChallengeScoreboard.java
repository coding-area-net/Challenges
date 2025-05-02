package net.codingarea.challenges.plugin.management.server.scoreboard;

import lombok.Getter;
import lombok.ToString;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.common.misc.StringUtils;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public final class ChallengeScoreboard {

  private final Map<Player, Objective> objectives = new ConcurrentHashMap<>();
  private BiConsumer<ScoreboardInstance, Player> content = (scoreboard, player) -> {
  };

  public void setContent(@Nonnull BiConsumer<ScoreboardInstance, Player> content) {
    this.content = content;
  }

  public void applyHide(@Nonnull Player player) {
    unregister(objectives.remove(player));
  }

  public void update() {
    for (Player player : new LinkedList<>(Bukkit.getOnlinePlayers())) {
      update(player);
    }
  }

  public void update(@Nonnull Player player) {
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
      Objective objective1 = scoreboard.getObjective(name);
      if (objective1 != null) {
        unregister(objective1);
      }

      Objective objective = scoreboard.registerNewObjective(name, "dummy", String.valueOf(instance.getTitle()));
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

  @ToString
  public static final class ScoreboardInstance {

    private final String[] lines = new String[15];
    @Getter
    private String title = Message.forName("scoreboard-title").asString();
    private int linesIndex = 0;

    private ScoreboardInstance() {
    }

    @Nonnull
    public ScoreboardInstance addLine(@Nonnull String text) {
      if (linesIndex >= lines.length)
        throw new IllegalStateException("All lines are already used! (" + lines.length + ")");
      lines[linesIndex++] = text;
      return this;
    }

    @Nonnull
    public Collection<String> getLines() {
      List<String> list = new ArrayList<>();
      for (String line : lines) {
        if (line == null) continue;
        list.add(line);
      }
      return list;
    }

    @Nonnull
    public ScoreboardInstance setTitle(@Nonnull String title) {
      this.title = title;
      return this;
    }
  }

}
