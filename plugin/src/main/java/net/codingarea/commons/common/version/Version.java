package net.codingarea.commons.common.version;

import net.codingarea.commons.common.annotations.Since;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface Version {

  int getMajor();

  int getMinor();

  int getRevision();

  default boolean isNewerThan(@NotNull Version other) {
    return this.intValue() > other.intValue();
  }

  default boolean isNewerOrEqualThan(@NotNull Version other) {
    return this.intValue() >= other.intValue();
  }

  default boolean isOlderThan(@NotNull Version other) {
    return this.intValue() < other.intValue();
  }

  default boolean isOlderOrEqualThan(@NotNull Version other) {
    return this.intValue() <= other.intValue();
  }

  default boolean equals(@NotNull Version other) {
    return this.intValue() == other.intValue();
  }

  @NotNull
  default String format() {
    int revision = getRevision();
    return revision > 0 ? String.format("%s.%s.%s", getMajor(), getMinor(), revision)
      : String.format("%s.%s", getMajor(), getMinor());
  }

  default int intValue() {
    int major = getMajor();
    int minor = getMinor();
    int revision = getRevision();

    if (major > 99) throw new IllegalStateException("Malformed version: major is greater than 99");
    if (minor > 99) throw new IllegalStateException("Malformed version: minor is greater than 99");
    if (revision > 99) throw new IllegalStateException("Malformed version: revision is greater than 99");

    return revision
      + minor * 100
      + major * 10000;
  }

  @NotNull
  static Version parse(@Nullable String input) {
    return parse(input, new VersionInfo(1, 0, 0));
  }

  static Version parse(@Nullable String input, Version def) {
    return VersionInfo.parse(input, def);
  }

  @NotNull
  static Version parseExceptionally(@Nullable String input) {
    return VersionInfo.parseExceptionally(input);
  }

  @NotNull
  static Version getAnnotatedSince(@NotNull Object object) {
    if (!object.getClass().isAnnotationPresent(Since.class)) return new VersionInfo(1, 0, 0);
    return parse(object.getClass().getAnnotation(Since.class).value());
  }

  @NotNull
  static <V extends Version> V findNearest(@NotNull Version target, @NotNull V[] sortedVersionsArray) {
    List<V> versions = new ArrayList<>(Arrays.asList(sortedVersionsArray));
    Collections.reverse(versions);
    for (V version : versions) {
      if (version.isNewerThan(target)) continue;
      return version;
    }
    throw new IllegalArgumentException("No version found for '" + target + "'");
  }

  @NotNull
  static Comparator<Version> comparator() {
    return new VersionComparator();
  }

}
