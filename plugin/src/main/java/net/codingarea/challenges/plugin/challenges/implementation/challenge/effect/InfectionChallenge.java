package net.codingarea.challenges.plugin.challenges.implementation.challenge.effect;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class InfectionChallenge extends Setting {

  public InfectionChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.EFFECT, new ItemStack(Material.SLIME_BALL), "infection-challenge");
  }

  @Override
  protected void onDisable() {
    Bukkit.getOnlinePlayers().forEach(this::removeEffects);
  }

  @ScheduledTask(ticks = 10, async = false)
  public void onHalfSecond() {
    if (!shouldExecuteEffect()) {
      Bukkit.getOnlinePlayers().forEach(this::removeEffects);
      return;
    }
    Bukkit.getOnlinePlayers().forEach(this::updateSickness);
  }

  private void updateSickness(@NotNull Player player) {
    if (ignorePlayer(player)) {
      removeEffects(player);
      return;
    }

    List<Entity> entities = getNearbyTargets(player, 2);
    if (entities.isEmpty()) {
      player.removePotionEffect(MinecraftNameWrapper.NAUSEA);
      return;
    }

    byte value = 1;
    for (Entity currentEntity : entities) {
      if (player.getLocation().distance(currentEntity.getLocation()) <= 1.5) value = 2;
    }

    player.addPotionEffect(new PotionEffect(MinecraftNameWrapper.NAUSEA, Integer.MAX_VALUE, 15));
    if (value == 1) {
      if (!player.hasPotionEffect(PotionEffectType.WITHER)) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, 2));
      }
    } else {
      player.removePotionEffect(PotionEffectType.POISON);
      player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 4));
    }

  }

  private void removeEffects(@NotNull Player player) {
    player.removePotionEffect(MinecraftNameWrapper.NAUSEA);
    player.removePotionEffect(PotionEffectType.POISON);
    player.removePotionEffect(PotionEffectType.WITHER);
  }

  private List<Entity> getNearbyTargets(@NotNull Player player, double range) {
    return player.getNearbyEntities(range, range, range).stream()
      .filter(entity -> entity != player)
      .filter(entity -> entity instanceof LivingEntity)
      .filter(entity -> !(entity instanceof Player)
        || (((Player) entity).getGameMode() != GameMode.CREATIVE
        && ((Player) entity).getGameMode() != GameMode.SPECTATOR)).collect(Collectors.toList());
  }

}
