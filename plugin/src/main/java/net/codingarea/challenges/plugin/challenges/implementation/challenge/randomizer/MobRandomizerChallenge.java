package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.RandomizerSetting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.ListBuilder;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Since("2.0")
public class MobRandomizerChallenge extends RandomizerSetting {

  private final Map<EntityType, EntityType> entityRandomizer = new HashMap<>();
  private final Map<EntityType, EntityType> inverseRandomizer = new HashMap<>();

  private boolean inSpawn = false;
  private boolean initialSpawn = false;

  public MobRandomizerChallenge() {
    super(MenuType.CHALLENGES);
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    if (!shouldExecuteEffect()) return;
    initialSpawn = true;
    loadAllEntities();
    initialSpawn = false;
  }

  @Override
  protected void onDisable() {
    super.onDisable();
    unLoadAllEntities();
  }

  @TimerTask(status = TimerStatus.PAUSED, async = false)
  public void onPause() {
    unLoadAllEntities();
  }

  private void loadAllEntities() {
    for (World world : ChallengeAPI.getGameWorlds()) {
      for (LivingEntity entity : world.getLivingEntities()) {
        Bukkit.getScheduler().runTask(Challenges.getInstance(), () -> {
          if (!entityRandomizer.containsKey(entity.getType())) return;
          entity.remove();
          entity.getWorld().spawnEntity(entity.getLocation(), entityRandomizer.get(entity.getType()));
        });
      }

    }
  }

  private void unLoadAllEntities() {
    inSpawn = true;

    for (World world : ChallengeAPI.getGameWorlds()) {
      for (LivingEntity entity : world.getLivingEntities()) {
        if (!inverseRandomizer.containsKey(entity.getType())) continue;
        entity.remove();
        EntityType entityType = inverseRandomizer.get(entity.getType());
        entity.getWorld().spawnEntity(entity.getLocation(), entityType);

      }

    }

    inSpawn = false;
  }


  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.COMMAND_BLOCK_MINECART, Message.forName("item-mob-randomizer-challenge"));
  }

  @Override
  protected void reloadRandomization() {
    List<EntityType> entityTypes = getSpawnAbleEntities();
    List<EntityType> randomEntityTypes = new ArrayList<>(entityTypes);
    random.shuffle(randomEntityTypes);

    for (int i = 0; i < entityTypes.size(); i++) {
      EntityType type = entityTypes.get(i);
      EntityType randomType = randomEntityTypes.get(i);
      entityRandomizer.put(type, randomType);
      inverseRandomizer.put(randomType, type);
    }

  }

  public List<EntityType> getSpawnAbleEntities() {
    ListBuilder<EntityType> builder = new ListBuilder<>(ExperimentalUtils.getEntityTypes())
      .removeIf(type -> !type.isSpawnable())
      .removeIf(type -> !type.isAlive())
      .remove(EntityType.ENDER_DRAGON)
      .remove(EntityType.WITHER)
      .remove(EntityType.GIANT)
      .remove(EntityType.ILLUSIONER);

    return builder.build();
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntitySpawn(@NotNull EntitySpawnEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!entityRandomizer.containsKey(event.getEntityType())) return;
    if (inSpawn) return;

    event.setCancelled(true);

    EntityType type = entityRandomizer.get(event.getEntityType());
    Location location = event.getLocation();
    if (location.getWorld() == null) return;
    if (!initialSpawn && !maySpawn(type, location.getWorld())) return;
    inSpawn = true;
    event.getEntity().getWorld().spawnEntity(location, type);
    inSpawn = false;
  }

  private boolean maySpawn(@NotNull EntityType newType, @NotNull World world) {
    EntityCategory category = getEntityCategory(newType);
    int currentMobCount = getCurrentMobCount(category, world);
    int spawnLimit = getEntityCategory(newType).getSpawnLimit(world);
    return currentMobCount < spawnLimit;
  }

  private int getCurrentMobCount(@NotNull EntityCategory entityState, @NotNull World world) {

    int mobCount = 0;

    for (Entity entity : world.getLivingEntities()) {
      if (!(entity instanceof LivingEntity)) continue;
      EntityCategory entityTypeState = getEntityCategory(entity.getType());
      if (entityState == entityTypeState) {
        mobCount++;
      }

    }

    return mobCount;
  }

  private EntityCategory getEntityCategory(@NotNull EntityType type) {
    Class<? extends Entity> entity = type.getEntityClass();
    if (entity == null) return EntityCategory.OTHER;

    if (Animals.class.isAssignableFrom(entity)) {
      return EntityCategory.ANIMAL;
    }
    if (Drowned.class.isAssignableFrom(entity) || Guardian.class.isAssignableFrom(entity)) {
      return EntityCategory.WATER_AMBIENT;
    }
    if (Monster.class.isAssignableFrom(entity)) {
      return EntityCategory.HOSTILE;
    }
    if (WaterMob.class.isAssignableFrom(entity)) {
      return EntityCategory.WATER_ANIMAL;
    }

    return EntityCategory.AMBIENT;
  }

  public enum EntityCategory {

    HOSTILE,
    AMBIENT,
    ANIMAL,
    WATER_ANIMAL,
    WATER_AMBIENT,
    OTHER;

    private int getSpawnLimit(@NotNull World world) {
      boolean useSpawnCategories = MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_19); // World#getSpawnLimit was added in 1.19
      switch (this) {
        case AMBIENT:
          return useSpawnCategories ? world.getSpawnLimit(SpawnCategory.AMBIENT) : world.getAmbientSpawnLimit();
        case HOSTILE:
          return useSpawnCategories ? world.getSpawnLimit(SpawnCategory.MONSTER) : world.getMonsterSpawnLimit();
        case ANIMAL:
          return useSpawnCategories ? world.getSpawnLimit(SpawnCategory.ANIMAL) : world.getAnimalSpawnLimit();
        case
          WATER_AMBIENT: { // getWaterAmbientSpawnLimit is not available in lower versions like 1.13, default to water animal then
          try {
            return useSpawnCategories ? world.getSpawnLimit(SpawnCategory.WATER_AMBIENT) : world.getWaterAmbientSpawnLimit();
          } catch (Throwable throwable) {
            Challenges.getInstance().getILogger().error("", throwable);
          }
        }
        case WATER_ANIMAL:
          return useSpawnCategories ? world.getSpawnLimit(SpawnCategory.WATER_ANIMAL) : world.getWaterAnimalSpawnLimit();
        default:
          return 0;
      }
    }

  }

}
