package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Since("2.0")
public class BlocksDisappearAfterTimeChallenge extends SettingModifier {

  private final Map<Block, BukkitTask> tasks = new HashMap<>();

  public BlocksDisappearAfterTimeChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.WORLD, 60, 300, new ItemStack(Material.STRING), "blocks-disappear-time");
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-time-seconds-description").asArray(getValue());
//  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockPlace(@NotNull BlockPlaceEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;

    BukkitTask oldTask = tasks.remove(event.getBlock());
    if (oldTask != null) oldTask.cancel();
    tasks.put(event.getBlock(), runTask(event.getBlock()));

  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockPlace(@NotNull BlockBreakEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;

    BukkitTask oldTask = tasks.remove(event.getBlock());
    if (oldTask != null) oldTask.cancel();
  }

  private BukkitTask runTask(@NotNull Block block) {
    return Bukkit.getScheduler().runTaskLater(plugin, () -> block.setType(Material.AIR), getValue() * 20L);
  }

}
