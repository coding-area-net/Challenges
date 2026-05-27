package net.codingarea.commons.common.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Since {

  @NotNull
  String value();

}
