package net.codingarea.challenges.plugin.utils.bukkit.nms.type;

import net.codingarea.challenges.plugin.utils.bukkit.nms.NMSProvider;
import org.bukkit.entity.Player;

/**
 * @author TobiasDeBruijn | <a href="https://github.com/TobiasDeBruijn">.<a href="..</a>
 * ">* @source https://github.com/TobiasDeBruijn/Bu</a>kkitReflectionUtil
 */
public abstract class CraftPlayer extends BukkitNMSClass<Player> {

  /**
   * @param player   The player to create the CraftPlayer for
   * @param nmsClass The NMS class
   */
  public CraftPlayer(Class<?> nmsClass, Player player) {
    super(nmsClass, player);
  }

  /**
   * @return The PlayerConnection of the Player as an object
   */
  public abstract Object getPlayerConnectionObject();

  /**
   * Get a connection to the Player
   *
   * @return The PlayerConnection
   */
  public PlayerConnection getConnection() {
    return NMSProvider.createPlayerConnection(this);
  }
}
