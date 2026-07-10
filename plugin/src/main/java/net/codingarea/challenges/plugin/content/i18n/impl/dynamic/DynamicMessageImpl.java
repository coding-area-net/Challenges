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
import java.util.function.Function;

@RequiredArgsConstructor
public class DynamicMessageImpl implements LocalizableMessage {

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
  public LocalizableMessage withArgs(@NotNull Object... args) {
    return new DynamicMessageImpl(valueFunction, args);
  }

  @Nullable
  @Override
  public MessageKey getLocalizableKey() {
    return null;
  }

  @NotNull
  @Override
  public Object[] getLocalizableArgs() {
    return args;
  }
}
