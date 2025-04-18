package net.codingarea.challenges.plugin.utils.bukkit.nms;

import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_13.*;
import net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_17.BorderPacketFactory_1_17;
import net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_17.CraftPlayer_1_17;
import net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_17.PacketBorder_1_17;
import net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_18.PacketBorder_1_18;
import net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_18.PlayerConnection_1_18;
import net.codingarea.challenges.plugin.utils.bukkit.nms.type.*;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class NMSProvider {

  private final static int majorVersion;
  /**
   * -- GETTER --
   *
   * @return A border packet factory
   */
  @Getter
  private static final BorderPacketFactory borderPacketFactory;

  static {
    majorVersion = ReflectionUtil.getMajorVersion();
    if (versionIsAtLeast(17)) {
      borderPacketFactory = new BorderPacketFactory_1_17();
    } else if (versionIsAtLeast(13)) {
      borderPacketFactory = new BorderPacketFactory_1_13();
    } else {
      throw new IllegalStateException("Could not find a BorderPacketFactory implementation for version " + getFormattedVersion());
    }
  }

  /**
   * Creates a world server for the given world
   *
   * @param world The world
   * @return The world server
   * @throws IllegalStateException If no implementation was found for the current version
   */
  public static WorldServer createWorldServer(World world) {
    try {
      if (versionIsAtLeast(13)) {
        return new WorldServer_1_13(world);
      }
    } catch (ClassNotFoundException exception) {
      Challenges.getInstance().getLogger().error("Failed to create WorldServer instance for version {}:", majorVersion, exception);
    }
    throw new IllegalStateException("Could not find a WorldServer implementation for version " + getFormattedVersion());
  }

  /**
   * Creates a craft player for the given player
   *
   * @param player The player
   * @return The craft player
   * @throws IllegalStateException If no implementation was found for the current version
   */
  public static CraftPlayer createCraftPlayer(Player player) {
    try {
      if (versionIsAtLeast(17)) {
        return new CraftPlayer_1_17(player);
      } else if (versionIsAtLeast(13)) {
        return new CraftPlayer_1_13(player);
      }
    } catch (ClassNotFoundException exception) {
      Challenges.getInstance().getLogger().error("Failed to create CraftPlayer instance for version {}:", majorVersion, exception);
    }
    throw new IllegalStateException("Could not find a CraftServer implementation for version " + getFormattedVersion());
  }

  /**
   * Creates a player connection for the given CraftPlayer
   *
   * @param player The CraftPlayer
   * @return The player connection
   * @throws IllegalStateException If no implementation was found for the current version
   */
  public static PlayerConnection createPlayerConnection(CraftPlayer player) {
    try {
      if (versionIsAtLeast(18)) {
        return new PlayerConnection_1_18(player.getPlayerConnectionObject());
      } else if (versionIsAtLeast(13)) {
        return new PlayerConnection_1_13(player.getPlayerConnectionObject());
      }
    } catch (ClassNotFoundException exception) {
      Challenges.getInstance().getLogger().error("Failed to create PlayerConnection instance for version {}:", majorVersion, exception);
    }

    throw new IllegalStateException("Could not find a PlayerConnection implementation for version " + getFormattedVersion());
  }

  /**
   * Creates a packet border for the given world
   *
   * @param world The world
   * @return The packet border
   * @throws IllegalStateException If no implementation was found for the current version
   */
  public static PacketBorder createPacketBorder(World world) {
    if (versionIsAtLeast(18)) {
      return new PacketBorder_1_18(world);
    } else if (versionIsAtLeast(17)) {
      return new PacketBorder_1_17(world);
    } else if (versionIsAtLeast(13)) {
      return new PacketBorder_1_13(world);
    }
    throw new IllegalStateException("Could not find a PacketBorder implementation for version " + getFormattedVersion());
  }

  private static boolean versionIsAtLeast(int majorVersion) {
    return NMSProvider.majorVersion >= majorVersion;
  }

  private static String getFormattedVersion() {
    return MinecraftVersion.current().format();
  }

}
