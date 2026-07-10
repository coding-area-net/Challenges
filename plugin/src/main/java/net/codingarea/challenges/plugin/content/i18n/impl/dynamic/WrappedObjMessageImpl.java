package net.codingarea.challenges.plugin.content.i18n.impl.dynamic;

import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageHolder;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.impl.MessageKeyImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@ApiStatus.Internal
@ApiStatus.Experimental
public class WrappedObjMessageImpl implements LocalizableMessage {
  // TODO REMOVE: this class introduced unnecessary overhead, impacting performance

  public static final String[] VALUE = new String[]{"{0}"};

  public WrappedObjMessageImpl(@NotNull Object wrappedArray) {
    this.wrappedArray = new Object[]{wrappedArray};
  }

  private final Object[] wrappedArray;

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Locale locale) {
    return new MessageHolder(VALUE, wrappedArray);
  }

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Player playerLocale) {
    return localize(MessageKeyImpl.getPlayerLocale(playerLocale));
  }

  @NotNull
  @Override
  public LocalizableMessage withArgs(@NotNull Object... args) {
    if (args.length != 1)
      throw new IllegalArgumentException("WrappedObjMessageImpl must wrap a single object, but got " + args.length + " arguments.");
    return new WrappedObjMessageImpl(args);
  }

  @Nullable
  @Override
  public MessageKey getLocalizableKey() {
    return null;
  }

  @Override
  public @NotNull Object[] getLocalizableArgs() {
    return new Object[0];
  }
}
