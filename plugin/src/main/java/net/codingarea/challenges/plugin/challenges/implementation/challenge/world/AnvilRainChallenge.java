package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.codingarea.challenges.plugin.challenges.type.abstraction.MenuSetting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.management.stats.Statistic.Display;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDropItemEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnvilRainChallenge extends MenuSetting {

  private final Random random = new Random();
  int currentTime = 0;

  public AnvilRainChallenge() {
    super(MenuType.CHALLENGES, Message.forName("menu-anvil-rain-challenge-settings"));
    setCategory(SettingCategory.WORLD);
    registerSetting("time", new NumberSubSetting(
        () -> new ItemBuilder(Material.CLOCK, Message.forName("item-anvil-rain-time-challenge")),
        value -> null,
        value -> "§e" + value + " §7" + Message.forName(value == 1 ? "second" : "seconds").asString(),
        1,
        30,
        7
      )
    );
    registerSetting("count", new NumberSubSetting(
        () -> new ItemBuilder(Material.FLINT, Message.forName("item-anvil-rain-count-challenge")),
        1,
        30,
        8
      )
    );
    registerSetting("range", new NumberSubSetting(
        () -> new ItemBuilder(Material.COMPASS, Message.forName("item-anvil-rain-range-challenge")),
        value -> null,
        value -> "§e" + value + " §7" + (value == 1 ? "chunk" : "chunks"),
        1,
        3,
        2
      )
    );
    registerSetting("damage", new NumberSubSetting(
        () -> new ItemBuilder(Material.IRON_SWORD, Message.forName("item-anvil-rain-damage-challenge")),
        value -> null,
        value -> "§e" + Display.HEARTS.formatChat(value),
        1,
        60,
        30
      )
    );
  }

  @Override
  protected void onDisable() {
    removeAnvils();
  }

  @TimerTask(async = false, status = TimerStatus.PAUSED)
  public void onPause() {
    removeAnvils();
  }

  private void removeAnvils() {
    for (World world : Bukkit.getWorlds()) {
      for (FallingBlock entity : world.getEntitiesByClass(FallingBlock.class)) {
        // TODO: SWITCH CASE
        String name = entity.getBlockData().getMaterial().name();
        if (!name.contains("ANVIL")) continue;
        entity.remove();
      }
    }
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.ANVIL, Message.forName("item-anvil-rain-challenge"));
  }

  @ScheduledTask(ticks = 20, async = false)
  public void onSecond() {
    currentTime++;

    if (currentTime > getSetting("time").getAsInt()) {
      currentTime = 0;
      handleTimeActivation();
    }

  }

  private void handleTimeActivation() {
    List<Chunk> chunks = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (ignorePlayer(player)) continue;

      List<Chunk> targetChunks = getTargetChunks(player.getLocation().getChunk());
      for (Chunk targetChunk : targetChunks) {
        if (chunks.contains(targetChunk)) continue;
        chunks.add(targetChunk);
        spawnAnvils(targetChunk, getHeight(player.getLocation().getBlockY()));
      }
    }

  }

  private void spawnAnvils(@Nonnull Chunk chunk, int height) {
    for (int i = 0; i < getCount(); i++) {
      Block block = getRandomBlockInChunk(chunk, height);
      Location location = block.getLocation().add(0.5, 0, 0.5);
      block.getWorld().spawnFallingBlock(location, Material.ANVIL, ((byte) 0));
    }
  }

  private List<Chunk> getTargetChunks(@Nonnull Chunk origin) {
    List<Chunk> chunks = new ArrayList<>();

    int originX = origin.getX();
    int originZ = origin.getZ();

    int range = getRange();
    for (int x = -range; x <= range; x++) {
      for (int z = -range; z <= range; z++) {
        Chunk chunkAt = origin.getWorld().getChunkAt(originX + x, originZ + z);
        chunks.add(chunkAt);
      }

    }

    return chunks;
  }

  private Block getRandomBlockInChunk(@Nonnull Chunk chunk, int y) {
    int x = random.nextInt(16);
    int z = random.nextInt(16);
    return chunk.getBlock(x, y, z);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityChangeBlock(@Nonnull EntityChangeBlockEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof FallingBlock)) return;

    String name = ((FallingBlock) event.getEntity()).getBlockData().getMaterial().name();
    if (!name.contains("ANVIL")) return;

    Block block = event.getBlock().getLocation().subtract(0, 1, 0).getBlock();
    if (BukkitReflectionUtils.isAir(block.getType())) return;

    event.getBlock().setType(Material.AIR);
    event.setCancelled(true);
    event.getEntity().remove();
    destroyRandomBlocks(event.getBlock().getLocation());
    applyDamageToNearEntities(event.getBlock().getLocation().add(0.5, 0.5, 0.5));
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onDrop(@Nonnull EntityDropItemEvent event) {
    if (event.getItemDrop().getItemStack().getType() != Material.ANVIL) return;
    if (event.getEntityType() != EntityType.FALLING_BLOCK) return;
    String name = ((FallingBlock) event.getEntity()).getBlockData().getMaterial().name();
    if (!name.contains("ANVIL")) return;

    event.setCancelled(true);

    destroyRandomBlocks(event.getEntity().getLocation());
    applyDamageToNearEntities(event.getEntity().getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5));
  }

  public void destroyRandomBlocks(@Nonnull Location origin) {
    int i = random.nextInt(2);

    if (i == 0) return;
    int blocks = getCount() < 16 ? 0 : 1;
    while (blocks < 2 && origin.getBlockY() > 1) {

      if (origin.getBlock().getType() == Material.WATER || origin.getBlock().getType() == Material.LAVA)
        return;
      origin.subtract(0, 1, 0);
      if (origin.getBlock().isPassable()) blocks--;

      origin.getBlock().setType(Material.AIR);
      blocks++;
    }

  }

  public void applyDamageToNearEntities(@Nonnull Location location) {
    if (location.getWorld() == null) return;
    for (Entity entity : location.getWorld().getNearbyEntities(location, 0.25, 0.25, 0.25)) {
      if (!(entity instanceof LivingEntity)) continue;
      LivingEntity livingEntity = (LivingEntity) entity;
      livingEntity.damage(getDamage());
    }

  }

  private int getRange() {
    return getSetting("range").getAsInt();
  }

  private int getCount() {
    return getSetting("count").getAsInt();
  }

  private int getDamage() {
    return getSetting("damage").getAsInt();
  }

  private int getHeight(int currentHeight) {
    return currentHeight + 50;
  }


}
