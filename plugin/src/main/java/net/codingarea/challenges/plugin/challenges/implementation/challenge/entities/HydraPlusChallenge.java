package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.codingarea.challenges.plugin.challenges.type.abstraction.HydraChallenge;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class HydraPlusChallenge extends HydraChallenge {

  private static final int limit = 512;

  public HydraPlusChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.ENTITIES, new ItemStack(Material.BAT_SPAWN_EGG), "hydra-plus");
  }

  @Override
  public int getNewMobsCount(@NotNull EntityType entityType) {
    int currentCount = getGameStateData().getInt(entityType.name());
    if (currentCount == 0) {
      currentCount = 2;
    } else {
      currentCount *= 2;
    }
    getGameStateData().set(entityType.name(), Math.min(currentCount, limit));
    return currentCount;
  }

}
