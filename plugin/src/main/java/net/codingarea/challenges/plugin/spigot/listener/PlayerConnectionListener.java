package net.codingarea.challenges.plugin.spigot.listener;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.content.loader.UpdateLoader;
import net.codingarea.challenges.plugin.utils.misc.DatabaseHelper;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import net.codingarea.challenges.plugin.utils.misc.ParticleUtils;
import net.codingarea.commons.common.config.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerConnectionListener implements Listener {

  private final boolean messages;
  private final boolean timerPausedInfo;
  private final boolean startTimerOnJoin;
  private final boolean resetOnLastQuit;
  private final boolean pauseOnLastQuit;
  private final boolean restoreDefaultsOnLastQuit;

  public PlayerConnectionListener() {
    Document config = Challenges.getInstance().getConfigDocument();
    messages = config.getBoolean("join-quit-messages");
    timerPausedInfo = config.getBoolean("timer-is-paused-info");
    startTimerOnJoin = config.getBoolean("start-on-first-join");
    resetOnLastQuit = config.getBoolean("reset-on-last-leave");
    pauseOnLastQuit = config.getBoolean("pause-on-last-leave");
    restoreDefaultsOnLastQuit = config.getBoolean("restore-defaults-on-last-leave");
  }

//  @EventHandler(priority = EventPriority.LOWEST)
//  public void onLogin(@NotNull PlayerLoginEvent event) {
//    // DISCUSSION: really necessary? not implemented by default - extract?
//    TranslationManager i18n = Challenges.getInstance().getTranslationManager();
//    if (!i18n.isLanguageProviderInitialized()) return;
//
//    LanguageProvider provider = i18n.getLanguageProvider();
//    if (provider.isDefaultBehaviour() || !provider.isUserSpecific()) return;
//
//    Locale playerLanguage = provider.getPlayerLanguage(event.getPlayer());
//    if (i18n.isLanguageCached(playerLanguage)) return;
//
//    LanguageLoader loader = Challenges.getInstance().getLoaderRegistry().getFirstLoaderByClass(LanguageLoader.class).orElse(null);
//    if (loader == null) return;
//    Challenges.getInstance().runAsync(() -> loader.populateLanguageFromFile(playerLanguage));
//  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onJoin(@NotNull PlayerJoinEvent event) {
    Player player = event.getPlayer();

    player.getLocation().getChunk().load(true);
    ParticleUtils.spawnParticleCylinder(Challenges.getInstance(), player.getLocation(),
      MinecraftNameWrapper.ENTITY_EFFECT, 17, 1, 2);
    Challenges.getInstance().getScoreboardManager().handleJoin(player);

    if (Challenges.getInstance().isFirstInstall() && !player.hasPermission("challenges.gui")) {
      MessageKey.of("not-op").send(player, Prefix.CHALLENGES);
    }

    if (player.hasPermission("challenges.gui")) {
      if (Challenges.getInstance().isFirstInstall()) {
        player.sendMessage("");
        player.sendMessage(Prefix.CHALLENGES + "§7Thanks for downloading §e§lChallenges§7!");
        player.sendMessage(Prefix.CHALLENGES + "§7You can change the language in the settings or with /setlang [language]");
        player.sendMessage(Prefix.CHALLENGES + "§7For more join our discord §ediscord.gg/74Ay5zF");
      }

      if (timerPausedInfo && !startTimerOnJoin && ChallengeAPI.isPaused()) {
        player.sendMessage("");
        MessageKey.of("timer-paused-message").send(player, Prefix.CHALLENGES);
      }
    }

    if (Challenges.getInstance().getStatsManager().isNoStatsAfterCheating() && Challenges.getInstance().getServerManager().hasCheated()) {
      player.sendMessage("");
      MessageKey.of("cheats-already-detected").send(player, Prefix.CHALLENGES);
    }


    if (startTimerOnJoin) {
      player.sendMessage("");
      ChallengeAPI.resumeTimer();
    }

    if (player.hasPermission("challenges.gui")) {
      if (!UpdateLoader.isNewestConfigVersion()) {
        player.sendMessage("");
        MessageKey.of("deprecated-config-version").send(player, Prefix.CHALLENGES, UpdateLoader.getDefaultConfigVersion().format(), UpdateLoader.getCurrentConfigVersion().format());
      }

      List<String> missingConfigSettings = Challenges.getInstance().getConfigManager().getMissingConfigSettings();
      if (!missingConfigSettings.isEmpty()) {
        player.sendMessage("");
        String separator = Message.forName("missing-config-settings-separator").asString();
        MessageKey.of("missing-config-settings").send(player, Prefix.CHALLENGES, String.join(separator, missingConfigSettings));
      } else if (!UpdateLoader.isNewestConfigVersion()) {
        player.sendMessage("");
        MessageKey.of("no-missing-config-settings").send(player, Prefix.CHALLENGES, UpdateLoader.getDefaultConfigVersion().format());
      }
      if (!UpdateLoader.isNewestPluginVersion()) {
        player.sendMessage("");
        MessageKey.of("deprecated-plugin-version").send(player, Prefix.CHALLENGES, "spigotmc.org/resources/" + UpdateLoader.RESOURCE_ID);
      }
    }


    if (messages) {
      event.setJoinMessage(null);
      player.sendMessage("");
      MessageKey.of("join-message").broadcast(Prefix.CHALLENGES, event.getPlayer()); // TODO name color
    }

    if (Challenges.getInstance().getDatabaseManager().isConnected()) {
      Challenges.getInstance().runAsync(() -> DatabaseHelper.savePlayerData(player));
    }

  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onQuit(@NotNull PlayerQuitEvent event) {

    try {
      Player player = event.getPlayer();
      Challenges.getInstance().getScoreboardManager().handleQuit(player);
      DatabaseHelper.clearCache(event.getPlayer().getUniqueId());

      if (Challenges.getInstance().getWorldManager().isShutdownBecauseOfReset()) {
        event.setQuitMessage(null);
      } else if (messages) {
        event.setQuitMessage(null);
        MessageKey.of("quit-message").broadcast(Prefix.CHALLENGES, NameHelper.getName(event.getPlayer()));
      }
    } catch (Exception exception) {
      Challenges.getInstance().getILogger().error("Error while handling disconnect", exception);
    }

    if (Bukkit.getOnlinePlayers().size() <= 1) {

      if (!Challenges.getInstance().getWorldManager().isShutdownBecauseOfReset()) {
        if (resetOnLastQuit && !ChallengeAPI.isFresh()) {
          Challenges.getInstance().getWorldManager().prepareWorldReset(Bukkit.getConsoleSender());
          return;
        } else if (pauseOnLastQuit && ChallengeAPI.isStarted()) {
          ChallengeAPI.pauseTimer();
        }
      }

      if (restoreDefaultsOnLastQuit) {
        Challenges.getInstance().getChallengeManager().restoreDefaults();
      }
    }

  }

}
