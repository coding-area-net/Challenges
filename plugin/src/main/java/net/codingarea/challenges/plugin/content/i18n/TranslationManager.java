package net.codingarea.challenges.plugin.content.i18n;

import com.google.common.base.Preconditions;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.impl.MessageKeyImpl;
import net.codingarea.challenges.plugin.content.i18n.impl.format.MessageFormatter;
import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.misc.ReflectionUtils;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TranslationManager {

  public static final Locale FALLBACK_LOCALE = Locale.ENGLISH;

  // default: populated by LanguageLoader
  // message name/key -> value obj
  protected final Map<String, MessageKeyImpl> cachedMessages = new ConcurrentHashMap<>();
  protected final Set<Locale> cachedLanguages = new HashSet<>();

  protected LanguageProvider languageProvider; // by default injected by LanguageLoader

  @NotNull
  public LanguageProvider getLanguageProvider() {
    if (languageProvider == null)
      throw new IllegalStateException("LanguageProvider not yet set, probably accessed before LanguageLoader has been called!");
    return languageProvider;
  }

  public boolean isLanguageProviderInitialized() {
    // will only ever be null during onLoad cycle
    return languageProvider != null;
  }

  public void setLanguageProvider(@NotNull LanguageProvider languageProvider) {
    Preconditions.checkNotNull(languageProvider, "Cannot set LanguageProvider to null");
    this.languageProvider = languageProvider;
  }

  @NotNull
  public MessageKey getMessageKey(@NotNull String key) {
    return cachedMessages.computeIfAbsent(key, forKey -> {
      Challenges.getInstance().getILogger().warn("Tried accessing unknown message '{}', called by {}", forKey, ReflectionUtils.getCallerName(3));
      return new MessageKeyImpl(forKey);
    });
  }

  public boolean isLanguageCached(@NotNull Locale locale) {
    boolean cachedUnnormalized = cachedLanguages.contains(locale);
    if (cachedUnnormalized) return true;
    return cachedLanguages.contains(normalizeLocale(locale));
  }

  public void unloadLanguage(@NotNull Locale locale) {
    for (MessageKeyImpl message : cachedMessages.values()) {
      message.removeValue(locale);
    }
    cachedLanguages.remove(normalizeLocale(locale));
  }

  public int populateLanguageFromDocument(@NotNull Locale locale, @NotNull Document document, @Nullable Document globalDocument) {
    Map<String, String[]> messageBundle = new HashMap<>();

    if (globalDocument != null) {
      extractFlattenedMessagesInto(globalDocument, messageBundle, MessageFormatter.SUB_DOCUMENT_DELIMITER, "");
    }
    extractFlattenedMessagesInto(document, messageBundle, MessageFormatter.SUB_DOCUMENT_DELIMITER, "");

    populateLanguage(locale, messageBundle);
    return messageBundle.size();
  }

  private void extractFlattenedMessagesInto(@NotNull Document from, @NotNull Map<String, String[]> into, char pathDelimiter, @NotNull String path) {
    for (String key : from.keys()) {
      if (from.isDocument(key)) {
        extractFlattenedMessagesInto(from.getDocument(key), into, pathDelimiter, path + key + pathDelimiter);
      } else {
        into.put(path + key, from.getStringArray(key));
      }
    }
  }

  public void populateLanguage(@NotNull Locale locale, @NotNull Map<String, String[]> messageBundle) {
    // pre resolve references to improve runtime performance
    // we need all translated messages beforehand, to resolve references!
    for (Map.Entry<String, String[]> entry : messageBundle.entrySet()) {
      // skip meta tags, they should not be cached and inflate memory usage
      // they should only ever be used in the reference/argument resolution stage
      if (MessageFormatter.isMetaTag(entry.getKey())) continue;
      String[] embeddedReferenced = MessageFormatter.embedReferences(entry.getValue(), entry.getKey(), messageBundle);
      cachedMessages.computeIfAbsent(entry.getKey(), MessageKeyImpl::new)
        .setValue(locale, embeddedReferenced);
    }
    cachedLanguages.add(normalizeLocale(locale));
  }

  // TODO remove
  @NotNull
  @CheckReturnValue
  public static Locale normalizeLocale(@Nullable Locale locale) {
    if (locale == null) return FALLBACK_LOCALE;

    if (locale.getCountry().isEmpty() &&
      locale.getVariant().isEmpty() &&
      locale.getScript().isEmpty() &&
      locale.getExtensionKeys().isEmpty()) {
      return locale;
    }

    // O(1) impl for supported languages
    return switch (locale.getLanguage()) {
      case "en" -> Locale.ENGLISH;
      case "de" -> Locale.GERMAN;

      // Locale.forLanguagerTag is expensive, use only if necessary
      default -> Locale.forLanguageTag(locale.getLanguage());
    };
  }

}
