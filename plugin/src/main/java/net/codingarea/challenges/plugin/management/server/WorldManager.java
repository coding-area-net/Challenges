package net.codingarea.challenges.plugin.management.server;

import lombok.Getter;
import lombok.Setter;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.utils.bukkit.container.PlayerData;
import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.common.collection.IRandom;
import net.codingarea.commons.common.collection.pair.Tuple;
import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.config.FileDocument;
import net.codingarea.commons.common.misc.FileUtils;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class WorldManager {

  private final boolean restartOnReset;
  @Getter
  private final boolean enableFreshReset;
  private final long configCustomSeed;
  private final boolean configUseCustomSeed;
  private final String levelName;
  private final String[] worlds;
  private final Map<UUID, PlayerData> playerData = new HashMap<>();
  @Getter
  private boolean shutdownBecauseOfReset = false;
  private WorldSettings settings = new WorldSettings();
  private World flatWorld;
  @Getter
  private boolean worldInUse;

  public WorldManager() {
    Document pluginConfig = Challenges.getInstance().getConfigDocument();
    restartOnReset = pluginConfig.getBoolean("restart-on-reset");
    enableFreshReset = pluginConfig.getBoolean("enable-fresh-reset");

    Document seedConfig = pluginConfig.getDocument("custom-seed");
    configUseCustomSeed = seedConfig.getBoolean("config");
    configCustomSeed = seedConfig.getLong("seed");

    Document sessionConfig = Challenges.getInstance().getConfigManager().getSessionConfig();
    levelName = sessionConfig.getString("level-name", "world");
    worlds = new String[]{
      levelName,
      levelName + "_nether", // TODO
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
    prepareWorldReset(requestedBy, configCustomSeed);
  }

  public void prepareWorldReset(@Nullable CommandSender requestedBy, @Nullable Long seed) {
    if (seed == null && configUseCustomSeed) seed = configCustomSeed;

    shutdownBecauseOfReset = true;
    ChallengeAPI.pauseTimer(false);

    // Stop all tasks to prevent them from overwriting configs
    Challenges.getInstance().getScheduler().stop();

    resetConfigs(seed);

    Object requester = requestedBy instanceof Player ? requestedBy : "<dark_red><b>Console<dark";
    MessageKey disconnectKey = MessageKey.of(restartOnReset ? "server-reset.restart" : "server-reset.stop");
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.kick(disconnectKey.asComponent(player, requester));
    }

    Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), this::stopServerNow, 3);
  }

  private void resetConfigs(@Nullable Long seed) {
    FileDocument sessionConfig = Challenges.getInstance().getConfigManager().getSessionConfig();
    sessionConfig.clear();
    sessionConfig.set("reset", true);
    sessionConfig.set("provided-custom-seed", seed != null);
    if (seed != null) {
      sessionConfig.set("custom-seed", seed);
    }

    World world = ChallengeAPI.getGameWorld(Environment.NORMAL);
    if (world != null) {
      sessionConfig.set("level-name", world.getName());
      sessionConfig.set("old-seed", world.getSeed());
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
      applyGameRuleInFlatWorld(MinecraftNameWrapper.getDisableRaidsGameRulePair());
      disableGameRulesInFlatWorld(MinecraftNameWrapper.MOB_SPAWNING, MinecraftNameWrapper.WANDERING_TRADERS,
        MinecraftNameWrapper.WEATHER_CYCLE, MinecraftNameWrapper.DAYLIGHT_CYCLE, GameRule.MOB_GRIEFING);
    } catch (Throwable ex) {
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

      Location location = getRespawnLocation(player);
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

  @Nullable
  @SuppressWarnings("deprecation")
  private Location getRespawnLocation(@NotNull Player player) {
    try {
      return player.getRespawnLocation(); // introduced in 1.20.4
    } catch (Error e) {
      return player.getBedSpawnLocation(); // deprecated since 1.20.4
    }
  }

  private void applyGameRuleInFlatWorld(Tuple<GameRule<Boolean>, Boolean> gameRulePair) {
    flatWorld.setGameRule(gameRulePair.getFirst(), gameRulePair.getSecond());
  }

  @SafeVarargs
  private void disableGameRulesInFlatWorld(@NotNull GameRule<Boolean>... gameRules) {
    for (GameRule<Boolean> gameRule : gameRules) {
      flatWorld.setGameRule(gameRule, false);
    }
  }

  private void executeWorldResetIfNecessary() {
    if (Challenges.getInstance().getConfigManager().getSessionConfig().getBoolean("reset"))
      executeWorldReset();
  }

  public void executeWorldReset() {
    Logger.info("Deleting worlds..");

    for (String world : worlds) {
      deleteWorld(world);
    }

    long newSeed = getCustomSeedOrRandom();
    String newSeedString = String.valueOf(newSeed);
    replaceServerPropertiesSeed(newSeedString);

    FileDocument sessionConfig = Challenges.getInstance().getConfigManager().getSessionConfig();
    if (sessionConfig.contains("old-seed")) {
      long oldSeed = sessionConfig.getLong("old-seed");
      injectSeedViaReflection(String.valueOf(oldSeed), newSeedString);
    } else {
      // this should never happen, probably old or corrupt session.json
      Logger.warn("Could not find old level-seed in session config for reflection injection!");
    }

    for (String world : Challenges.getInstance().getGameWorldStorage().getCustomGeneratedGameWorlds()) {
      deleteWorld(world);
    }

    sessionConfig.set("reset", false);
    sessionConfig.set("provided-custom-seed", false);
    sessionConfig.save();
  }

  private long getCustomSeedOrRandom() {
    FileDocument sessionConfig = Challenges.getInstance().getConfigManager().getSessionConfig();
    boolean providedCustomSeed = sessionConfig.getBoolean("provided-custom-seed");
    long customSeed = sessionConfig.getLong("custom-seed");

    if (providedCustomSeed) return customSeed;
    if (configUseCustomSeed) return configCustomSeed;
    return IRandom.secure().nextLong();
  }

  private void replaceServerPropertiesSeed(String newSeed) {
    // before the world structure overhaul we pre generated a custom seed world and copied it. injecting the level-seed
    // into the server.properties works more seamlessly. versions after the update seem to have already read the seed
    // before we replace it, requiring an injection via reflection and rendering this function ineffective
    File serverPropertiesFile = new File("server.properties");
    if (!serverPropertiesFile.exists()) {
      Logger.warn("Unable to find server.properties at {} for seed '{}' injection", serverPropertiesFile.getAbsolutePath(), newSeed);
      return;
    }

    FileDocument properties = FileDocument.readPropertiesFile(serverPropertiesFile);
    properties.set("level-seed", newSeed);
    properties.save();
  }

  private void injectSeedViaReflection(String oldSeed, String newSeed) {
    // before the world structure overhaul it was enough to just delete world data to trigger a regeneration
    // with a new random seed. but after the overhaul the seed seemed to be already be read when onLoad injection
    // takes place, therefore requiring the seed to be replaced in memory when sticking to world deletion on startup
    // for easy plug-and-play. this also removes the need to pre-generate custom seed levels as the seed gets replaced.
    try {
      Object craftServer = Bukkit.getServer();
      Object dedicatedServer = ReflectionUtil.invokeMethod(craftServer, "getServer");
      if (dedicatedServer != null) {
        injectSeedInto(dedicatedServer, oldSeed, newSeed, 6, new HashSet<>());
      }
    } catch (Exception ex) {
      Logger.warn("Failed to inject seed via reflection", ex);
    }
  }

  private void injectSeedInto(Object obj, String oldSeed, String newSeed, int depth, Set<Integer> visited) {
    if (obj == null || depth == 0) return;
    if (!visited.add(System.identityHashCode(obj))) return;
    // defend preemptively against obfuscation and impl changes: replace fields containing old seed with new one
    // alternative: replacing correct field directly via reflection would be riskier and less future-proof
    Class<?> clazz = obj.getClass();
    while (clazz != null && clazz != Object.class) {
      for (Field field : clazz.getDeclaredFields()) {
        if (Modifier.isStatic(field.getModifiers())) continue;
        try {
          field.setAccessible(true);
          Object value = field.get(obj);
          if (value == null) continue;

          if (value instanceof String) {
            if (oldSeed.equals(value)) {
              field.set(obj, newSeed);
              Logger.debug("Injected string seed in " + clazz.getSimpleName() + "." + field.getName());
            }
          } else if (field.getType() == long.class) {
            try {
              long oldL = Long.parseLong(oldSeed);
              long newL = Long.parseLong(newSeed);
              if ((long) value == oldL) {
                field.setLong(obj, newL);
                Logger.debug("Injected long seed in " + clazz.getSimpleName() + "." + field.getName());
              }
            } catch (NumberFormatException ignored) {
            }
          } else if (value instanceof OptionalLong) {
            try {
              long oldL = Long.parseLong(oldSeed);
              long newL = Long.parseLong(newSeed);
              OptionalLong opt = (OptionalLong) value;
              if (opt.isPresent() && opt.getAsLong() == oldL) {
                field.set(obj, OptionalLong.of(newL));
                Logger.debug("Injected OptionalLong seed in " + clazz.getSimpleName() + "." + field.getName());
              }
            } catch (NumberFormatException ignored) {
            }
          } else if (value instanceof Properties) {
            Properties p = (Properties) value;
            if (oldSeed.equals(p.getProperty("level-seed"))) {
              p.setProperty("level-seed", newSeed);
              Logger.debug("Injected seed in java.util.Properties");
            }
          } else {
            String name = value.getClass().getName();
            if (name.startsWith("net.minecraft") || name.startsWith("org.bukkit") || name.startsWith("com.destroystokyo")) {
              injectSeedInto(value, oldSeed, newSeed, depth - 1, visited);
            } else if (value instanceof Iterable) {
              for (Object item : (Iterable<?>) value) {
                injectSeedInto(item, oldSeed, newSeed, depth - 1, visited);
              }
            }
          }
        } catch (Exception ignored) {
        }
      }
      clazz = clazz.getSuperclass();
    }
  }

  private void deleteWorld(@NotNull String name) {
    File folder = new File(Bukkit.getWorldContainer(), name);
    FileUtils.deleteWorldFolder(folder);
    Logger.info("Deleted world {} (at: {}, world container: {})", name, folder, Bukkit.getWorldContainer());
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

  public void cachePlayerData(@NotNull Player player) {
    playerData.put(player.getUniqueId(), new PlayerData(player));
  }

  public void restorePlayerData() {
    Bukkit.getOnlinePlayers().forEach(this::restorePlayerData);
  }

  public void restorePlayerData(@NotNull Player player) {
    PlayerData data = playerData.remove(player.getUniqueId());
    if (data == null) return;
    data.apply(player);
  }

  public boolean hasPlayerData(@NotNull Player player) {
    return playerData.containsKey(player.getUniqueId());
  }

  @NotNull
  public World getExtraWorld() {
    return flatWorld;
  }

  @NotNull
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
