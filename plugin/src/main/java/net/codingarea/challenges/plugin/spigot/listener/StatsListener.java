package net.codingarea.challenges.plugin.spigot.listener;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.management.scheduler.policy.FreshnessPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.management.stats.PlayerStats;
import net.codingarea.challenges.plugin.management.stats.Statistic;
import net.codingarea.challenges.plugin.spigot.events.PlayerJumpEvent;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class StatsListener implements Listener {

  private final List<Player> dragonDamager = new ArrayList<>();

  @TimerTask(status = TimerStatus.RUNNING, freshnessPolicy = FreshnessPolicy.FRESH, async = false)
  public void onStart() {
    if (!Challenges.getInstance().getStatsManager().isEnabled()) return;

    for (Player player : Bukkit.getOnlinePlayers()) {
      Challenges.getInstance().getStatsManager().getStats(player).incrementStatistic(Statistic.CHALLENGES_PLAYED, 1);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(@Nonnull EntityDamageEvent event) {
    if (countNoStats()) return;
    if (!(event.getEntity() instanceof Player)) return;
    if (event.getCause() == DamageCause.VOID) return;

    Player player = (Player) event.getEntity();
    if (AbstractChallenge.ignorePlayer(player)) return;
    incrementStatistic(player, Statistic.DAMAGE_TAKEN, event.getFinalDamage());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(@Nonnull EntityDamageByEntityEvent event) {
    if (countNoStats()) return;

    if (event.getDamager() instanceof Player) {
      Player player = (Player) event.getDamager();
      if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE)
        return;
      incrementStatistic(player, Statistic.DAMAGE_DEALT, event.getFinalDamage());

      if (event.getEntity() instanceof EnderDragon && !dragonDamager.contains(player))
        dragonDamager.add(player);
    } else if (event.getDamager() instanceof Projectile) {
      Projectile projectile = (Projectile) event.getDamager();
      if (!((projectile.getShooter()) instanceof Player)) return;
      Player player = (Player) projectile.getShooter();
      if (AbstractChallenge.ignorePlayer(player)) return;
      incrementStatistic(player, Statistic.DAMAGE_DEALT, event.getFinalDamage());

      if (event.getEntity() instanceof EnderDragon && !dragonDamager.contains(player))
        dragonDamager.add(player);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockPlace(@Nonnull BlockPlaceEvent event) {
    if (countNoStats()) return;
    if (AbstractChallenge.ignorePlayer(event.getPlayer())) return;
    incrementStatistic(event.getPlayer(), Statistic.BLOCKS_PLACED, 1);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockBreak(@Nonnull BlockBreakEvent event) {
    if (AbstractChallenge.ignorePlayer(event.getPlayer())) return;
    if (countNoStats()) return;
    incrementStatistic(event.getPlayer(), Statistic.BLOCKS_MINED, 1);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onDeath(@Nonnull PlayerDeathEvent event) {
    if (countNoStats()) return;
    incrementStatistic(event.getEntity(), Statistic.DEATHS, 1);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onKill(@Nonnull EntityDeathEvent event) {
    if (countNoStats()) return;
    LivingEntity entity = event.getEntity();
    Player player = entity.getKiller();
    if (player == null) return;
    if (AbstractChallenge.ignorePlayer(player)) return;

    incrementStatistic(player, Statistic.ENTITY_KILLS, 1);
    if (entity instanceof EnderDragon && entity.getWorld().getEnvironment() == Environment.THE_END) {
      dragonDamager.forEach(damager -> incrementStatistic(damager, Statistic.DRAGON_KILLED, 1));
      dragonDamager.clear();
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMove(@Nonnull PlayerMoveEvent event) {
    if (countNoStats()) return;
    if (AbstractChallenge.ignorePlayer(event.getPlayer())) return;
    if (ChallengeAPI.isPaused()) return;
    if (event.getTo() == null) return;
    if (BlockUtils.isSameBlockLocationIgnoreHeight(event.getFrom(), event.getTo())) return;
    incrementStatistic(event.getPlayer(), Statistic.BLOCKS_TRAVELED, 1);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onJump(@Nonnull PlayerJumpEvent event) {
    if (countNoStats()) return;
    if (AbstractChallenge.ignorePlayer(event.getPlayer())) return;
    if (ChallengeAPI.isPaused()) return;
    incrementStatistic(event.getPlayer(), Statistic.JUMPS, 1);
  }

  private void incrementStatistic(@Nonnull Player player, @Nonnull Statistic statistic, double amount) {
    PlayerStats stats = Challenges.getInstance().getStatsManager().getStats(player);
    stats.incrementStatistic(statistic, amount);
  }

  private boolean countNoStats() {
    return Challenges.getInstance().getServerManager().hasCheated() && Challenges.getInstance().getStatsManager().isNoStatsAfterCheating();
  }

}
