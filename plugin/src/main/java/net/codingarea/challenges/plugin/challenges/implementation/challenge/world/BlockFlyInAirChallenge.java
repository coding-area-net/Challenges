package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@Since("2.1.1")
public class BlockFlyInAirChallenge extends Setting {

  public BlockFlyInAirChallenge() {
    super(MenuType.CHALLENGES);
    setCategory(SettingCategory.WORLD);
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.FERN, Message.forName("item-blocks-fly-challenge"));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMove(PlayerMoveEvent event) {
    if (event.getTo() == null) return;
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;

    Block blockBelow = BlockUtils.getBlockBelow(event.getTo());
    if (blockBelow == null) return;
    if (!blockBelow.getType().isSolid()) return;

    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      boostBlockInAir(blockBelow);
    }, 20);

  }

  private void boostBlockInAir(Block block) {
    if (!block.getType().isSolid()) return;

    FallingBlock fallingBlock = block.getWorld()
      .spawnFallingBlock(block.getLocation().add(0.5, 0, 0.5), block.getBlockData());
    fallingBlock.setInvulnerable(true);
    fallingBlock.setVelocity(new Vector(0, 1, 0));

    block.setType(Material.AIR);
  }


}
