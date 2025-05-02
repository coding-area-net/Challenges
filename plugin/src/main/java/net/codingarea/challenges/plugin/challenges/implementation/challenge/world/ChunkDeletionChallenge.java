package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.codingarea.commons.common.collection.pair.Tuple;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class ChunkDeletionChallenge extends SettingModifier {

  private final HashMap<Chunk, Tuple<BukkitTask, Long>> chunks = new HashMap<>();

  public ChunkDeletionChallenge() {
    super(MenuType.CHALLENGES, 1, 120, 60);
    setCategory(SettingCategory.WORLD);
  }

  @Override
  public @NotNull ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.GOLDEN_PICKAXE, Message.forName("item-chunk-deletion-challenge"));
  }

  @Override
  protected @Nullable String[] getSettingsDescription() {
    return Message.forName("item-time-seconds-description").asArray(getValue());
  }

  @Override
  protected void onEnable() {
    bossbar.setContent((bossbar, player) -> {
      Message message = Message.forName("bossbar-chunk-deletion");
      bossbar.setColor(BarColor.PINK);
      if (!checkIfAllowed(player)) {
        Tuple<BukkitTask, Long> taskTuple = chunks.get(player.getLocation().getChunk());
        if (taskTuple != null) {
          long remainingTime = calculateRemainingTime(taskTuple.getSecond());
          bossbar.setTitle(message.asString(remainingTime));
        } else {
          bossbar.setTitle(message.asString(getValue()));
        }
        return;
      }
      Chunk chunk = player.getLocation().getChunk();
      scheduleChunkDeletion(chunk);
      Long timeStamp = chunks.get(player.getLocation().getChunk()).getSecond();
      long remainingTime = calculateRemainingTime(timeStamp);

      bossbar.setTitle(message.asString(remainingTime));
      bossbar.setProgress((double) remainingTime / getValue());
    });
    bossbar.show();
  }

  private long calculateRemainingTime(long timeStamp) {
    return (timeStamp - System.currentTimeMillis()) / 1000;
  }

  @Override
  protected void onDisable() {
    chunks.forEach((chunk, taskTuple) -> taskTuple.getFirst().cancel());
    chunks.clear();
    bossbar.hide();
  }

  @ScheduledTask(ticks = 20)
  public void onSecond() {
    if (!shouldExecuteEffect()) {
      return;
    }
    bossbar.update();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerMove(PlayerMoveEvent event) {
    if (event.getTo() == null || !checkIfAllowed(event.getPlayer())) {
      return;
    }

    Chunk chunk = event.getTo().getChunk();
    scheduleChunkDeletion(chunk);
  }

  private boolean checkIfAllowed(Player player) {
    GameMode gameMode = player.getGameMode();
    return shouldExecuteEffect() && gameMode != GameMode.SPECTATOR && gameMode != GameMode.CREATIVE;
  }

  private void scheduleChunkDeletion(Chunk chunk) {
    chunks.computeIfAbsent(chunk, key -> {
      long timer = getValue() * 20L;
      BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
        if (!shouldExecuteEffect()) {
          BukkitTask task = chunks.remove(chunk).getFirst();
          if (task != null) {
            task.cancel();
          }
          return;
        }
        BukkitTask task = chunks.remove(chunk).getFirst();
        if (task != null) {
          task.cancel();
        }
        deleteChunk(chunk);
      }, timer);
      return new Tuple<>(bukkitTask, System.currentTimeMillis() + (timer * 50L));
    });
  }

  private void deleteChunk(@NotNull Chunk chunk) {
    World world = chunk.getWorld();
    Bukkit.getScheduler().runTask(plugin, () -> {
      for (int x = 0; x < 16; x++) {
        for (int z = 0; z < 16; z++) {
          for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
            Block block = chunk.getBlock(x, y, z);
            if (world.getEnvironment() == Environment.THE_END && block.getType() == Material.BEDROCK) {
              // avoid end portal destruction. results in buggy ender dragon
              continue;
            }
            block.setType(Material.AIR);
          }
        }
      }
    });
  }
}
