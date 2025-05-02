package net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_17;

import net.codingarea.challenges.plugin.utils.bukkit.nms.NMSUtils;
import net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_13.PacketBorder_1_13;
import org.bukkit.World;

public class PacketBorder_1_17 extends PacketBorder_1_13 {

  public PacketBorder_1_17(World world) {
    super(
      world,
      NMSUtils.getClass("world.level.border.WorldBorder")
    );
  }


}
