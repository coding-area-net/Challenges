package net.codingarea.challenges.plugin.challenges.type.annotation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Updated {

  @NotNull
  String value();

}
