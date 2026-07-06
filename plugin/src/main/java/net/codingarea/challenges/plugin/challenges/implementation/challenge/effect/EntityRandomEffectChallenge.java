package net.codingarea.challenges.plugin.challenges.implementation.challenge.effect;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Since("2.1.2")
public class EntityRandomEffectChallenge extends Setting {

  public EntityRandomEffectChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.EFFECT, new ItemStack(Material.PHANTOM_MEMBRANE), "entity-effect");
  }

  @Override
  protected void onEnable() {
    for (World world : ChallengeAPI.getGameWorlds()) {
      for (LivingEntity entity : world.getLivingEntities()) {
        addRandomEffect(entity);
      }
    }
  }

  @Override
  protected void onDisable() {
    for (World world : ChallengeAPI.getGameWorlds()) {
      for (LivingEntity entity : world.getLivingEntities()) {
        if (entity.getType() == EntityType.PLAYER) continue;
        for (PotionEffect effect : entity.getActivePotionEffects()) {
          entity.removePotionEffect(effect.getType());
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onSpawn(EntitySpawnEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof LivingEntity)) return;
    addRandomEffect((LivingEntity) event.getEntity());
  }

  public void addRandomEffect(LivingEntity entity) {
    if (entity.getType() == EntityType.PLAYER) return;
    PotionEffectType[] types = PotionEffectType.values();
    PotionEffectType type = globalRandom.choose(types);
    entity.addPotionEffect(type.createEffect(Integer.MAX_VALUE, 255));
  }

}
