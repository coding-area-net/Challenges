package net.codingarea.challenges.plugin.content.i18n.impl;

import lombok.Getter;
import lombok.Setter;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.*;
import net.codingarea.challenges.plugin.content.i18n.impl.format.ComponentFormatter;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.management.server.TitleManager;
import net.codingarea.commons.common.collection.IRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MessageKeyImpl implements MessageKey {

  protected static final IRandom random = IRandom.create();

  @Getter
  protected final String key;

  @Getter
  @Setter
  protected Map<Locale, String[]> values = new HashMap<>();

  public MessageKeyImpl(@NotNull String key) {
    this.key = key;
  }

  public void setValue(@NotNull Locale locale, @NotNull String[] values) {
    this.values.put(locale, values);
  }

  @Override
  public void removeValue(@NotNull Locale locale) {
    values.remove(locale);
  }

  @Override
  public boolean exists() {
    return !values.isEmpty();
  }

  @Override
  public boolean isCached(@NotNull Locale locale) {
    return values.containsKey(locale);
  }

  @NotNull
  @Override
  public String[] localizeRawValue(@NotNull Locale locale) {
    String[] value = values.get(locale);
    if (value != null && value.length != 0) return value;
    return arrayOf(MessageKey.formatMissingTranslation(key, locale));
  }

  @NotNull
  @Override
  public String localizeRawValueAsSingleLine(@NotNull Locale locale) {
    String[] value = values.get(locale);
    if (value == null || value.length == 0) return MessageKey.formatMissingTranslation(key, locale);
    if (value.length == 1) return value[0];
    return String.join("\n", value);
  }

  @NotNull
  @Override
  public LocalizableMessage withArgs(@NotNull Object... args) {
    return new LocalizableMessageImpl(this, args);
  }

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Locale locale) {
    return new MessageHolder(localizeRawValue(locale), MessageHolder.EMPTY_ARGS);
  }

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Player playerLocale) {
    return localize(getPlayerLocale(playerLocale));
  }

  @NotNull
  @Override
  public MessageKey getLocalizableKey() {
    return this;
  }

  @NotNull
  @Override
  public Object[] getLocalizableArgs() {
    return MessageHolder.EMPTY_ARGS;
  }

  @Override
  public String toString() {
    return key;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof MessageKeyImpl that)) return false;
    return Objects.equals(key, that.key);
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Nullable
  protected String localizePrefix(@NotNull Locale locale, @Nullable Prefix prefix) {
    if (prefix == null) return null;
    MessageKey asKey = prefix.getKey();
    // TODO remove when legacy color impl is removed
    if (asKey.isCached(locale)) {
      return asKey.localizeRawValueAsSingleLine(locale);
    }
    return prefix.getLegacyFallback();
  }

  @NotNull
  public static Locale getPlayerLocale(@NotNull Player player) {
    return Challenges.getInstance().getTranslationManager().getLanguageProvider().getPlayerLanguage(player);
  }

  public static void localizeArgs(@NotNull Locale locale, @NotNull Object[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i] instanceof LocalizableMessage message) {
        args[i] = message.localize(locale);
      }
    }
  }

  @NotNull
  @CheckReturnValue
  public static Object[] localizeArgsAsCopy(@NotNull Locale locale, @NotNull Object[] args) {
    if (args.length == 0) return args;
    // avoid mutating original array & ensure args array is really of type Object, so MessageHolder can be stored
    Object[] localizedArgs = new Object[args.length];
    System.arraycopy(args, 0, localizedArgs, 0, args.length);
    localizeArgs(locale, localizedArgs);
    return localizedArgs;
  }

  @NotNull
  public static String[] arrayOf(@NotNull String single) {
    return new String[]{single};
  }

  // Action Implementations

  @Override
  public void send(@NotNull CommandSender target, @Nullable Prefix prefix, @NotNull Object... args) {
    if (target instanceof Player) {
      send((Player) target, prefix, args);
    } else {
      Locale locale = Challenges.getInstance().getLoaderRegistry().getFirstLoaderByClass(LanguageLoader.class)
        .map(LanguageLoader::getConfigLanguage)
        .orElse(TranslationManager.FALLBACK_LOCALE);
      target.sendMessage(asComponent(locale, prefix, args));
    }
  }

  @Override
  public void send(@NotNull Player target, @Nullable Prefix prefix, @NotNull Object... args) {
    target.sendMessage(asComponent(getPlayerLocale(target), prefix, args));
  }

  @Override
  public void sendRandom(@NotNull Player target, @Nullable Prefix prefix, @NotNull Object... args) {
    Locale locale = getPlayerLocale(target);
    localizeArgs(locale, args);
    String raw = random.choose(localizeRawValue(locale));
    target.sendMessage(ComponentFormatter.deserializeLinesWithArgs(localizePrefix(locale, prefix), arrayOf(raw), args));
  }

  @Override
  public void broadcast(@Nullable Prefix prefix, @NotNull Object... args) {
    doBroadcast(prefix, args, Player::sendMessage);
  }

  @Override
  public void broadcastRandom(@Nullable Prefix prefix, @NotNull Object... args) {
    int index = random.nextInt(getMinValueLengthIncludingFallback());
    doBroadcast0(Player::sendMessage, locale ->
      ComponentFormatter.deserializeLinesWithArgs(localizePrefix(locale, prefix), arrayOf(localizeRawValue(locale)[index]), localizeArgsAsCopy(locale, args)));
  }

  @Override
  public void sendTitle(@NotNull Player target, @NotNull Object... args) {
    target.showTitle(createTitleFromComponentList(asComponents(target, args),
      Title.Times.times(Ticks.duration(TitleManager.FADEIN), Ticks.duration(TitleManager.DURATION), Ticks.duration(TitleManager.FADEOUT))));
  }

  @Override
  public void sendTitleInstantly(@NotNull Player target, @NotNull Object... args) {
    target.showTitle(createTitleFromComponentList(asComponents(target, args),
      Title.Times.times(Duration.ZERO, Ticks.duration(TitleManager.DURATION), Ticks.duration(TitleManager.FADEOUT))));
  }

  @Override
  public void broadcastTitle(@NotNull Object... args) {
    doBroadcastAsList(args, (player, components) ->
      player.showTitle(createTitleFromComponentList(components,
        Title.Times.times(Ticks.duration(TitleManager.FADEIN), Ticks.duration(TitleManager.DURATION), Ticks.duration(TitleManager.FADEOUT))))
    );
  }

  @Override
  public void broadcastTitleInstantly(@NotNull Object... args) {
    doBroadcastAsList(args, (player, components) ->
      player.showTitle(createTitleFromComponentList(components,
        Title.Times.times(Duration.ZERO, Ticks.duration(TitleManager.DURATION), Ticks.duration(TitleManager.FADEOUT))))
    );
  }

  @Override
  public void sendActionBar(@NotNull Player target, @NotNull Object... args) {
    target.sendActionBar(asComponent(getPlayerLocale(target), null, args));
  }

  @Override
  public void broadcastActionBar(@NotNull Object... args) {
    doBroadcast(null, args, Player::sendActionBar);
  }

  @NotNull
  protected Title createTitleFromComponentList(@NotNull List<Component> components, @NotNull Title.Times timing) {
    return Title.title(!components.isEmpty() ? components.getFirst() : Component.empty(),
      components.size() > 1 ? components.get(1) : Component.empty(), timing);
  }

  protected void doBroadcast(@Nullable Prefix prefix, @NotNull Object[] args, @NotNull BiConsumer<Player, Component> messageSender) {
    doBroadcast0(messageSender, locale ->
      ComponentFormatter.deserializeLinesWithArgs(localizePrefix(locale, prefix), localizeRawValue(locale), localizeArgsAsCopy(locale, args)));
  }

  protected void doBroadcastAsList(@NotNull Object[] args, @NotNull BiConsumer<Player, List<Component>> messageSender) {
    doBroadcast0(messageSender, locale ->
      ComponentFormatter.deserializeLinesAsListWithArgs(localizeRawValue(locale), localizeArgsAsCopy(locale, args)));
  }

  protected <T> void doBroadcast0(@NotNull BiConsumer<Player, T> messageSender, @NotNull Function<Locale, T> compute) {
    Collection<? extends Player> targets = Bukkit.getOnlinePlayers();
    if (targets.isEmpty()) return;
    if (targets.size() == 1) { // skip overhead
      Player player = targets.iterator().next();
      messageSender.accept(player, compute.apply(getPlayerLocale(player)));
      return;
    }

    Map<Locale, T> localizedCache = new HashMap<>();
    for (Player target : targets) {
      Locale locale = getPlayerLocale(target);
      T formatted = localizedCache.computeIfAbsent(locale, compute);
      messageSender.accept(target, formatted);
    }
  }

  protected int getMinValueLengthIncludingFallback() {
    if (values.isEmpty()) return 1; // fallback message
    Iterator<String[]> iterator = values.values().iterator();
    int min = iterator.next().length;
    while (iterator.hasNext()) {
      int length = iterator.next().length;
      if (length == 0) return 1; // fallback message
      if (length < min) min = length;
    }
    return min; // > 1
  }

  @NotNull
  @Override
  public Component asComponent(@NotNull Locale locale, @Nullable Prefix prefix, @NotNull Object... args) {
    localizeArgs(locale, args);
    return ComponentFormatter.deserializeLinesWithArgs(localizePrefix(locale, prefix), localizeRawValue(locale), args);
  }

  @NotNull
  @Override
  public List<Component> asComponents(@NotNull Locale locale, @NotNull Object... args) {
    localizeArgs(locale, args);
    return ComponentFormatter.deserializeLinesAsListWithArgs(localizeRawValue(locale), args);
  }

}
