package net.codingarea.challenges.platform.message;

import org.jetbrains.annotations.NotNull;

public interface MessageHolder {

  @NotNull
  String[] miniMessageRaw();

  @NotNull
  Object[] positionalArgs();

}
