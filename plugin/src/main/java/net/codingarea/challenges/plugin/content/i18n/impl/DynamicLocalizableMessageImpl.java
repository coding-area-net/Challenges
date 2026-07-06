package net.codingarea.challenges.plugin.content.i18n.impl;

import lombok.RequiredArgsConstructor;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageHolder;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Function;

@RequiredArgsConstructor
public class DynamicLocalizableMessageImpl implements LocalizableMessage {

  public static final MessageKey KEY = MessageKey.empty("<dynamic>");

  private final Function<Locale, String[]> valueFunction;
  private final Object[] args;

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Locale locale) {
    return new MessageHolder(valueFunction.apply(locale), MessageKeyImpl.localizeArgsAsCopy(locale, args));
  }

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Player playerLocale) {
    return localize(MessageKeyImpl.getPlayerLocale(playerLocale));
  }

  @NotNull
  @Override
  public MessageKey getLocalizableKey() {
    return KEY;
  }

  @NotNull
  @Override
  public Object[] getLocalizableArgs() {
    return args;
  }
}
