package net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_13;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.bukkit.nms.NMSProvider;
import net.codingarea.challenges.plugin.utils.bukkit.nms.NMSUtils;
import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import net.codingarea.challenges.plugin.utils.bukkit.nms.type.CraftPlayer;
import net.codingarea.challenges.plugin.utils.bukkit.nms.type.PacketBorder;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PacketBorder_1_13 extends PacketBorder {
    /**
     * Creates a new packet border.
     * Use this constructor if you override this implementation.
     * @param nmsClass      The NMS class
     * @param world         The world the border is in
     */
    protected PacketBorder_1_13(World world, Class<?> nmsClass) {
        super(
            nmsClass,
            world
        );
    }

    /**
     * Creates a new packet border.
     * @param world The world the border is in.
     */
    public PacketBorder_1_13(World world) {
        this(
            world,
            NMSUtils.getClass("WorldBorder")
        );
    }

    @Override
    protected Object createWorldBorder() {
        try {
            return ReflectionUtil.invokeConstructor(nmsClass);
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to create world border:", exception);
            return null;
        }
    }

    /**
     * Sets the world the border is in
     *
     * @param world The world
     */
    @Override
    protected void setWorld(World world) {
        try {
            ReflectionUtil.setFieldValue(worldBorder, "world", getWorldServer(world));
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to set world:", exception);
        }
    }

    @Override
    protected void setSizeField(double size) {
        try {
            ReflectionUtil.invokeMethod(worldBorder, "setSize", new Class[] {double.class}, new Object[]{size});
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to set border size:", exception);
        }
    }

    @Override
    protected void transitionSizeBetween(double oldSize, double newSize, long animationTime) {
        try {
            ReflectionUtil.invokeMethod(worldBorder, "transitionSizeBetween", new Class[]{double.class, double.class, long.class}, new Object[]{oldSize, newSize, animationTime});
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to set border size:", exception);
        }
    }

    @Override
    protected void setCenterField(double x, double z) {
        try {
            ReflectionUtil.invokeMethod(worldBorder, "setCenter", new Class[] {double.class, double.class}, new Object[]{x, z});
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to set border center:", exception);
        }
    }

    @Override
    protected void setWarningTimeField(int warningTime) {
        try {
            ReflectionUtil.invokeMethod(worldBorder, "setWarningTime", new Class[] {int.class}, new Object[]{warningTime});
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to set border warning time:", exception);
        }
    }

    @Override
    protected void setWarningDistanceField(int warningDistance) {
        try {
            ReflectionUtil.invokeMethod(worldBorder, "setWarningDistance", new Class[] {int.class}, new Object[]{warningDistance});
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to set border warning distance:", exception);
        }
    }

    @Override
    public void send(Player player, UpdateType updateType) {
        try {
            Object packet = updateType.createPacket(this);
            CraftPlayer craftPlayer = NMSProvider.createCraftPlayer(player);
            craftPlayer.getConnection().sendPacket(packet);
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to send update {} to player {}:", updateType.name(), player.getName(), exception);
        }
    }

}
