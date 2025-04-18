package net.codingarea.challenges.plugin.spigot.listener;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockDropListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockBreak(@Nonnull BlockBreakEvent event) {
    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
    if (!event.isDropItems()) return;
    dropCustomDrops(event.getBlock(), () -> event.setDropItems(false));
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockExplosion(@Nonnull BlockExplodeEvent event) {
    handleExplosion(event.blockList(), () -> event.setYield(0));
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityExplosion(@Nonnull EntityExplodeEvent event) {
    handleExplosion(event.blockList(), () -> event.setYield(0));
  }

  protected void handleExplosion(@Nonnull Iterable<Block> blocklist, @Nonnull Runnable dropsExist) {
    for (Block block : blocklist) {
      dropCustomDrops(block, dropsExist);
    }
  }

  protected void dropCustomDrops(@Nonnull Block block, @Nonnull Runnable dropsExist) {

    Material material = block.getType();
    if (BukkitReflectionUtils.isAir(material)) return;

    if (!ChallengeAPI.getDropChance(material)) {
      dropsExist.run();
      return;
    }

    List<Material> drops = Challenges.getInstance().getBlockDropManager().getCustomDrops(material);
    if (drops.isEmpty()) return;

    Location location = block.getLocation().clone().add(0.5, 0, 0.5);
    if (location.getWorld() == null) return;

    dropsExist.run();
    for (Material drop : drops) {
      try {
        location.getWorld().dropItem(location, new ItemStack(drop));
      } catch (Exception ex) {
        Logger.warn("Unable to drop custom drop {}", drop, ex);
      }
    }

  }

}
