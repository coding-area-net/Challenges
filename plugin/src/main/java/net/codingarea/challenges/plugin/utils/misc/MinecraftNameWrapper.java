package net.codingarea.challenges.plugin.utils.misc;

import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import net.codingarea.commons.common.collection.pair.Tuple;
import net.codingarea.commons.common.misc.ReflectionUtils;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;

public class MinecraftNameWrapper {

  public static final Material GREEN_DYE = getMaterialByNames("CACTUS_GREEN", "GREEN_DYE");
  public static final Material RED_DYE = getMaterialByNames("ROSE_RED", "RED_DYE");
  public static final Material YELLOW_DYE = getMaterialByNames("DANDELION_YELLOW", "YELLOW_DYE");
  public static final Material SIGN = getMaterialByNames("SIGN", "OAK_SIGN");

  public static final EntityType FIREWORK = getEntityByNames("FIREWORK", "FIREWORK_ROCKET");
  public static final EntityType SNOW_GOLEM = getEntityByNames("SNOWMAN", "SNOW_GOLEM");

  public static final Particle INSTANT_EFFECT = getParticleByNames("SPELL_INSTANT", "INSTANT_EFFECT");
  public static final Particle WITCH_EFFECT = getParticleByNames("SPELL_WITCH", "WITCH");
  public static final Particle ENTITY_EFFECT = getParticleByNames("SPELL_MOB", "ENTITY_EFFECT");
  public static final Particle REDSTONE_DUST = getParticleByNames("REDSTONE", "DUST");

  public static final PotionEffectType NAUSEA = getPotionByNames("CONFUSION", "NAUSEA");
  public static final PotionEffectType INSTANT_HEALTH = getPotionByNames("HEAL", "INSTANT_HEALTH");
  public static final PotionEffectType INSTANT_DAMAGE = getPotionByNames("HARM", "INSTANT_DAMAGE");
  public static final PotionEffectType SLOWNESS = getPotionByNames("SLOW", "SLOWNESS");

  public static final Enchantment UNBREAKING = getEnchantByNames("DURABILITY", "UNBREAKING");

  public static final Attribute MAX_HEALTH = getAttributeByNames("GENERIC_MAX_HEALTH", "MAX_HEALTH");
  public static final Attribute ATTACK_SPEED = getAttributeByNames("GENERIC_ATTACK_SPEED", "ATTACK_SPEED");
  public static final Attribute LUCK = getAttributeByNames("GENERIC_LUCK", "LUCK");

  // replacement for wrapping via GameRule.getByName (marked for removal as of 1.21.11),
  // access via namespace key would be a viable alternative
  public static final GameRule<Boolean> DAYLIGHT_CYCLE = getGameRuleByNames("DO_DAYLIGHT_CYCLE", "ADVANCE_TIME");
  public static final GameRule<Boolean> WEATHER_CYCLE = getGameRuleByNames("DO_WEATHER_CYCLE", "ADVANCE_WEATHER");
  public static final GameRule<Boolean> IMMEDIATE_RESPAWN = getGameRuleByNames("DO_IMMEDIATE_RESPAWN", "IMMEDIATE_RESPAWN");
  public static final GameRule<Boolean> MOB_SPAWNING = getGameRuleByNames("DO_MOB_SPAWNING", "SPAWN_MOBS");
  public static final GameRule<Boolean> WANDERING_TRADERS = getGameRuleByNames("DO_TRADER_SPAWNING", "SPAWN_WANDERING_TRADERS");

  // the game rule for toggling raids has been inverted from "disableRaids" to "raids" in the 1.21->26.1 update
  private static final GameRule<Boolean> DISABLE_RAIDS = getFirstConstantByNamesOrNull(GameRule.class, "DISABLE_RAIDS");
  private static final GameRule<Boolean> ENABLE_RAIDS = getFirstConstantByNamesOrNull(GameRule.class, "RAIDS");

  @NotNull
  public static Tuple<GameRule<Boolean>, Boolean> getDisableRaidsGameRulePair() {
    return DISABLE_RAIDS != null ? Tuple.of(DISABLE_RAIDS, true) : Tuple.of(ENABLE_RAIDS, false);
  }

  private MinecraftNameWrapper() {
  }

  @NotNull
  public static Material getMaterialByNames(@NotNull String... names) {
    return ReflectionUtils.getFirstEnumByNames(Material.class, names);
  }

  @NotNull
  public static EntityType getEntityByNames(@NotNull String... names) {
    return ReflectionUtils.getFirstEnumByNames(EntityType.class, names);
  }

  @NotNull
  public static Particle getParticleByNames(@NotNull String... names) {
    return ReflectionUtils.getFirstEnumByNames(Particle.class, names);
  }

  @NotNull
  public static PotionEffectType getPotionByNames(@NotNull String... names) {
    return getFirstConstantByNames(PotionEffectType.class, names);
  }

  @NotNull
  public static Enchantment getEnchantByNames(@NotNull String... names) {
    return getFirstConstantByNames(Enchantment.class, names);
  }

  @NotNull
  public static Attribute getAttributeByNames(@NotNull String... names) {
    return getFirstConstantByNames(Attribute.class, names);
  }

  @NotNull
  public static <T> GameRule<T> getGameRuleByNames(@NotNull String... names) {
    return getFirstConstantByNames(GameRule.class, names);
  }

  @NotNull
  @SuppressWarnings("unchecked")
  public static <T> T getFirstConstantByNames(@NotNull Class<?> clazz, @NotNull String... names) {
    for (String name : names) {
      try {
        Field field = ReflectionUtil.getField(clazz, name);
        return (T) field.get(null);
      } catch (NoSuchFieldException | IllegalAccessException ignored) {
      }
    }
    throw new IllegalArgumentException("No attribute found in: " + clazz.getName() + " for " + Arrays.toString(names));
  }

  @Nullable
  public static <T> T getFirstConstantByNamesOrNull(@NotNull Class<?> clazz, @NotNull String... names) {
    try {
      return getFirstConstantByNames(clazz, names);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

}
