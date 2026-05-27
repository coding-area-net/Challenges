package net.codingarea.challenges.plugin.spigot.listener;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.misc.ParticleUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.jetbrains.annotations.NotNull;

public class RestrictionListener implements Listener {

  @EventHandler(priority = EventPriority.LOW)
  public void onEntityDamage(@NotNull EntityDamageEvent event) {
    if (ChallengeAPI.isStarted()) return;
    Entity entity = event.getEntity();
    if (entity instanceof Player && ((Player) entity).getGameMode() == GameMode.CREATIVE) {
      return;
    }

    if (event.getCause() != DamageCause.VOID && (entity instanceof Player || event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.PROJECTILE)) {
      entity.setFireTicks(entity instanceof Player ? 0 : entity.getFireTicks());
      event.setCancelled(true);
      ParticleUtils.spawnParticleCircleAroundEntity(Challenges.getInstance(), entity);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onEntityDeath(@NotNull EntityDeathEvent event) {
    if (ChallengeAPI.isStarted()) return;
    if (!(event.getEntity() instanceof Player)) return;
    event.getDrops().clear();
    event.setDroppedExp(0);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockBreak(@NotNull BlockBreakEvent event) {
    if (ChallengeAPI.isPaused() && event.getPlayer().getGameMode() != GameMode.CREATIVE)
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockPlace(@NotNull BlockPlaceEvent event) {
    if (ChallengeAPI.isPaused() && event.getPlayer().getGameMode() != GameMode.CREATIVE)
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onFoodLevelChange(@NotNull FoodLevelChangeEvent event) {
    if (ChallengeAPI.isPaused())
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onEntityRegainHealth(@NotNull EntityRegainHealthEvent event) {
    if (ChallengeAPI.isPaused())
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onItemPickup(@NotNull EntityPickupItemEvent event) {
    if (ChallengeAPI.isStarted()) return;
    if (!(event.getEntity() instanceof Player)) return;
    Player player = (Player) event.getEntity();
    if (player.getGameMode() != GameMode.CREATIVE)
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onDrop(@NotNull PlayerDropItemEvent event) {
    if (ChallengeAPI.isStarted()) return;
    if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onInteract(@NotNull PlayerInteractEvent event) {
    if (ChallengeAPI.isPaused() && event.getPlayer().getGameMode() != GameMode.CREATIVE)
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onInteract(@NotNull PlayerInteractAtEntityEvent event) {
    if (ChallengeAPI.isPaused() && event.getPlayer().getGameMode() != GameMode.CREATIVE)
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onInteract(@NotNull PlayerInteractEntityEvent event) {
    if (ChallengeAPI.isPaused() && event.getPlayer().getGameMode() != GameMode.CREATIVE)
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onOffHandItemSwitch(@NotNull PlayerSwapHandItemsEvent event) {
    if (ChallengeAPI.isPaused() && event.getPlayer().getGameMode() != GameMode.CREATIVE)
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onDamage(@NotNull VehicleDamageEvent event) {
    if (ChallengeAPI.isStarted()) return;
    Entity entity = event.getVehicle();
    event.setCancelled(true);
    if (event.getAttacker() instanceof Player) {
      if (((Player) event.getAttacker()).getGameMode() == GameMode.CREATIVE)
        event.setCancelled(false);
      ParticleUtils.spawnParticleCircleAroundEntity(Challenges.getInstance(), entity);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onDamage(@NotNull VehicleDestroyEvent event) {
    if (ChallengeAPI.isPaused() && !(event.getAttacker() instanceof Player && ((Player) event.getAttacker()).getGameMode() == GameMode.CREATIVE))
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onWeatherChange(@NotNull WeatherChangeEvent event) {
    if (ChallengeAPI.isPaused() && event.toWeatherState())
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onThunderChange(@NotNull ThunderChangeEvent event) {
    if (ChallengeAPI.isPaused() && event.toThunderState())
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onTarget(@NotNull EntityTargetEvent event) {
    if (ChallengeAPI.isPaused() && event.getTarget() != null)
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onClick(@NotNull InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) return;
    Player player = (Player) event.getWhoClicked();
    if (ChallengeAPI.isPaused() && player.getGameMode() != GameMode.CREATIVE)
      event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockSpread(@NotNull BlockSpreadEvent event) {
    if (ChallengeAPI.isStarted()) return;
    event.setCancelled(true);

  }

}
