package net.codingarea.challenges.plugin.management.cloud;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface CloudSupport {

  @Nonnull
  String getColoredName(@Nonnull Player player);

  @Nonnull
  String getColoredName(@Nonnull UUID uuid);

  boolean hasNameFor(@Nonnull UUID uuid);

  void setIngame();

  void setLobby();

  void startNewService();

}
