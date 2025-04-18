package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public abstract class KillMobsGoal extends SettingGoal {

  protected static List<EntityType> entitiesKilled;

  protected final List<EntityType> entitiesToKill;

  public KillMobsGoal(List<EntityType> entitiesKilled) {
    this.entitiesToKill = entitiesKilled;
    resetEntitiesToKill();
  }

  public abstract Message getBossbarMessage();

  private void resetEntitiesToKill() {
    entitiesKilled = new LinkedList<>();
  }

  @Override
  public void getWinnersOnEnd(@NotNull List<Player> winners) {

  }

  @Override
  protected void onEnable() {
    bossbar.setContent((bar, player) -> {
      float i = 1 - ((float) getEntitiesLeftToKill().size() / (float) entitiesToKill.size());
      bar.setProgress(i);
      bar.setColor(BarColor.GREEN);
      bar.setTitle(getBossbarMessage().asString(getEntitiesKilled().size(), entitiesToKill.size()));
    });
    bossbar.show();
  }

  @Override
  protected void onDisable() {
    bossbar.hide();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDeath(@Nonnull EntityDeathEvent event) {
    if (!shouldExecuteEffect()) return;
    if (event.getEntityType() != EntityType.WITHER && event.getEntityType() != EntityType.ENDER_DRAGON && event.getEntityType() != EntityType.ELDER_GUARDIAN) {
      if (event.getEntity().getKiller() == null) return;
    }
    if (entitiesKilled.contains(event.getEntityType())) return;
    entitiesKilled.add(event.getEntityType());
    if (entitiesToKill.contains(event.getEntityType())) {
      Message.forName("mob-kill").broadcast(Prefix.CHALLENGES, event.getEntityType(), getEntitiesKilled().size(), entitiesToKill.size());
      bossbar.update();
      if (!getEntitiesLeftToKill().isEmpty()) return;
      resetEntitiesToKill();
      ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED);
    }
  }

  @Override
  public void writeGameState(@Nonnull Document document) {
    super.writeGameState(document);

    document.set("entities", entitiesKilled);
  }

  @Override
  public void loadGameState(@Nonnull Document document) {
    super.loadGameState(document);

    entitiesKilled = document.getEnumList("entities", EntityType.class);
  }

  public List<EntityType> getEntitiesKilled() {
    LinkedList<EntityType> entityTypes = new LinkedList<>(entitiesKilled);
    entityTypes.removeIf(type -> !entitiesToKill.contains(type));
    return entityTypes;
  }

  public List<EntityType> getEntitiesLeftToKill() {
    LinkedList<EntityType> entityTypes = new LinkedList<>(entitiesToKill);
    entityTypes.removeAll(entitiesKilled);
    return entityTypes;
  }

}
