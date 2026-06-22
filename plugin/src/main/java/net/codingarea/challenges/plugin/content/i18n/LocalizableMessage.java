package net.codingarea.challenges.plugin.content.i18n;

import net.codingarea.challenges.platform.message.MessageHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Represents a {@link MessageKey} with arguments that can be passed as a message argument.
 *
 * @see MessageKey#withArgs(Object...)
 */
public interface LocalizableMessage {

  @NotNull
  MessageHolder localize(@NotNull Locale locale);

  @NotNull
  MessageHolder localize(@NotNull Player playerLocale);

  @NotNull
  MessageKey getLocalizableKey();

  @NotNull
  Object[] getLocalizableArgs();

}
