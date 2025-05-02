package net.codingarea.challenges.plugin.utils.misc;

import net.codingarea.challenges.plugin.Challenges;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

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

    for (Material material : Material.values()) {
      try {
        if (!material.isEnabledByFeature(Challenges.getInstance().getGameWorldStorage().getWorld(World.Environment.NORMAL))) {
          continue;
        }
      } catch (NoSuchMethodError ignored) {
      } // only NoSuchMethodException

      materials.add(material);
    }
    ExperimentalUtils.materials = materials.toArray(new Material[0]);
  }

  public static EntityType[] getEntityTypes() {
    if (entityTypes == null) {
      loadEntityTypes();
    }
    return entityTypes;
  }

  private static void loadEntityTypes() {
    List<EntityType> entityTypes = new LinkedList<>();

    for (EntityType type : EntityType.values()) {
      try {
        if (!type.isEnabledByFeature(Challenges.getInstance().getGameWorldStorage().getWorld(World.Environment.NORMAL))) {
          continue;
        }
      } catch (NoSuchMethodError | IllegalArgumentException ignored) {
      } // only NoSuchMethodException

      entityTypes.add(type);
    }
    ExperimentalUtils.entityTypes = entityTypes.toArray(new EntityType[0]);
  }

}
