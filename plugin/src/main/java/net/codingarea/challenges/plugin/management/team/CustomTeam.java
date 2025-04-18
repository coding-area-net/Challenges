package net.codingarea.challenges.plugin.management.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CustomTeam implements Team {

  private final UUID uniqueId;
  private final String displayName;

  public CustomTeam(String displayName) {
    this(UUID.randomUUID(), displayName);
  }

}
