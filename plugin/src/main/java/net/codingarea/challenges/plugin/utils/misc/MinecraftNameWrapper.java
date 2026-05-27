package net.codingarea.challenges.plugin.utils.misc;

import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import net.codingarea.commons.common.misc.ReflectionUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;

public class MinecraftNameWrapper {

  public static final Material GREEN_DYE = getItemByNames("CACTUS_GREEN", "GREEN_DYE");
  public static final Material RED_DYE = getItemByNames("ROSE_RED", "RED_DYE");
  public static final Material YELLOW_DYE = getItemByNames("DANDELION_YELLOW", "YELLOW_DYE");
  public static final Material SIGN = getItemByNames("SIGN", "OAK_SIGN");

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

  private MinecraftNameWrapper() {
  }

  @NotNull
  private static Material getItemByNames(@NotNull String... names) {
    return ReflectionUtils.getFirstEnumByNames(Material.class, names);
  }

  @NotNull
  private static EntityType getEntityByNames(@NotNull String... names) {
    return ReflectionUtils.getFirstEnumByNames(EntityType.class, names);
  }

  @NotNull
  private static Particle getParticleByNames(@NotNull String... names) {
    return ReflectionUtils.getFirstEnumByNames(Particle.class, names);
  }

  @NotNull
  private static PotionEffectType getPotionByNames(@NotNull String... names) {
    return getFirstAttributeByNames(PotionEffectType.class, names);
  }

  @NotNull
  private static Enchantment getEnchantByNames(@NotNull String... names) {
    return getFirstAttributeByNames(Enchantment.class, names);
  }

  @SuppressWarnings("unchecked")
  @NotNull
  public static <T> T getFirstAttributeByNames(@NotNull Class<?> clazz, @NotNull String... names) {
    for (String name : names) {
      try {
        Field field = ReflectionUtil.getField(clazz, name);
        return (T) field.get(null);
      } catch (NoSuchFieldException | IllegalAccessException ignored) {
      }
    }
    throw new IllegalArgumentException("No attribute found in: " + clazz.getName() + " for " + Arrays.toString(names));
  }

}
