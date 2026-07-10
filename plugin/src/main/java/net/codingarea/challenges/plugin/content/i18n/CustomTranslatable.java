package net.codingarea.challenges.plugin.content.i18n;

import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Provides translation keys for common minecraft values that have no client side translation key
 * (don't implement {@link net.kyori.adventure.translation.Translatable}).
 */
@ApiStatus.Experimental
public final class CustomTranslatable {

  public CustomTranslatable() {
  }

  @NotNull
  public static MessageKey of(@NotNull EntityDamageEvent.DamageCause damageCause) {
    String key = damageCause.name().toLowerCase();
    return MessageKey.of("translatable.damage-cause." + key);
  }

}
