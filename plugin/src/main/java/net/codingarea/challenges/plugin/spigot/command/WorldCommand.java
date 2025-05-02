package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.implementation.setting.PositionSetting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class WorldCommand implements PlayerCommand, TabCompleter {

  @Override
  public void onCommand(@NotNull Player player, @NotNull String[] args) throws Exception {

    if (args.length < 1) {
      Message.forName("syntax").send(player, Prefix.CHALLENGES, "world <world>");
      return;
    }

    String worldName = args[0];

    Environment environment = PositionSetting.getWorldEnvironment(worldName);

    boolean targetIsVoidMap = worldName.equalsIgnoreCase("void");
    if (environment == null && !targetIsVoidMap) {
      Message.forName("syntax").send(player, Prefix.CHALLENGES, "world <world>");
      return;
    }

    World world = targetIsVoidMap ? Challenges.getInstance().getGameWorldStorage().getOrCreateVoidWorld() : ChallengeAPI.getGameWorld(environment);
    if (world == null) {
      Message.forName("syntax").send(player, Prefix.CHALLENGES, "world <world>");
      return;
    }

    Location location = getSpawn(world, player);

    Message.forName("command-world-teleport").send(player, Prefix.CHALLENGES, targetIsVoidMap ? "Void" : getWorldName(location));
    player.teleport(location);
  }

  public Location getSpawn(@Nonnull World world, @Nonnull Player player) {
    Location location = world.getSpawnLocation();
    Location bedSpawnLocation = player.getBedSpawnLocation();
    if (bedSpawnLocation != null && bedSpawnLocation.getWorld() == world) {
      location = bedSpawnLocation;
    } else if (world.getEnvironment() == Environment.THE_END) {
      location = world.getHighestBlockAt(0, 0).getLocation().add(0.5, 1, 0.5);
    }
    return location;
  }

  public String getWorldName(@Nonnull Location location) {
    if (location.getWorld() == null) return "?";
    switch (location.getWorld().getEnvironment()) {
      default:
        return "Overworld";
      case NETHER:
        return "Nether";
      case THE_END:
        return "End";
    }
  }

  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String alias, @NotNull String[] args) {
    return Utils.filterRecommendations(args[0], "Overworld", "Nether", "End", "Void");
  }

}
