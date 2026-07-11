package net.codingarea.challenges.plugin.utils.misc;

import net.codingarea.challenges.plugin.Challenges;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class ExperimentalUtils {

  private static Material[] materials;
  private static EntityType[] entityTypes;

  public static Material[] getMaterials() {
    if (materials == null) {
      loadMaterials();
    }
    return materials;
  }

  private static void loadMaterials() {
    List<Material> materials = new LinkedList<>();

    World world = Challenges.getInstance().getGameWorldStorage().getWorld(World.Environment.NORMAL);
    for (Material material : Material.values()) {
      if (!isMaterialFeatureEnabled(material, world)) continue;
      materials.add(material);
    }
    ExperimentalUtils.materials = materials.toArray(new Material[0]);
  }

  private static boolean isMaterialFeatureEnabled(@NotNull Material material, @NotNull World world) {
    try {
      return FeatureDependentAccessor.isMaterialFeatureEnabled(material, world);
    } catch (Throwable _) {
      return true;
    }
  }

  public static EntityType[] getEntityTypes() {
    if (entityTypes == null) {
      loadEntityTypes();
    }
    return entityTypes;
  }

  private static void loadEntityTypes() {
    List<EntityType> entityTypes = new LinkedList<>();

    World world = Challenges.getInstance().getGameWorldStorage().getWorld(World.Environment.NORMAL);
    for (EntityType type : EntityType.values()) {
      if (!isEntityTypeFeatureEnabled(type, world)) continue;

      entityTypes.add(type);
    }
    ExperimentalUtils.entityTypes = entityTypes.toArray(new EntityType[0]);
  }

  private static boolean isEntityTypeFeatureEnabled(@NotNull EntityType entityType, @NotNull World world) {
    try {
      return FeatureDependentAccessor.isEntityTypeFeatureEnabled(entityType, world);
    } catch (Throwable _) {
      return true;
    }
  }

}
