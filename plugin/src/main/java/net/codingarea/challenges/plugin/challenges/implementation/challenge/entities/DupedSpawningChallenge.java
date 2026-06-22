package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DupedSpawningChallenge extends Setting {

  private boolean inCustomSpawn = false;

  public DupedSpawningChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.ENTITIES, new ItemStack(Material.ELDER_GUARDIAN_SPAWN_EGG), "duped-spawning-challenge");
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onSpawn(@NotNull EntitySpawnEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof LivingEntity)
      || event.getEntity() instanceof Player
      || event.getEntity() instanceof EnderDragon
    ) return;
    if (inCustomSpawn) return;
    inCustomSpawn = true;
    Entity entity = event.getEntity().getWorld().spawnEntity(event.getLocation(), event.getEntityType());

    if (entity instanceof Slime && event.getEntity() instanceof Slime) {
      ((Slime) entity).setSize(((Slime) event.getEntity()).getSize());
    }

    inCustomSpawn = false;
  }

}
