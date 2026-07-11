package net.codingarea.challenges.plugin.utils.misc;

import io.papermc.paper.world.flag.FeatureDependant;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

// io.papermc.paper.world.flag.FeatureDependant was only added in 1.20.2
// for versions below the import will result in a NoClassDefFoundError
final class FeatureDependentAccessor {

  static boolean isMaterialFeatureEnabled(@NotNull Material material, @NotNull World world) {
    FeatureDependant feature = material.isItem() ? material.asItemType() : material.asBlockType();
    return feature == null || world.isEnabled(feature);
  }

  static boolean isEntityTypeFeatureEnabled(@NotNull EntityType entityType, @NotNull World world) {
    return world.isEnabled(entityType);
  }

}
