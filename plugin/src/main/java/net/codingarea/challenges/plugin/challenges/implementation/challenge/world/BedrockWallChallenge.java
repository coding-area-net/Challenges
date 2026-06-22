package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.commons.bukkit.utils.misc.BukkitReflectionUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BedrockWallChallenge extends SettingModifier {

  public BedrockWallChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.WORLD, 1, 60, 30, new ItemStack(Material.BEDROCK), "bedrock-walls");
  }

  @EventHandler
  public void onMove(@NotNull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR)
      return;

    Location location = event.getTo();
    if (location == null) return;
    if (BlockUtils.isSameBlockLocationIgnoreHeight(event.getFrom(), location)) return;

    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
      World world = event.getPlayer().getWorld();

      List<Block> blocks = new ArrayList<>();
      for (int y = BukkitReflectionUtils.getMinHeight(world) + 1; y < world.getMaxHeight(); y++) {
        Location blockLocation = location.clone();
        blockLocation.setY(y);
        blocks.add(blockLocation.getBlock());
      }

      Bukkit.getScheduler().runTask(plugin, () -> {
        for (Block block : blocks) {
          block.setType(Material.BEDROCK, false);
        }
      });

    }, getValue() * 20L);

  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-time-seconds-description").asArray(getValue());
//  }

}
