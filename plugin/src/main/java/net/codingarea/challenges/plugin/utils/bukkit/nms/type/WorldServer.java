package net.codingarea.challenges.plugin.utils.bukkit.nms.type;

import org.bukkit.World;

/**
 * @author sehrschlechtYT | <a href="https://github.com/sehrschlechtYT">...</a>
 * @since 2.2.3
 */
public abstract class WorldServer extends BukkitNMSClass<World> {
	/**
	 * @param object   The instance of the specified bukkit type
	 * @param nmsClass The NMS class
	 */
	protected WorldServer(World object, Class<?> nmsClass) {
		super(nmsClass, object);
	}

	public Object getWorldServerObject() {
		return nmsObject;
	}
}