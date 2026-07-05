package net.codingarea.challenges.plugin.content.i18n;

import net.codingarea.challenges.plugin.content.i18n.impl.DynamicLocalizableMessageImpl;
import net.codingarea.challenges.plugin.content.i18n.impl.JoinedMessageImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Represents a {@link MessageKey} with arguments that can be passed as a message argument for dynamic formatting
 * or a {@link MessageKey} instance without arguments.
 *
 * @see MessageKey#withArgs(Object...)
 */
public interface LocalizableMessage {

  @NotNull
  MessageHolder localize(@NotNull Locale locale);

  @NotNull
  MessageHolder localize(@NotNull Player playerLocale);

  @NotNull
  @ApiStatus.Internal
  MessageKey getLocalizableKey();

  @NotNull
  Object[] getLocalizableArgs();

  @NotNull
  @CheckReturnValue
  static LocalizableMessage of(@NotNull String messageKey, @NotNull Object[] args) {
    return MessageKey.of(messageKey).withArgs(args);
  }

  @NotNull
  @CheckReturnValue
  static LocalizableMessage join(@NotNull MessageKey delimiter, @NotNull Object... elements) {
    return new JoinedMessageImpl(delimiter, elements);
  }

  @NotNull
  @CheckReturnValue
  static LocalizableMessage joinArray(@NotNull Object[] elements) {
    return join(MessageKey.of("generic.delimiter"), elements);
  }

  @NotNull
  @CheckReturnValue
  static LocalizableMessage joinList(@NotNull List<?> elements) {
    return joinArray(elements.toArray());
  }

  @NotNull
  @CheckReturnValue
  static LocalizableMessage joinArgs(@NotNull Object... elements) { // prevents ambiguous var-args
    return joinArray(elements);
  }

  @NotNull
  @CheckReturnValue
  static LocalizableMessage fromLines(@NotNull Function<Locale, String[]> valueFunction, @NotNull Object... args) {
    return new DynamicLocalizableMessageImpl(valueFunction, args);
  }

  @NotNull
  @CheckReturnValue
  static LocalizableMessage from(@NotNull Function<Locale, String> valueFunction, @NotNull Object... args) {
    return new DynamicLocalizableMessageImpl(valueFunction.andThen((string) -> new String[]{string}), args);
  }

}
