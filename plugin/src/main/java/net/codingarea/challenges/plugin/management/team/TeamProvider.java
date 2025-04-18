package net.codingarea.challenges.plugin.management.team;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamProvider {

  private final ConcurrentHashMap<UUID, Team> customTeams = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<UUID, UUID> userTeams = new ConcurrentHashMap<>();

  public TeamProvider() {

  }

  @NotNull
  public Team getTeam(Player player) {
    UUID uuid = userTeams.get(player.getUniqueId());
    if (uuid != null) {
      Team team = customTeams.get(uuid);
      if (team != null) {
        return team;
      }
    }
    return new PlayerTeam(player);
  }

  public void setTeam(Player player, UUID teamId) {
    Team team = customTeams.get(teamId);
    if (team instanceof TemporaryTeam) {
      userTeams.remove(player.getUniqueId());
      return;
    }
    userTeams.put(player.getUniqueId(), teamId);
  }

  public void registerTeam(Team team) {
    if (team instanceof TemporaryTeam) {
      Logger.error("Attempted to register a temporary team as a team!");
      return;
    }
    customTeams.put(team.getUniqueId(), team);
  }

  public void unregisterTeam(Team team) {
    if (team instanceof TemporaryTeam) {
      Logger.error("Attempted to unregister a temporary team!");
      return;
    }
    customTeams.remove(team.getUniqueId());
  }
}
