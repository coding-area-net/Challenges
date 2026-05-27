package net.codingarea.challenges.plugin.management.cloud.support;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.IPermissionManagement;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.ext.bridge.BridgeHelper;
import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper;
import net.codingarea.challenges.plugin.management.cloud.CloudSupport;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class CloudNet3Support implements CloudSupport {

  @NotNull
  @Override
  public String getColoredName(@NotNull Player player) {
    return getColoredName(player.getUniqueId());
  }

  @NotNull
  @Override
  public String getColoredName(@NotNull UUID uuid) {
    IPermissionManagement management = CloudNetDriver.getInstance().getPermissionManagement();
    IPermissionUser user = management.getUser(uuid);
    if (user == null) return "Unknown CloudPlayer";
    IPermissionGroup group = management.getHighestPermissionGroup(user);
    String color = group.getColor();
    return color.replace('&', '§') + user.getName();
  }

  @Override
  public boolean hasNameFor(@NotNull UUID uuid) {
    return CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid) != null;
  }

  @Override
  public void startNewService() {
    BukkitCloudNetHelper.changeToIngame();
  }

  @Override
  public void setIngame() {
    BukkitCloudNetHelper.setState("INGAME");
    BridgeHelper.updateServiceInfo();
  }

  @Override
  public void setLobby() {
    BukkitCloudNetHelper.setState("LOBBY");
    BridgeHelper.updateServiceInfo();
  }

}
