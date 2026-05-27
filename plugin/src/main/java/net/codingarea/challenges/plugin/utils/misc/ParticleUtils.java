package net.codingarea.challenges.plugin.utils.misc;

import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.commons.common.collection.IRandom;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

public final class ParticleUtils {

  private static final IRandom iRandom = IRandom.create();

  private ParticleUtils() {
  }

  private static void spawnParticleCircle(@NotNull Location location, int points, double radius, @NotNull BiConsumer<World, Location> player) {
    World world = location.getWorld();
    if (world == null) return;

    for (int i = 0; i < points; i++) {
      double angle = 2 * Math.PI * i / points;
      Location point = location.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
      player.accept(world, point);
    }
  }

  public static void spawnParticleCircle(@NotNull Location location, @NotNull Effect particle, int points, double radius) {
    spawnParticleCircle(location, points, radius, (world, point) -> world.playEffect(point, particle, 1));
  }

  public static void spawnParticleCircle(@NotNull Location location, @NotNull Particle particle, int points, double radius) {
    spawnParticleCircle(location, points, radius, (world, point) -> world.spawnParticle(particle, point, 1));
  }

  private static void spawnUpGoingParticleCircle(@NotNull JavaPlugin plugin, @NotNull Location location, int points, double radius, double height, @NotNull BiConsumer<World, Location> player) {
    for (double y = 0, i = 0; y < height; y += .25, i++) {
      final double Y = y;
      Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
        spawnParticleCircle(location.clone().add(0, Y, 0), points, radius, player);
      }, (long) i);
    }
  }

  public static void spawnUpGoingParticleCircle(@NotNull JavaPlugin plugin, @NotNull Location location, @NotNull Effect effect, int points, double radius, double height) {
    spawnUpGoingParticleCircle(plugin, location, points, radius, height, (world, point) -> world.playEffect(point, effect, 1));
  }

  public static void spawnUpGoingParticleCircle(@NotNull JavaPlugin plugin, @NotNull Location location, @NotNull Particle particle, int points, double radius, double height) {
    if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_20_5)) {
      for (double y = 0, i = 0; y < height; y += .25, i++) {
        final double Y = y;

        Color color = Color.fromRGB(
          iRandom.range(0, 255),
          iRandom.range(0, 255),
          iRandom.range(0, 255)
        );

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
          if (particle == Particle.ENTITY_EFFECT) {
            spawnParticleCircle(location.clone().add(0, Y, 0), points, radius, (world, loc) -> world.spawnParticle(particle, loc, 1, color));
          } else {
            spawnParticleCircle(location.clone().add(0, Y, 0), points, radius, (world, loc) -> world.spawnParticle(particle, loc, 1));
          }
        }, (long) i);
      }
    } else {
      spawnUpGoingParticleCircle(plugin, location, points, radius, height, (world, point) -> world.spawnParticle(particle, point, 1));
    }
  }

  public static void spawnParticleCircleAroundEntity(@NotNull JavaPlugin plugin, @NotNull Entity entity) {
    spawnParticleCircleAroundBoundingBox(plugin, entity.getLocation(), MinecraftNameWrapper.INSTANT_EFFECT, entity.getBoundingBox(), 0.25);
  }

  public static void spawnParticleCircleAroundBoundingBox(@NotNull JavaPlugin plugin, @NotNull Location location, @NotNull Particle particle, @NotNull BoundingBox box, double height) {
    spawnParticleCircleAroundRadius(plugin, location, particle, box.getWidthX(), height);
  }

  public static void spawnParticleCircleAroundRadius(@NotNull JavaPlugin plugin, @NotNull Location location, @NotNull Particle particle, double radius, double height) {
    spawnUpGoingParticleCircle(plugin, location, particle, (int) (radius * 15), radius, height);
  }

  public static void drawLine(@NotNull Player player, @NotNull Location point1, @NotNull Location point2, @NotNull Particle particle, @Nullable Particle.DustOptions dustOptions, int count, double space, int max) {
    World world = point1.getWorld();
    if (!Objects.equals(world, point2.getWorld())) return;
    double distance = point1.distance(point2);
    Vector p1 = point1.toVector();
    Vector p2 = point2.toVector();
    Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
    double length = 0;
    int current = 0;
    for (; length < distance; p1.add(vector)) {
      player.spawnParticle(particle, p1.getX(), p1.getY(), p1.getZ(), count, dustOptions);
      length += space;

      current++;
      if (current >= max) break;
    }

  }

}
