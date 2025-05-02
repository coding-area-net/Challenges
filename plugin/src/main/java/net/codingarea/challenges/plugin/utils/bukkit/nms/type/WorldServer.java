package net.codingarea.challenges.plugin.utils.bukkit.nms.type;

import org.bukkit.World;

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
