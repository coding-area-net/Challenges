package net.codingarea.challenges.plugin.management.cloud;

import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.collection.WrappedException;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.management.cloud.support.CloudNet2Support;
import net.codingarea.challenges.plugin.management.cloud.support.CloudNet3Support;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CloudSupportManager implements Listener {

  private final Map<UUID, String> cachedColoredNames = new HashMap<>();
  private final boolean nameSupport;
  private final boolean resetToLobby;
  private final boolean setIngame;
  @Getter
  private final String type;
  private final boolean startNewService;
  private boolean startedNewService = false;
  private CloudSupport support;

  public CloudSupportManager() {
    Document config = Challenges.getInstance().getConfigDocument().getDocument("cloud-support");

    startNewService = config.getBoolean("start-new-service");
    nameSupport = config.getBoolean("name-rank-colors");
    resetToLobby = config.getBoolean("reset-to-lobby");
    setIngame = config.getBoolean("set-ingame");
    type = config.getString("type", "none");
    Logger.debug("Detected cloud support type '{}'", type);

    if (type.equals("none")) return;

    support = loadSupport(type);
    ChallengeAPI.registerScheduler(this);
    Challenges.getInstance().registerListener(this);

  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCommandsUpdate(@Nonnull PlayerCommandSendEvent event) {
    cachedColoredNames.remove(event.getPlayer().getUniqueId());
  }

  private CloudSupport loadSupport(@Nonnull String name) {
    switch (name) {
      default:
        return null;
      case "cloudnet":
      case "cloudnet3":
        return new CloudNet3Support();
      case "cloudnet2":
        return new CloudNet2Support();
    }
  }

  @Nonnull
  public String getColoredName(@Nonnull Player player) {
    if (support == null)
      throw new IllegalStateException("No support loaded! Check compatibility before use");
    if (cachedColoredNames.containsKey(player.getUniqueId()))
      return cachedColoredNames.get(player.getUniqueId());

    try {
      return cacheColoredName(player.getUniqueId(), support.getColoredName(player));
    } catch (NoClassDefFoundError ex) {
      Logger.error("Unable to get name with cloud support '{}', missing dependencies", type);
      throw new WrappedException(ex);
    }
  }

  @Nonnull
  public String getColoredName(@Nonnull UUID uuid) {
    if (support == null)
      throw new IllegalStateException("No support loaded! Check compatibility before use");
    if (cachedColoredNames.containsKey(uuid)) return cachedColoredNames.get(uuid);

    try {
      return cacheColoredName(uuid, support.getColoredName(uuid));
    } catch (NoClassDefFoundError ex) {
      Logger.error("Unable to get name with cloud support '{}', missing dependencies", type);
      throw new WrappedException(ex);
    }
  }

  @Nonnull
  private String cacheColoredName(@Nonnull UUID uuid, @Nonnull String name) {
    cachedColoredNames.put(uuid, name);
    return name;
  }

  public boolean hasNameFor(@Nonnull UUID uuid) {
    if (support == null) return false;

    try {
      return support.hasNameFor(uuid);
    } catch (NoClassDefFoundError ex) {
      Logger.error("Unable to check name with cloud support '{}', missing dependencies", type);
      return false;
    }
  }

  @TimerTask(status = TimerStatus.RUNNING, async = false)
  public void setIngameAndStartService() {
    if (!setIngame) return;
    if (support == null) return;

    try {
      support.setIngame();

      if (startNewService && !startedNewService)
        support.startNewService();
      startedNewService = true;
    } catch (NoClassDefFoundError ex) {
      Logger.error("Unable to set to ingame with cloud support '{}', missing dependencies", type);
    }
  }

  @TimerTask(status = TimerStatus.PAUSED, async = false)
  public void setLobby() {
    if (!resetToLobby || !startNewService) return;
    if (support == null) return;

    try {
      support.setLobby();
    } catch (NoClassDefFoundError ex) {
      Logger.error("Unable to set to lobby with cloud support '{}', missing dependencies", type);
    }
  }

  private boolean isEnabled() {
    return support != null;
  }

  public boolean isNameSupport() {
    return isEnabled() && nameSupport;
  }

  public boolean isResetToLobby() {
    return isEnabled() && resetToLobby;
  }

  public boolean isStartNewService() {
    return isEnabled() && startNewService;
  }

}
