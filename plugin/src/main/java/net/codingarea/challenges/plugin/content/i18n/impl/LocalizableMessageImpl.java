package net.codingarea.challenges.plugin.content.i18n.impl;

import lombok.RequiredArgsConstructor;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageHolder;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@RequiredArgsConstructor
public class LocalizableMessageImpl implements LocalizableMessage {

  private final MessageKeyImpl messageKey;
  private final Object[] args;

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Locale locale) {
    return new MessageHolder(messageKey.localizeWithPrimitiveArgs(locale, args), MessageKeyImpl.localizeArgsAsCopy(locale, args));
  }

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Player playerLocale) {
    return localize(MessageKeyImpl.getPlayerLocale(playerLocale));
  }

  @NotNull
  @Override
  public LocalizableMessage withArgs(@NotNull Object... args) {
    return new LocalizableMessageImpl(messageKey, args);
  }

  @NotNull
  @Override
  public MessageKey getLocalizableKey() {
    return messageKey;
  }

  @NotNull
  @Override
  public Object[] getLocalizableArgs() {
    return args;
  }
}
