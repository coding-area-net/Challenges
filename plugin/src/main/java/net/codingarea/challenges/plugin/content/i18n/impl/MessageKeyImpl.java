package net.codingarea.challenges.plugin.content.i18n.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.codingarea.challenges.platform.message.MessageHolder;
import net.codingarea.challenges.platform.message.MessagePlatform;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.TranslationManager;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.management.server.TitleManager;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;
import net.codingarea.commons.common.collection.IRandom;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;

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
  public boolean isCached(@NotNull Locale locale) {
    return values.containsKey(locale);
  }

  @NotNull
  @Override
  public String[] localizeRawValue(@NotNull Locale locale) {
    String[] value = values.get(locale);
    if (value != null && value.length != 0) return value;
    return new String[]{MessageKey.formatMissingTranslation(key, locale)};
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
    return new LocalizableMessageImpl(args);
  }

  @NotNull
  @Override
  public MessageHolder localize(@NotNull Locale locale) {
    return new MessageHolderImpl(localizeRawValue(locale), MessageHolderImpl.EMPTY_ARGS);
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
    return MessageHolderImpl.EMPTY_ARGS;
  }

  protected void localizeArgs(@NotNull Locale locale, @NotNull Object[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i] instanceof LocalizableMessage message) {
        args[i] = message.localize(locale);
      }
    }
  }

  @CheckReturnValue
  protected Object[] localizeArgsAsCopy(@NotNull Locale locale, @NotNull Object[] args) {
    if (args.length == 0) return args;
    Object[] localizedArgs = Arrays.copyOf(args, args.length); // avoid mutating original array
    localizeArgs(locale, localizedArgs);
    return localizedArgs;
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
  protected String stringifyPrefix(@NotNull Locale locale, @Nullable Prefix prefix) {
    if (prefix == null) return null;
    MessageKey asKey = prefix.getKey();
    if (asKey.isCached(locale)) {
      return asKey.localizeRawValueAsSingleLine(locale);
    }
    return prefix.getLegacyFallback();
  }

  @NotNull
  protected Locale getPlayerLocale(@NotNull Player player) {
    return Challenges.getInstance().getTranslationManager().getLanguageProvider().getPlayerLanguage(player);
  }

  @NotNull
  protected MessagePlatform getMessagePlatform() {
    return Challenges.getInstance().getPlatformManager().getMessagePlatform();
  }


  // Action Implementations

  @Override
  public void send(@NotNull CommandSender target, @Nullable Prefix prefix, @NonNull @NotNull Object... args) {
    if (target instanceof Player) {
      send((Player) target, prefix, args);
    } else {
      Locale locale = Challenges.getInstance().getLoaderRegistry().getFirstLoaderByClass(LanguageLoader.class)
        .map(LanguageLoader::getConfigLanguage)
        .orElse(TranslationManager.FALLBACK_LOCALE);
      localizeArgs(locale, args);
      getMessagePlatform().sendSenderMessage(target, stringifyPrefix(locale, prefix), localizeRawValue(locale), args);
    }
  }

  @Override
  public void send(@NotNull Player target, @Nullable Prefix prefix, @NotNull Object... args) {
    Locale locale = getPlayerLocale(target);
    localizeArgs(locale, args);
    getMessagePlatform().sendChatMessage(target, stringifyPrefix(locale, prefix), localizeRawValue(locale), args);
  }

  @Override
  public void sendRandom(@NotNull Player target, @Nullable Prefix prefix, @NotNull Object... args) {
    Locale locale = getPlayerLocale(target);
    localizeArgs(locale, args);
    String raw = random.choose(localizeRawValue(locale));
    getMessagePlatform().sendChatMessage(target, stringifyPrefix(locale, prefix), new String[]{raw}, args);
  }

  @Override
  public void broadcast(@Nullable Prefix prefix, @NotNull Object... args) {
    doBroadcast(prefix, args, getMessagePlatform()::sendChatMessage);
  }

  @Override
  public void broadcastRandom(@Nullable Prefix prefix, @NotNull Object... args) {
    int index = random.nextInt(getMinValueLengthIncludingFallback());
    doBroadcast(prefix, args, (target, localizedPrefix, raw, localizedArgs) ->
      getMessagePlatform().sendChatMessage(target, localizedPrefix, new String[]{raw[index]}, localizedArgs));
  }

  @Override
  public void sendTitle(@NotNull Player target, @NonNull @NotNull Object... args) {
    Locale locale = getPlayerLocale(target);
    localizeArgs(locale, args);
    getMessagePlatform().sendTitle(target, localizeRawValue(locale), args, TitleManager.FADEIN, TitleManager.DURATION, TitleManager.FADEOUT);
  }

  @Override
  public void sendTitleInstantly(@NotNull Player target, @NonNull @NotNull Object... args) {
    Locale locale = getPlayerLocale(target);
    localizeArgs(locale, args);
    getMessagePlatform().sendTitle(target, localizeRawValue(locale), args, 0, TitleManager.DURATION, TitleManager.FADEOUT);
  }

  @Override
  public void broadcastTitle(@NotNull Object... args) {
    doBroadcast(null, args, (target, _, raw, localizedArgs) ->
      getMessagePlatform().sendTitle(target, raw, localizedArgs, TitleManager.FADEIN, TitleManager.DURATION, TitleManager.FADEOUT));
  }

  @Override
  public void broadcastTitleInstantly(@NonNull @NotNull Object... args) {
    doBroadcast(null, args, (target, _, raw, localizedArgs) ->
      getMessagePlatform().sendTitle(target, raw, localizedArgs, 0, TitleManager.DURATION, TitleManager.FADEOUT));
  }

  @Override
  public void sendActionBar(@NotNull Player target, @NotNull Object... args) {
    Locale locale = getPlayerLocale(target);
    localizeArgs(locale, args);
    getMessagePlatform().sendActionBar(target, localizeRawValueAsSingleLine(locale), args);
  }

  @Override
  public void broadcastActionBar(@NotNull Object... args) {
    // array will never be empty
    doBroadcast(null, args, (target, _, raw, localizedArgs) ->
      getMessagePlatform().sendActionBar(target, raw[0], localizedArgs));
  }

  @NotNull
  @Override
  public Inventory createInventory(@NotNull Locale locale, int size, @NotNull Object... args) {
    localizeArgs(locale, args);
    return getMessagePlatform().createInventory(MenuPosition.HOLDER, size, localizeRawValueAsSingleLine(locale), args);
  }

  @Override
  public @NotNull Inventory createInventory(@NotNull Locale locale, @NotNull InventoryType type, @NotNull Object... args) {
    localizeArgs(locale, args);
    return getMessagePlatform().createInventory(MenuPosition.HOLDER, type, localizeRawValueAsSingleLine(locale), args);
  }

  protected void doBroadcast(@Nullable Prefix prefix, @NotNull Object[] args, @NotNull SendMessageConsumer messageSender) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      Locale locale = getPlayerLocale(player);
      messageSender.accept(player, stringifyPrefix(locale, prefix), localizeRawValue(locale), localizeArgsAsCopy(locale, args));
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


  // Item Implementations

  @Override
  public void applyAsItemNameAndLore(@NotNull Locale locale, @NotNull ItemMeta item, @NotNull Object... args) {
    localizeArgs(locale, args);

    String[] rawValue = localizeRawValue(locale); // length >= 1
    if (rawValue.length == 1) {
      applyAsItemName(locale, item, args);
      return;
    }

    String nameLine = rawValue[0];
    getMessagePlatform().applyItemName(item, nameLine, args);

    String[] loreLines = Arrays.copyOfRange(rawValue, 1, rawValue.length);
    getMessagePlatform().applyItemLore(item, loreLines, args);
  }

  @Override
  public void applyAsItemName(@NotNull Locale locale, @NotNull ItemMeta item, @NotNull Object... args) {
    localizeArgs(locale, args);
    getMessagePlatform().applyItemName(item, localizeRawValueAsSingleLine(locale), args);
  }

  @Override
  public void appendToItemName(@NotNull Locale locale, @NotNull ItemMeta item, boolean withSpace, @NotNull Object... args) {
    String rawValue = localizeRawValueAsSingleLine(locale);
    if (withSpace) rawValue = " " + rawValue;
    getMessagePlatform().appendItemName(item, rawValue, args);
  }

  @Override
  public void applyAsItemLore(@NotNull Locale locale, @NotNull ItemMeta item, @NotNull Object... args) {
    localizeArgs(locale, args);
    getMessagePlatform().applyItemLore(item, localizeRawValue(locale), args);
  }

  @Override
  public void appendToItemLore(@NotNull Locale locale, @NotNull ItemMeta item, @NotNull Object... args) {
    localizeArgs(locale, args);
    getMessagePlatform().appendItemLore(item, localizeRawValue(locale), args);
  }

  @FunctionalInterface
  public interface SendMessageConsumer {
    void accept(@NotNull Player target, @Nullable String localizedPrefix, @NotNull String[] raw, @NotNull Object[] localizedArgs);
  }

  @AllArgsConstructor
  public class LocalizableMessageImpl implements LocalizableMessage {

    private final Object[] args;

    @NotNull
    @Override
    public MessageHolder localize(@NotNull Locale locale) {
      return new MessageHolderImpl(localizeRawValue(locale), localizeArgsAsCopy(locale, args));
    }

    @NotNull
    @Override
    public MessageHolder localize(@NotNull Player playerLocale) {
      return localize(getPlayerLocale(playerLocale));
    }

    @Override
    public @NotNull MessageKey getLocalizableKey() {
      return MessageKeyImpl.this;
    }

    @NotNull
    @Override
    public Object[] getLocalizableArgs() {
      return args;
    }
  }

  public record MessageHolderImpl(String[] miniMessageRaw, Object[] positionalArgs) implements MessageHolder {
    public static final Object[] EMPTY_ARGS = new Object[0];
  }

}
