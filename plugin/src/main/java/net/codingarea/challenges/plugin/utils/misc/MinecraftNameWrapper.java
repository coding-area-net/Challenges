package net.codingarea.challenges.plugin.utils.misc;

import net.anweisen.utilities.common.misc.ReflectionUtils;
import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
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

  @Nonnull
  private static Material getItemByNames(@Nonnull String... names) {
    return ReflectionUtils.getFirstEnumByNames(Material.class, names);
  }

  @Nonnull
  private static EntityType getEntityByNames(@Nonnull String... names) {
    return ReflectionUtils.getFirstEnumByNames(EntityType.class, names);
  }

  @Nonnull
  private static Particle getParticleByNames(@Nonnull String... names) {
    return ReflectionUtils.getFirstEnumByNames(Particle.class, names);
  }

  @Nonnull
  private static PotionEffectType getPotionByNames(@Nonnull String... names) {
    return getFirstAttributeByNames(PotionEffectType.class, names);
  }

  @Nonnull
  private static Enchantment getEnchantByNames(@Nonnull String... names) {
    return getFirstAttributeByNames(Enchantment.class, names);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public static <T> T getFirstAttributeByNames(@Nonnull Class<?> clazz, @Nonnull String... names) {
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
