package net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_17;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.bukkit.nms.NMSUtils;
import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import net.codingarea.challenges.plugin.utils.bukkit.nms.type.BorderPacketFactory;
import net.codingarea.challenges.plugin.utils.bukkit.nms.type.PacketBorder;

public class BorderPacketFactory_1_17 extends BorderPacketFactory {

  @Override
  public Object center(PacketBorder packetBorder) {
    return createPacket(packetBorder, "ClientboundSetBorderCenterPacket");
  }

  @Override
  public Object size(PacketBorder packetBorder) {
    return createPacket(packetBorder, "ClientboundSetBorderSizePacket");
  }

  @Override
  public Object warningDelay(PacketBorder packetBorder) {
    return createPacket(packetBorder, "ClientboundSetBorderWarningDelayPacket");
  }

  @Override
  public Object warningDistance(PacketBorder packetBorder) {
    return createPacket(packetBorder, "ClientboundSetBorderWarningDistancePacket");
  }

  private Object createPacket(PacketBorder packetBorder, String className) {
    Class<?> clazz = NMSUtils.getClass("network.protocol.game." + className);
    try {
      return ReflectionUtil.invokeConstructor(clazz, new Class[]{packetBorder.getNMSClass()}, new Object[]{packetBorder.getWorldBorderObject()});
    } catch (Exception exception) {
      Challenges.getInstance().getILogger().error("Failed to create packet {}:", className, exception);
      return null;
    }
  }
}
