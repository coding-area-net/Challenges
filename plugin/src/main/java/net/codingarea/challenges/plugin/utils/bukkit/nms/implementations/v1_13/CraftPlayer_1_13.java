package net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_13;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import net.codingarea.challenges.plugin.utils.bukkit.nms.type.CraftPlayer;
import org.bukkit.entity.Player;

public class CraftPlayer_1_13 extends CraftPlayer {
    /**
     * @param player   The player to create the CraftPlayer for
     */
    public CraftPlayer_1_13(Player player) throws ClassNotFoundException {
        super(ReflectionUtil.getBukkitClass("entity.CraftPlayer"), player);
    }

    /**
     * Creates an NMS object of the specified player
     *
     * @param player The player to create the CraftPlayer for
     * @return The NMS object
     */
    @Override
    public Object get(Player player) {
        Object craftPlayer;
        try {
            craftPlayer = ReflectionUtil.invokeMethod(nmsClass, player, "getHandle");
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to create CraftPlayer:", exception);
            craftPlayer = null;
        }
        return craftPlayer;
    }

    /**
     * @return The PlayerConnection of the Player as an object
     */
    @Override
    public Object getPlayerConnectionObject() {
        try {
            return ReflectionUtil.getObject(this.nmsObject, "playerConnection");
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to get the playerConnection object:", exception);
            return null;
        }
    }
}
