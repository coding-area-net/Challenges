package net.codingarea.challenges.plugin.management.server;

import lombok.Getter;
import lombok.Setter;
import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.config.FileDocument;
import net.anweisen.utilities.common.misc.FileUtils;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.bukkit.container.PlayerData;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WorldManager {

  private static final String customSeedWorldPrefix = "pregenerated_";
  private final boolean restartOnReset;
  @Getter
  private final boolean enableFreshReset;
  private final long customSeed;
  private final String levelName;
  private final String[] worlds;
  private final Map<UUID, PlayerData> playerData = new HashMap<>();
  @Getter
  private boolean shutdownBecauseOfReset = false;
  private boolean useCustomSeed;
  private WorldSettings settings = new WorldSettings();
  private World flatWorld;
  @Getter
  private boolean worldInUse;

  public WorldManager() {
    Document pluginConfig = Challenges.getInstance().getConfigDocument();
    restartOnReset = pluginConfig.getBoolean("restart-on-reset");
    enableFreshReset = pluginConfig.getBoolean("enable-fresh-reset");

    Document seedConfig = pluginConfig.getDocument("custom-seed");
    useCustomSeed = seedConfig.getBoolean("config");
    customSeed = seedConfig.getLong("seed");

    Document sessionConfig = Challenges.getInstance().getConfigManager().getSessionConfig();
    levelName = sessionConfig.getString("level-name", "world");
    worlds = new String[]{
      levelName,
      levelName + "_nether",
      levelName + "_the_end"
    };
  }

  public void load() {
    executeWorldResetIfNecessary();
  }

  public void enable() {
    loadExtraWorld();
  }

  public void prepareWorldReset(@Nullable CommandSender requestedBy) {
    prepareWorldReset(requestedBy, customSeed);
  }

  public void prepareWorldReset(@Nullable CommandSender requestedBy, @Nullable Long seed) {
    if (seed == null && useCustomSeed) seed = customSeed;
    if (seed != null) useCustomSeed = true;

    shutdownBecauseOfReset = true;
    ChallengeAPI.pauseTimer(false);

    // Stop all tasks to prevent them from overwriting configs
    Challenges.getInstance().getScheduler().stop();

    resetConfigs();

    String requester = requestedBy instanceof Player ? NameHelper.getName((Player) requestedBy) : "§4§lConsole";
    String kickMessage = Message.forName("server-reset").asString(requester);
    Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(kickMessage));

    if (seed != null) {
      generateCustomSeedWorlds(seed);
    }

    Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), this::stopServerNow, 3);

  }

  private void generateCustomSeedWorlds(long seed) {

    Logger.debug("Generating custom seed worlds with seed " + seed);
    for (String name : worlds) {

      World world = Bukkit.getWorld(name);
      if (world == null) {
        Logger.error("Could not find world {}", name);
        continue;
      }

      String newWorldName = customSeedWorldPrefix + name;
      File folder = new File(Bukkit.getWorldContainer(), newWorldName);
      if (folder.exists()) FileUtils.deleteWorldFolder(folder);

      WorldCreator creator = new WorldCreator(newWorldName).seed(seed).environment(world.getEnvironment());
      creator.createWorld();

      Logger.debug("Created custom seed world {}", newWorldName);

    }

  }

  private void resetConfigs() {

    FileDocument sessionConfig = Challenges.getInstance().getConfigManager().getSessionConfig();
    sessionConfig.clear();
    sessionConfig.set("reset", true);
    sessionConfig.set("seed-reset", useCustomSeed);
    if (!Bukkit.getWorlds().isEmpty()) {
      sessionConfig.set("level-name", ChallengeAPI.getGameWorld(Environment.NORMAL).getName());
    }
    sessionConfig.save();

    FileDocument gamestateConfig = Challenges.getInstance().getConfigManager().getGamestateConfig();
    gamestateConfig.clear();
    gamestateConfig.save();

  }

  private void loadExtraWorld() {
    if (!Challenges.getInstance().isReloaded())
      deleteWorld("challenges-extra");

    try {
      flatWorld = new WorldCreator("challenges-extra").type(WorldType.FLAT).generateStructures(false).createWorld();
      if (flatWorld == null) return;
      flatWorld.setSpawnFlags(false, false);
      disableGameRuleInFlatWorld("doMobSpawning");
      disableGameRuleInFlatWorld("doTraderSpawning");
      disableGameRuleInFlatWorld("doWeatherCycle");
      disableGameRuleInFlatWorld("doDaylightCycle");
      disableGameRuleInFlatWorld("disableRaids");
      disableGameRuleInFlatWorld("mobGriefing");

    } catch (Exception ex) {
      Logger.error("Could not load extra world!", ex);
      Logger.error("Probably the server version or server system was changed and the old world is not compatible with it");
      Logger.error("Please delete all worlds and try again!");
      return;
    }

    teleportPlayersOutOfExtraWorld();
  }

  private void teleportPlayersOutOfExtraWorld() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getWorld() != flatWorld) continue;

      Location location = player.getRespawnLocation();
      if (location == null) {
        World world = Bukkit.getWorld(levelName);
        if (world == null) {
          world = ChallengeAPI.getGameWorld(Environment.NORMAL);
        }
        location = world.getSpawnLocation();
      }

      player.teleport(location);
    }
  }

  @SuppressWarnings("unchecked")
  private void disableGameRuleInFlatWorld(@Nonnull String name) {
    GameRule<Boolean> gamerule = (GameRule<Boolean>) GameRule.getByName(name);
    if (gamerule == null) return;
    flatWorld.setGameRule(gamerule, false);
  }

  private void executeWorldResetIfNecessary() {
    if (Challenges.getInstance().getConfigManager().getSessionConfig().getBoolean("reset"))
      executeWorldReset();
  }

  public void executeWorldReset() {
    FileDocument sessionConfig = Challenges.getInstance().getConfigManager().getSessionConfig();
    boolean seedReset = sessionConfig.getBoolean("seed-reset");

    Logger.info("Deleting worlds..");

    for (String world : worlds) {
      deleteWorld(world);
      if (seedReset) {
        copyPreGeneratedWorld(world);
      } else {
        deletePreGeneratedWorld(world);
      }
    }

    for (String world : Challenges.getInstance().getGameWorldStorage().getCustomGeneratedGameWorlds()) {
      deleteWorld(world);
    }

    sessionConfig.set("reset", false);
    sessionConfig.set("seed-reset", false);
    sessionConfig.save();

  }

  private void deleteWorld(@Nonnull String name) {
    File folder = new File(Bukkit.getWorldContainer(), name);
    FileUtils.deleteWorldFolder(folder);
    Logger.info("Deleted world {}", name);
  }

  private void copyPreGeneratedWorld(@Nonnull String name) {
    File source = new File(Bukkit.getWorldContainer(), customSeedWorldPrefix + name);
    if (!source.exists() || !source.isDirectory()) {
      Logger.warn("Custom seed world '{}' does not exist!", name);
      return;
    }

    File target = new File(Bukkit.getWorldContainer(), name);
    try {
      copy(source, target);
      Logger.debug("Copied pre generated custom seed world {}", name);
    } catch (IOException ex) {
      Logger.error("Unable to copy pre generated custom seed world {}", name, ex);
    }
  }

  private void deletePreGeneratedWorld(@Nonnull String name) {
    File source = new File(Bukkit.getWorldContainer(), customSeedWorldPrefix + name);
    if (!source.exists() || !source.isDirectory()) return;

    FileUtils.deleteWorldFolder(source);
    Logger.debug("Deleted pre generated custom seed world {}", name);
  }

  public void copy(@Nonnull File source, @Nonnull File target) throws IOException {
    if (source.isDirectory()) {
      copyDirectory(source, target);
    } else {
      copyFile(source, target);
    }
  }

  private void copyDirectory(@Nonnull File source, @Nonnull File target) throws IOException {
    if (!target.exists()) {
      if (!target.mkdir()) {
        return;
      }
    }

    String[] list = source.list();
    if (list == null) return;
    for (String child : list) {
      if ("session.lock".equals(child)) continue;
      copy(new File(source, child), new File(target, child));
    }
  }

  private void copyFile(@Nonnull File source, @Nonnull File target) throws IOException {
    try (InputStream in = Files.newInputStream(source.toPath()); OutputStream out = Files.newOutputStream(target.toPath())) {
      byte[] buf = new byte[1024];
      int length;
      while ((length = in.read(buf)) > 0)
        out.write(buf, 0, length);
    }
  }

  private void stopServerNow() {
    if (!restartOnReset) {
      Bukkit.shutdown();
      return;
    }

    try {
      Bukkit.spigot().restart();
    } catch (NoSuchMethodError ex) {
      Bukkit.shutdown();
    }
  }

  public void setWorldInUse(boolean worldInUse) {
    this.worldInUse = worldInUse;
    if (worldInUse) {
      cachePlayerData();
    } else {
      settings = new WorldSettings();
      restorePlayerData();
    }
  }

  private void cachePlayerData() {
    Bukkit.getOnlinePlayers().forEach(this::cachePlayerData);
  }

  public void cachePlayerData(@Nonnull Player player) {
    playerData.put(player.getUniqueId(), new PlayerData(player));
  }

  public void restorePlayerData() {
    Bukkit.getOnlinePlayers().forEach(this::restorePlayerData);
  }

  public void restorePlayerData(@Nonnull Player player) {
    PlayerData data = playerData.remove(player.getUniqueId());
    if (data == null) return;
    data.apply(player);
  }

  public boolean hasPlayerData(@Nonnull Player player) {
    return playerData.containsKey(player.getUniqueId());
  }

  @Nonnull
  public World getExtraWorld() {
    return flatWorld;
  }

  @Nonnull
  public WorldSettings getSettings() {
    return settings;
  }

  @Setter
  @Getter
  public static class WorldSettings {

    private boolean placeBlocks = false;
    private boolean destroyBlocks = false;
    private boolean dropItems = false;
    private boolean pickupItems = false;

  }

}
