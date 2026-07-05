package net.codingarea.challenges.plugin.content.i18n.impl;

import lombok.RequiredArgsConstructor;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageHolder;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.impl.format.MessageFormatter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@RequiredArgsConstructor
public class JoinedMessageImpl implements LocalizableMessage {

  private final MessageKey delimiter;
  private final Object[] elements;

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Locale locale) {
    StringBuilder raw = new StringBuilder();
    String localizedDelimiter = delimiter.localizeRawValueAsSingleLine(locale);
    for (int i = 0; i < elements.length; i++) {
      raw.append(MessageFormatter.START_ARG_CHAR).append(i).append(MessageFormatter.END_ARG_CHAR);
      if (i != elements.length - 1) raw.append(localizedDelimiter);
    }

    Object[] args = MessageKeyImpl.localizeArgsAsCopy(locale, elements);
    return new MessageHolder(new String[]{raw.toString()}, args);
  }

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Player playerLocale) {
    return localize(MessageKeyImpl.getPlayerLocale(playerLocale));
  }

  @NotNull
  @Override
  public MessageKey getLocalizableKey() {
    return delimiter;
  }

  @NotNull
  @Override
  public Object[] getLocalizableArgs() {
    return elements;
  }
}
