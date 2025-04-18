package net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_13;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.bukkit.nms.NMSUtils;
import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import net.codingarea.challenges.plugin.utils.bukkit.nms.type.BorderPacketFactory;
import net.codingarea.challenges.plugin.utils.bukkit.nms.type.PacketBorder;

public class BorderPacketFactory_1_13 extends BorderPacketFactory {

    @Override
    public Object center(PacketBorder packetBorder) {
        return createPacket(packetBorder, "SET_CENTER");
    }

    @Override
    public Object size(PacketBorder packetBorder) {
        return createPacket(packetBorder, "SET_SIZE");
    }

    @Override
    public Object warningDelay(PacketBorder packetBorder) {
        return createPacket(packetBorder, "SET_WARNING_TIME");
    }

    @Override
    public Object warningDistance(PacketBorder packetBorder) {
        return createPacket(packetBorder, "SET_WARNING_BLOCKS");
    }

    private Object createPacket(PacketBorder packetBorder, String worldBorderAction) {
        try {
            Class<?> clazz = NMSUtils.getClass("PacketPlayOutWorldBorder");
            Class<?> actionClazz = NMSUtils.getClass("PacketPlayOutWorldBorder$EnumWorldBorderAction");
            assert actionClazz != null;
            for (Object enumConstant : actionClazz.getEnumConstants()) {
                if (enumConstant.toString().equalsIgnoreCase(worldBorderAction)) {
                    return ReflectionUtil.invokeConstructor(clazz, new Class[]{packetBorder.getNMSClass(), actionClazz}, new Object[]{packetBorder.getWorldBorderObject(), enumConstant});
                }
            }
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("Failed to create packet for action {}:", worldBorderAction, exception);
        }
        return null;
    }
}
