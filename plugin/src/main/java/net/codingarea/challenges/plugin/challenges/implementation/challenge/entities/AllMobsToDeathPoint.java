package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.EntityDeathByPlayerEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;

import javax.annotation.Nonnull;
import java.util.Collection;

@Since("2.0")
public class AllMobsToDeathPoint extends Setting {

  public AllMobsToDeathPoint() {
    super(MenuType.CHALLENGES);
    setCategory(SettingCategory.ENTITIES);
  }

  @EventHandler
  public void onEntityDeath(@Nonnull EntityDeathByPlayerEvent event) {
    if (!shouldExecuteEffect()) return;
    if (event.getEntity() instanceof EnderDragon || event.getEntity() instanceof Player) return;
    if (!(event.getEntity() instanceof LivingEntity)) return;
    if (ignorePlayer(event.getKiller())) return;

    teleportAllMobsOfType(event.getEntityType(), event.getEntity().getLocation());
  }

  private void teleportAllMobsOfType(@Nonnull EntityType entityType, @Nonnull Location location) {
    if (location.getWorld() == null) return;
    Collection<Entity> entities = location.getWorld().getEntitiesByClasses(entityType.getEntityClass());
    for (Entity entity : entities) {
      if (!(entity instanceof LivingEntity)) return;
      ((LivingEntity) entity).setNoDamageTicks(20);
      entity.teleport(location);
    }

  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.SPAWNER, Message.forName("item-all-mobs-to-death-position-challenge"));
  }

}
