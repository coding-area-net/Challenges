package net.codingarea.challenges.plugin.content.i18n.impl.dynamic;

import lombok.RequiredArgsConstructor;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageHolder;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.impl.MessageKeyImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

import static net.codingarea.challenges.plugin.content.i18n.impl.MessageKeyImpl.arrayOf;
import static net.codingarea.challenges.plugin.content.i18n.impl.format.MessageFormatter.END_ARG_CHAR;
import static net.codingarea.challenges.plugin.content.i18n.impl.format.MessageFormatter.START_ARG_CHAR;

@RequiredArgsConstructor
public class JoinedMessageImpl implements LocalizableMessage {

  private final MessageKey delimiter;
  private final MessageKey remainingPlaceholder;
  private final int limit;
  private final Object[] elements;

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Locale locale) {
    String localizedDelimiter = delimiter.localizeRawValueAsSingleLine(locale);
    int shown = Math.min(elements.length, limit);
    int remaining = elements.length - shown;

    StringBuilder raw = new StringBuilder();
    for (int i = 0; i < shown; i++) {
      if (i > 0) raw.append(localizedDelimiter);
      raw.append(START_ARG_CHAR).append(i).append(END_ARG_CHAR);
    }
    if (remaining > 0) {
      raw.append(remainingPlaceholder.localizeWithPrimitiveArgAsSingleLine(locale, remaining));
    }

    Object[] args = new Object[shown];
    System.arraycopy(elements, 0, args, 0, shown);

    MessageKeyImpl.localizeArgs(locale, args);
    return new MessageHolder(arrayOf(raw.toString()), args);
  }

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Player playerLocale) {
    return localize(MessageKeyImpl.getPlayerLocale(playerLocale));
  }

  @NotNull
  @Override
  public LocalizableMessage withArgs(@NotNull Object... args) {
    return new JoinedMessageImpl(delimiter, remainingPlaceholder, limit, args);
  }

  @Nullable
  @Override
  public MessageKey getLocalizableKey() {
    return null;
  }

  @NotNull
  @Override
  public Object[] getLocalizableArgs() {
    return elements;
  }
}
