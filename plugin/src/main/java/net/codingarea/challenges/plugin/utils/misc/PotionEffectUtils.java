package net.codingarea.challenges.plugin.utils.misc;

import org.bukkit.potion.PotionEffect;

public final class PotionEffectUtils {

  private PotionEffectUtils() {
  }

  public static final int INFINITE_DURATION;

  static {
    int value;
    try {
      // introduced in 1.19.4
      value = PotionEffect.INFINITE_DURATION;
    } catch (Error _) {
      value = Integer.MAX_VALUE;
    }
    INFINITE_DURATION = value;
  }

}
