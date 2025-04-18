package net.codingarea.challenges.plugin.management.scheduler;

import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@EqualsAndHashCode
public final class ScheduledFunction {

  private final Method method;
  private final Object holder;
  private final PoliciesContainer policies;

  ScheduledFunction(@Nonnull Object holder, @Nonnull Method method, @Nonnull PoliciesContainer policies) {
    this.method = method;
    this.holder = holder;
    this.policies = policies;
  }

  public void invoke() throws InvocationTargetException, IllegalAccessException {
    if (shouldInvoke())
      invokeAnyway();
  }

  public void invokeAnyway() throws InvocationTargetException, IllegalAccessException {
    method.setAccessible(true);
    method.invoke(holder);
  }

  private boolean shouldInvoke() {
    return policies.allPoliciesAreTrue(holder);
  }

  @Nonnull
  public Object getHolder() {
    return holder;
  }

  @Override
  public String toString() {
    return holder.getClass().getName() + "." + method.getName() + "()";
  }

}
