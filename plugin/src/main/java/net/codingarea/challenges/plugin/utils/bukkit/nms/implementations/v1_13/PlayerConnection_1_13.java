package net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_13;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.bukkit.nms.NMSUtils;
import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import net.codingarea.challenges.plugin.utils.bukkit.nms.type.PlayerConnection;

public class PlayerConnection_1_13 extends PlayerConnection {
  public PlayerConnection_1_13(Object connection) throws ClassNotFoundException {
    super(NMSUtils.getClass("network.protocol.Packet"), connection);
  }

  @Override
  public void sendPacket(Object packet) {
    try {
      ReflectionUtil.invokeMethod(this.connection, "sendPacket", new Class<?>[]{nmsClass}, new Object[]{packet});
    } catch (Exception exception) {
      Challenges.getInstance().getILogger().error("Failed to send packet {}:", packet.getClass().getSimpleName(), exception);
    }
  }
}
