package net.codingarea.challenges.plugin.utils.bukkit.misc.wrapper;

import net.codingarea.challenges.plugin.utils.bukkit.misc.version.MinecraftVersion;
import org.bukkit.attribute.Attribute;

public class AttributeWrapper {

  public static final Attribute MAX_HEALTH = wrap("MAX_HEALTH", "GENERIC_MAX_HEALTH"),
    ATTACK_SPEED = wrap("ATTACK_SPEED", "GENERIC_ATTACK_SPEED");

  public static Attribute wrap(String name, String legacyName) {
    return wrap(name, legacyName, MinecraftVersion.V1_21_2);
  }

  public static Attribute wrap(String name, String legacyName, MinecraftVersion since) {
    if (MinecraftVersion.current().isNewerOrEqualThan(since)) {
      return Attribute.valueOf(name);
    } else {
      return Attribute.valueOf(legacyName);
    }
  }

}
