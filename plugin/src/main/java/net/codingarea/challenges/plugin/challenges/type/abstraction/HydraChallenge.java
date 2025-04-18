package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.spigot.events.EntityDeathByPlayerEvent;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.challenges.plugin.utils.misc.ParticleUtils;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;

public abstract class HydraChallenge extends Setting {

  public HydraChallenge(@Nonnull MenuType menu) {
    super(menu);
  }

  public HydraChallenge(@Nonnull MenuType menu, boolean enabledByDefault) {
    super(menu, enabledByDefault);
  }

  public abstract int getNewMobsCount(@Nonnull EntityType entityType);

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityDamageByEntity(@Nonnull EntityDeathByPlayerEvent event) {
    if (!shouldExecuteEffect()) return;
    if (event.getEntity() instanceof EnderDragon || event.getEntity() instanceof Player) return;
    if (ChallengeHelper.ignoreDamager(event.getKiller())) return;

    int mobsCount = getNewMobsCount(event.getEntityType());

    for (int i = 0; i < mobsCount; i++) {
      event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), event.getEntityType());
    }
    ParticleUtils.spawnUpGoingParticleCircle(Challenges.getInstance(), event.getEntity().getLocation(),
      MinecraftNameWrapper.ENTITY_EFFECT, 2, 17, 1);
  }

}
