package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SurfaceHoleChallenge extends SettingModifier {

  public SurfaceHoleChallenge() {
    super(MenuType.CHALLENGES, 1, 60, 30);
    setCategory(SettingCategory.WORLD);
  }

  @EventHandler
  public void onMove(@Nonnull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR)
      return;

    Location location = event.getTo();
    if (location == null) return;
    if (BlockUtils.isSameBlockLocationIgnoreHeight(event.getFrom(), location)) return;

    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
      if (!shouldExecuteEffect()) return;

      World world = event.getPlayer().getWorld();

      List<Block> blocks = new ArrayList<>();
      for (int y = BukkitReflectionUtils.getMinHeight(world); y < world.getMaxHeight(); y++) {
        Location blockLocation = location.clone();
        blockLocation.setY(y);
        blocks.add(blockLocation.getBlock());
      }

      Bukkit.getScheduler().runTask(plugin, () -> {
        for (Block block : blocks) {
          block.setType(Material.AIR, false);
        }
      });

    }, getValue() * 20L);

  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.BARRIER, Message.forName("item-surface-hole-challenge"));
  }

  @Nullable
  @Override
  protected String[] getSettingsDescription() {
    return Message.forName("item-time-seconds-description").asArray(getValue());
  }

}
