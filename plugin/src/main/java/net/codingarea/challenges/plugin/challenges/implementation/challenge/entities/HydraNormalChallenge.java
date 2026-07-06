package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.codingarea.challenges.plugin.challenges.type.abstraction.HydraChallenge;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HydraNormalChallenge extends HydraChallenge {

  public HydraNormalChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.ENTITIES, new ItemStack(Material.WITCH_SPAWN_EGG), "hydra");
  }

  @Override
  public int getNewMobsCount(@NotNull EntityType entityType) {
    return 2;
  }
}
