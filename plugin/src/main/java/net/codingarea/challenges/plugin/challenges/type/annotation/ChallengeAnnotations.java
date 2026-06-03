package net.codingarea.challenges.plugin.challenges.type.annotation;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
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

  @NotNull
  public static Version getUpdated(@NotNull Object object) {
    return getUpdated(object.getClass());
  }

  @NotNull
  public static Version getUpdated(@NotNull Class<?> clazz) {
    if (!clazz.isAnnotationPresent(Updated.class)) return Version.FALLBACK;
    return Version.parse(clazz.getAnnotation(Updated.class).value());
  }

  public static boolean isNew(@NotNull IChallenge challenge) {
    return isVersionNewer(getSince(challenge));
  }

  public static boolean isUpdated(@NotNull IChallenge challenge) {
    return isVersionNewer(getUpdated(challenge));
  }

  private static boolean isVersionNewer(@NotNull Version challengeVersion) {
    Version pluginVersion = Challenges.getInstance().getVersion();
    return challengeVersion.isNewerOrEqualThan(pluginVersion);
  }

}
