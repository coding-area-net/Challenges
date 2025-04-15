package net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_17;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.bukkit.nms.type.CraftPlayer;
import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import org.bukkit.entity.Player;

/**
 * @author sehrschlechtYT | <a href="https://github.com/sehrschlechtYT">...</a>
 * @since 2.2.3
 */
public class CraftPlayer_1_17 extends CraftPlayer {

    /**
     * Create a new CraftPlayer
     *
     * @param player The Player
     */
    public CraftPlayer_1_17(Player player) throws ClassNotFoundException {
        super(ReflectionUtil.getBukkitClass("entity.CraftPlayer"), player);
    }

    @Override
    public Object get(Player player) {
        Object craftPlayer;
        try {
            craftPlayer = ReflectionUtil.invokeMethod(nmsClass, player, "getHandle");
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to get CraftPlayer:", exception);
            craftPlayer = null;
        }
        return craftPlayer;
    }

    @Override
    public Object getPlayerConnectionObject() {
        try {
            return ReflectionUtil.getObject(this.nmsObject, "b");
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to get player connection object:", exception);
            return null;
        }
    }
}
