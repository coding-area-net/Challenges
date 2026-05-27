package net.codingarea.challenges.plugin.management.cloud;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface CloudSupport {

  @NotNull
  String getColoredName(@NotNull Player player);

  @NotNull
  String getColoredName(@NotNull UUID uuid);

  boolean hasNameFor(@NotNull UUID uuid);

  void setIngame();

  void setLobby();

  void startNewService();

}
