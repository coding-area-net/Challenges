package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.EntityDamageByPlayerEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Since("2.1.3")
public class RandomTeleportOnHitChallenge extends Setting {

  public RandomTeleportOnHitChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.RANDOMIZER, new ItemStack(Material.ENDER_CHEST), "mob-damage-teleport");
  }

  public static void switchEntityLocations(LivingEntity entity1, LivingEntity entity2) {
    entity1.setInvisible(true);
    entity2.setInvisible(false);
    Location entity2Location = entity2.getLocation().clone();
    entity2.teleport(entity1.getLocation());
    entity1.teleport(entity2Location);
    entity2.setInvisible(false);
    entity1.setInvisible(false);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityDamageByPlayer(EntityDamageByPlayerEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getDamager())) return;

    World world = event.getDamager().getWorld();
    List<LivingEntity> livingEntities = new ArrayList<>(world.getLivingEntities());
    livingEntities.removeIf(entity -> entity == event.getDamager() || entity instanceof Player && ignorePlayer((Player) entity));
    LivingEntity entity = globalRandom.choose(livingEntities);

    switchEntityLocations(entity, event.getDamager());
  }

}
