package net.codingarea.challenges.plugin.content.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Represents a localized message with arguments.
 *
 * @see LocalizableMessage#localize(java.util.Locale)
 */
public record MessageHolder(@NotNull String[] rawValue, @NotNull Object[] positionalArgs) {

  public static final Object[] EMPTY_ARGS = new Object[0];

  @NotNull
  @Override
  public String toString() {
    return "MessageHolder{" +
      "raw=" + Arrays.toString(rawValue) +
      ", args=" + Arrays.toString(positionalArgs) +
      '}';
  }
}
