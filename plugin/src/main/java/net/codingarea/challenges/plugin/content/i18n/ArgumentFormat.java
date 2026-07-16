package net.codingarea.challenges.plugin.content.i18n;

import lombok.RequiredArgsConstructor;
import net.codingarea.commons.common.collection.NumberFormatter;
import net.codingarea.commons.common.collection.pair.Tuple;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface ArgumentFormat<T> extends Function<T, LocalizableMessage> {

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

  @SuppressWarnings({"unchecked", "rawtypes"})
  ArgumentFormat<Tuple> SECONDS_RANGE = register(Tuple.class, "time_range", arg -> {
    Tuple<Integer, Integer> range = (Tuple<Integer, Integer>) arg;
    int seconds = range.getFirst();
    int secondsRange = range.getSecond();

    if (seconds % 15 == 0) {
      float minutes = seconds / 60f;
      return MessageKey.of("arg-format.time-range").withArgs(
        NumberFormatter.DEFAULT.format(minutes), MessageKey.pluralize("generic.minute", minutes),
        NumberFormatter.DEFAULT.format(secondsRange), MessageKey.pluralize("generic.second", secondsRange));
    }

    return MessageKey.of("arg-format.time-range").withArgs(
      NumberFormatter.DEFAULT.format(seconds), MessageKey.pluralize("generic.second", seconds),
      NumberFormatter.DEFAULT.format(secondsRange), MessageKey.pluralize("generic.second", secondsRange));
  });

  ArgumentFormat<Number> TIME = register(Number.class, "time", arg -> {
    long time = arg.longValue();
    long s = time % 60;
    long m = (time / 60) % 60;
    long h = (time / 3_600) % 24;
    long d = time / 86_400;

    List<LocalizableMessage> components = new LinkedList<>();
    if (s > 0 || time == 0) { // show 0s
      components.add(MessageKey.of("arg-format.time").withArgs(s, MessageKey.pluralize("generic.second", s)));
    }
    if (m > 0) {
      components.addFirst(MessageKey.of("arg-format.time").withArgs(m, MessageKey.pluralize("generic.minute", m)));
    }
    if (h > 0) {
      components.addFirst(MessageKey.of("arg-format.time").withArgs(h, MessageKey.pluralize("generic.hour", h)));
    }
    if (d > 0) {
      components.addFirst(MessageKey.of("arg-format.time").withArgs(d, MessageKey.pluralize("generic.day", d)));
    }

    return LocalizableMessage.joinList(components);
  });

  ArgumentFormat<EntityDamageEvent> DAMAGE_CAUSE = register(EntityDamageEvent.class, "damage_cause", event -> {
    if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) return MessageKey.of("generic.undefined");
    MessageKey cause = CustomTranslatable.of(event.getCause());

    if (event instanceof EntityDamageByBlockEvent damageEvent) {
      if (damageEvent.getDamager() != null) {
        return MessageKey.of("arg-format.damage-cause-source").withArgs(cause, damageEvent.getDamager().getType());
      }
    }

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
    } else if (damageEvent.getDamager() instanceof TNTPrimed tntPrimed) {
      Entity source = tntPrimed.getSource();
      if (source instanceof Player sourcePlayer) {
        return MessageKey.of("arg-format.damage-cause-source").withArgs(tntPrimed.getType(), sourcePlayer);
      } else if (source instanceof Entity sourceEntity) {
        return MessageKey.of("arg-format.damage-cause-source").withArgs(tntPrimed.getType(), sourceEntity.getType());
      } else {
        return MessageKey.of("arg-format.damage-cause").withArgs(cause, tntPrimed.getType());
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
    public LocalizableMessage apply(@NotNull T arg) {
      return formatter.apply(arg);
    }

    @Override
    public boolean isApplicable(@NotNull Object arg) {
      return argClass.isAssignableFrom(arg.getClass());
    }
  }

}
