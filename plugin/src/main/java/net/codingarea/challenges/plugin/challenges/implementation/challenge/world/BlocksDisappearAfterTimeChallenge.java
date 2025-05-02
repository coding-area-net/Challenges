package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Since("2.0")
public class BlocksDisappearAfterTimeChallenge extends SettingModifier {

  private final Map<Block, BukkitTask> tasks = new HashMap<>();

  public BlocksDisappearAfterTimeChallenge() {
    super(MenuType.CHALLENGES, 60, 300);
    setCategory(SettingCategory.WORLD);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.STRING, Message.forName("item-blocks-disappear-time-challenge"));
  }

  @Nullable
  @Override
  protected String[] getSettingsDescription() {
    return Message.forName("item-time-seconds-description").asArray(getValue());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockPlace(@Nonnull BlockPlaceEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;

    BukkitTask oldTask = tasks.remove(event.getBlock());
    if (oldTask != null) oldTask.cancel();
    tasks.put(event.getBlock(), runTask(event.getBlock()));

  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockPlace(@Nonnull BlockBreakEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;

    BukkitTask oldTask = tasks.remove(event.getBlock());
    if (oldTask != null) oldTask.cancel();
  }

  private BukkitTask runTask(@Nonnull Block block) {
    return Bukkit.getScheduler().runTaskLater(plugin, () -> block.setType(Material.AIR), getValue() * 20L);
  }

}
