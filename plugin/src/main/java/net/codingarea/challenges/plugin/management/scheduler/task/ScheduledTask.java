package net.codingarea.challenges.plugin.management.scheduler.task;

import net.codingarea.challenges.plugin.management.scheduler.policy.*;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledTask {

  @Nonnegative
  int ticks();

  boolean async() default true;

  @Nonnull
  TimerPolicy timerPolicy() default TimerPolicy.STARTED;

  @Nonnull
  ChallengeStatusPolicy challengePolicy() default ChallengeStatusPolicy.ENABLED;

  @Nonnull
  PlayerCountPolicy playerPolicy() default PlayerCountPolicy.SOMEONE;

  @Nonnull
  ExtraWorldPolicy worldPolicy() default ExtraWorldPolicy.NOT_USED;

  @Nonnull
  FreshnessPolicy freshnessPolicy() default FreshnessPolicy.ALWAYS;

}
