package net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_18;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_17.PacketBorder_1_17;
import org.bukkit.World;

public class PacketBorder_1_18 extends PacketBorder_1_17 {

    public PacketBorder_1_18(World world) {
        super(world);
    }

    @Override
    protected void setCenterField(double x, double z) {
        try {
            ReflectionUtil.invokeMethod(worldBorder, "c", new Class[] {double.class, double.class}, new Object[]{x, z});
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to set center:", exception);
        }
    }

    @Override
    protected void setSizeField(double size) {
        try {
            ReflectionUtil.invokeMethod(worldBorder, "a", new Class[] {double.class}, new Object[]{size});
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to set size:", exception);
        }
    }

    @Override
    protected void setWarningDistanceField(int warningDistance) {
        try {
            ReflectionUtil.invokeMethod(worldBorder, "c", new Class[] {int.class}, new Object[]{warningDistance});
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to set warning distance:", exception);
        }
    }

    @Override
    protected void setWarningTimeField(int warningTime) {
        try {
            ReflectionUtil.invokeMethod(worldBorder, "b", new Class[] {int.class}, new Object[]{warningTime});
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to set warning time:", exception);
        }
    }

    @Override
    protected void transitionSizeBetween(double oldSize, double newSize, long animationTime) {
        try {
            ReflectionUtil.invokeMethod(worldBorder, "a", new Class[]{double.class, double.class, long.class}, new Object[]{oldSize, newSize, animationTime});
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to set size:", exception);
        }
    }
}
