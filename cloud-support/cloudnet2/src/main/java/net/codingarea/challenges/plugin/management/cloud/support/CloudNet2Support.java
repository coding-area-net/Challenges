package net.codingarea.challenges.plugin.management.cloud.support;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.server.ServerState;
import net.codingarea.challenges.plugin.management.cloud.CloudSupport;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class CloudNet2Support implements CloudSupport {

  @NotNull
  @Override
  public String getColoredName(@NotNull Player player) {
    return getColoredName(player.getUniqueId());
  }

  @NotNull
  @Override
  public String getColoredName(@NotNull UUID uuid) {
    OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(uuid);
    PermissionGroup permissionGroup = offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
    String color = permissionGroup.getColor();
    return color.replace('&', '§') + offlinePlayer.getName();
  }

  @Override
  public boolean hasNameFor(@NotNull UUID uuid) {
    return CloudAPI.getInstance().getOfflinePlayer(uuid) != null;
  }

  @Override
  public void startNewService() {
    CloudServer.getInstance().changeToIngame();
  }

  @Override
  public void setIngame() {
    CloudServer.getInstance().setServerState(ServerState.INGAME);
  }

  @Override
  public void setLobby() {
    CloudServer.getInstance().setServerStateAndUpdate(ServerState.LOBBY);
  }

}
