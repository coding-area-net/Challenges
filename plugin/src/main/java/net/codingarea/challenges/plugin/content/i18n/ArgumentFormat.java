package net.codingarea.challenges.plugin.content.i18n;

import lombok.RequiredArgsConstructor;
import net.codingarea.commons.common.collection.NumberFormatter;
import net.codingarea.commons.common.misc.StringUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface ArgumentFormat<T> {

  @NotNull
  LocalizableMessage apply(@NotNull T arg);

  boolean isApplicable(@NotNull Object arg);

  ArgumentFormat<Number> HEARTS = register(Number.class, "hearts", arg -> {
    double hearts = arg.doubleValue() / 2;
    return MessageKey.of("arg-format.hearts").withArgs(NumberFormatter.FLOATING_POINT.format(hearts));
  });
  ArgumentFormat<Number> HEARTS_LIMITED = register(Number.class, "hearts_limited", arg -> {
    if (arg.doubleValue() >= 1000) return MessageKey.of("symbol.infinity");
    return HEARTS.apply(arg);
  });

  ArgumentFormat<Number> HP = register(Number.class, "hp", arg -> {
    int hp = arg.intValue();
    double hearts = hp / 2d;
    return MessageKey.of("arg-format.hp").withArgs(hp, NumberFormatter.FLOATING_POINT.format(hearts));
  });

  ArgumentFormat<EntityDamageEvent> DAMAGE_CAUSE = register(EntityDamageEvent.class, "damage_cause", event -> {
    if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) return MessageKey.of("generic.undefined");
    String cause = StringUtils.getEnumName(event.getCause()); // DamageCause is not a Translatable, impl custom translations?

    if (!(event instanceof EntityDamageByEntityEvent damageEvent)) {
      return MessageKey.of("arg-format.damage-cause").withArgs(cause);
    }

    if (damageEvent.getDamager() instanceof Player damagerPlayer) {
      return MessageKey.of("arg-format.damage-cause-source").withArgs(cause, damagerPlayer);
    } else if (damageEvent.getDamager() instanceof Projectile projectile) {
      ProjectileSource shooter = projectile.getShooter();
      if (shooter instanceof Player shooterPlayer) {
        return MessageKey.of("arg-format.damage-cause-source").withArgs(projectile.getType(), shooterPlayer);
      } else if (shooter instanceof Entity shooterEntity) {
        return MessageKey.of("arg-format.damage-cause-source").withArgs(projectile.getType(), shooterEntity.getType());
      } else {
        return MessageKey.of("arg-format.damage-cause").withArgs(projectile.getType());
      }
    } else {
      return MessageKey.of("arg-format.damage-cause-source").withArgs(cause, damageEvent.getDamager().getType());
    }
  });

  @NotNull
  static <T> ArgumentFormat<T> register(@NotNull Class<T> argClass, @NotNull String key, @NotNull Function<T, LocalizableMessage> formatter) {
    ArgumentFormatImpl<T> format = new ArgumentFormatImpl<>(argClass, formatter);
    ArgumentFormatImpl.REGISTRY.put(key, format);
    return format;
  }

  @Nullable
  static ArgumentFormat<?> getByName(@NotNull String name) {
    return ArgumentFormatImpl.REGISTRY.get(name);
  }

  @RequiredArgsConstructor
  class ArgumentFormatImpl<T> implements ArgumentFormat<T> {

    private static final Map<String, ArgumentFormat<?>> REGISTRY = new HashMap<>();

    private final Class<T> argClass;
    private final Function<T, LocalizableMessage> formatter;

    @NotNull
    @Override
    public LocalizableMessage apply(@NonNull T arg) {
      return formatter.apply(arg);
    }

    @Override
    public boolean isApplicable(@NotNull Object arg) {
      return argClass.isAssignableFrom(arg.getClass());
    }
  }

}
