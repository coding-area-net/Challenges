package net.codingarea.challenges.plugin.management.scheduler.policy;

import javax.annotation.Nonnull;

public interface IPolicy {

  boolean check(@Nonnull Object holder);

  default boolean isApplicable(@Nonnull Object holder) {
    return true;
  }

}
