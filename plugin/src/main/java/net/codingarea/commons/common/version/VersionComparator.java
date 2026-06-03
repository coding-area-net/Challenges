package net.codingarea.commons.common.version;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class VersionComparator implements Comparator<Version> {

  public static final VersionComparator INSTANCE = new VersionComparator();

  @Override
  public int compare(@NotNull Version v1, @NotNull Version v2) {
    return v1.equals(v2) ? 0 : v1.isNewerThan(v2) ? 1 : -1;
  }

}
