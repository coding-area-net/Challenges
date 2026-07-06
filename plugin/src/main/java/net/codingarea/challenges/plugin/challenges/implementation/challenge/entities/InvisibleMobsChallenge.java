package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class InvisibleMobsChallenge extends Setting {

  public InvisibleMobsChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.ENTITIES, new StandardItemBuilder.PotionBuilder(Material.POTION).setColor(Color.WHITE).build(),
      "invisible-mobs");
  }

  @Override
  protected void onEnable() {
    addEffectForEveryEntity();
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onSpawn(@NotNull EntitySpawnEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof LivingEntity)) return;
    addEffect(((LivingEntity) event.getEntity()));
  }

  @ScheduledTask(ticks = 20, async = false, timerPolicy = TimerPolicy.ALWAYS)
  public void playEffects() {
    if (!shouldExecuteEffect()) return;
    addEffectForEveryEntity();
  }

  private void addEffectForEveryEntity() {
    for (World world : ChallengeAPI.getGameWorlds()) {
      for (LivingEntity entity : world.getLivingEntities()) {
        if (entity instanceof Player) continue;
        addEffect(entity);
      }
    }
  }

  private void addEffect(@NotNull LivingEntity entity) {
    entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 1, true, false, false));
  }

}
