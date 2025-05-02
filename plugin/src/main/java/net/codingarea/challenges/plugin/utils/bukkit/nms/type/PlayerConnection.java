package net.codingarea.challenges.plugin.utils.bukkit.nms.type;

/**
 * @author TobiasDeBruijn | <a href="https://github.com/TobiasDeBruijn">.<a href="..</a>
 * ">* @source https://github.com/TobiasDeBruijn/Bu</a>kkitReflectionUtil
 */
public abstract class PlayerConnection extends AbstractNMSClass {

  protected final Object connection;

  public PlayerConnection(Class<?> nmsClass, Object connection) {
    super(nmsClass);
    this.connection = connection;
  }

  /**
   * Send a packet
   * <p>
   * The caller must guarantee the passed in object is a Packet
   *
   * @param packet The packet to send
   */
  public abstract void sendPacket(Object packet);
}
