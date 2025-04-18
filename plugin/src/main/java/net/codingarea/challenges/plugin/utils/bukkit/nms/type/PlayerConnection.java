package net.codingarea.challenges.plugin.utils.bukkit.nms.type;

/**
 * @author TobiasDeBruijn | https://github.com/TobiasDeBruijn
 * @source https://github.com/TobiasDeBruijn/BukkitReflectionUtil
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
