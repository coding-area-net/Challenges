package net.codingarea.challenges.plugin.management.team;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class PlayerTeam implements Team, TemporaryTeam {

  private final Player player;

  @Override
  public UUID getUniqueId() {
    return player.getUniqueId();
  }

  @Override
  public String getDisplayName() {
    return player.getDisplayName();
  }
}
