package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomizedHPChallenge extends SettingModifier {

  private final Random random = new Random();

  public RandomizedHPChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.RANDOMIZER, 5, new StandardItemBuilder.PotionBuilder(Material.POTION).setColor(Color.RED).build(),
      "randomized-hp");
    randomizeExistingEntityHealth();
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-max-health-description").asArray(getValue() * 50);
//  }

  @Override
  protected void onDisable() {
    resetExistingEntityHealth();
  }

  @Override
  public void onEnable() {
    randomizeExistingEntityHealth();
  }

  @Override
  protected void onValueChange() {
    randomizeExistingEntityHealth();
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onSpawn(@NotNull EntitySpawnEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof LivingEntity)) return;
    LivingEntity entity = (LivingEntity) event.getEntity();
    randomizeEntityHealth(entity);
  }

  private void randomizeEntityHealth(@NotNull LivingEntity entity) {
    if (entity instanceof Player) return;

    AttributeInstance attribute = entity.getAttribute(MinecraftNameWrapper.MAX_HEALTH);
    if (attribute == null) return;

    if (!isEnabled()) {
      attribute.setBaseValue(attribute.getDefaultValue());
      entity.setHealth(attribute.getDefaultValue());
//      entity.resetMaxHealth();
//      entity.setHealth(entity.getMaxHealth());
      return;
    }

    int health = random.nextInt(getValue() * 100) + 1;
    attribute.setBaseValue(health);
    entity.setHealth(health);
  }

  private void randomizeExistingEntityHealth() {
    for (World world : ChallengeAPI.getGameWorlds()) {
      for (LivingEntity entity : world.getLivingEntities()) {
        randomizeEntityHealth(entity);
      }
    }
  }

  private void resetExistingEntityHealth() {
    Map<EntityType, Double> entityDefaultHealth = new HashMap<>();
    for (World world : ChallengeAPI.getGameWorlds()) {
      for (LivingEntity entity : world.getLivingEntities()) {
        if (entity instanceof Player) continue;
        EntityType type = entity.getType();
        double health = entityDefaultHealth.getOrDefault(type, getDefaultHealth(type));
        entityDefaultHealth.put(type, health);

        AttributeInstance attribute = entity.getAttribute(MinecraftNameWrapper.MAX_HEALTH);
        if (attribute == null) return;
        attribute.setBaseValue(health);
        entity.setHealth(health);
      }
    }
  }

  private double getDefaultHealth(@NotNull EntityType entityType) {
    World world = ChallengeAPI.getGameWorld(Environment.NORMAL);
    Entity entity = world.spawnEntity(new Location(world, 0, 0, 0), entityType);
    entity.remove();
    if (!(entity instanceof LivingEntity)) return 0;
    AttributeInstance attribute = ((LivingEntity) entity).getAttribute(MinecraftNameWrapper.MAX_HEALTH);
    if (attribute == null) return 10;
    return attribute.getBaseValue();
  }

  @NotNull
  @Override
  public ItemStack getSettingsItemPreset() {
    return DefaultItems.createEnabledValuePreset(getValue() * 5);
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeHeartsValueChangeTitle(this, getValue() * 100);
  }

}
