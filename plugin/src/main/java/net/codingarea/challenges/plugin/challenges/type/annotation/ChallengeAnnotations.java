package net.codingarea.challenges.plugin.challenges.type.annotation;

import net.codingarea.commons.common.version.Version;
import org.jetbrains.annotations.NotNull;

public final class ChallengeAnnotations {

  private ChallengeAnnotations() {
  }

  @NotNull
  public static Version getSince(@NotNull Object object) {
    return getSince(object.getClass());
  }

  @NotNull
  public static Version getSince(@NotNull Class<?> clazz) {
    if (!clazz.isAnnotationPresent(Since.class)) return Version.FALLBACK;
    return Version.parse(clazz.getAnnotation(Since.class).value());
  }
}
