package net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_17;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.bukkit.nms.NMSUtils;
import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_13.PacketBorder_1_13;
import org.bukkit.World;

/**
 * @author sehrschlechtYT | <a href="https://github.com/sehrschlechtYT">...</a>
 * @since 2.2.3
 */
public class PacketBorder_1_17 extends PacketBorder_1_13 {

    public PacketBorder_1_17(World world) {
        super(
            world,
            NMSUtils.getClass("world.level.border.WorldBorder")
        );
    }


}