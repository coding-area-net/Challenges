package net.codingarea.challenges.plugin.content.i18n;


import org.jetbrains.annotations.NotNull;

/**
 * Represents a localized message with arguments.
 *
 * @see LocalizableMessage#localize(java.util.Locale)
 */
public record MessageHolder(@NotNull String[] rawValue, @NotNull Object[] positionalArgs) {

  public static final Object[] EMPTY_ARGS = new Object[0];

}
