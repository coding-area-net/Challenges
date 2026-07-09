package net.codingarea.challenges.plugin.content.i18n;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.impl.MessageKeyImpl;
import net.codingarea.challenges.plugin.content.i18n.impl.format.MessageFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.*;

import java.util.List;
import java.util.Locale;

/**
 * Identifies a translatable message using a unique name (key).
 * Subdocument/-object paths may be accessed by using dots as the path delimiter.
 * <p>
 * Provides all required helpers and accessor to utilize them directly like
 * <ul>
 *   <li>{@link #send(Player, Prefix, Object...)} to send this as a chat message to a player in their language</li>
 *   <li>{@link #sendRandom(Player, Prefix, Object...)} to send one randomly selected chat message (of the translation array) in their language</li>
 *   <li>{@link #sendTitle(Player, Object...)} to send this as a title and subtitle to a player in their language, where the first line in the array in the header, and the second the subtitle</li>
 *   <li>{@link #sendActionBar(Player, Object...)} to send this as an action bar to a player, the translation must be one line</li>
 *   <li>{@link #broadcast(Prefix, Object...)} to send this as a chat message to all players in their language</li>
 * </ul>
 * See {@link net.codingarea.challenges.plugin.utils.item.ItemBuilder#ItemBuilder(Locale, Material, MessageKey, Object...)}
 * for creating items with translated names and descriptions.
 * <p>
 * Due to compatibility issues we cannot expose the internally used adventure api / mini-message api to this module directly.
 *
 * @implSpec Do <b>NOT</b> store {@link MessageKey} instances {@code statically}, but rather use {@link #of(String)} to retrieve them when needed.
 * @implNote Messages may use the following formats:
 * <ul>
 *  <li>
 *    <b>Positional Arguments:</b>
 *    {@code {0}}, {@code {1}}, etc. - Replaced by corresponding arguments passed at runtime.
 *  </li>
 *  <li>
 *    <b>Message References:</b>
 *    {@code {@message.key}} - Dynamically embeds another message from the bundle.
 *    Supports multi-line resolution and gracefully handles cyclic dependencies.
 *    Positional arguments will be added to the root translation while the argument indices remain unmodified.
 *  </li>
 *  <li>
 *    <b>Mini Message</b>
 *    The system uses "Mini Messages" internally, by parsing {@code <...>} tags;
 *    see the <a href="https://docs.papermc.io/adventure/minimessage/format/">MiniMessage documentation</a> for more.
 *  </li>
 *  <li>
 *    <b>Legacy Colors</b>
 *    Currently, legacy colors using '§' are still supported, but it's strongly recommended to use the MiniMessage
 *    format instead as this implementation is subject to change
 *  </li>
 *  <li>
 *    <b>Supported Argument Types</b>
 *    The following argument types are currently supported by default and will automatically be formatted
 *    (and <b>translated</b> using their namespace key if possible)
 *    <ul>
 *      <li>{@link MessageKey} will automatically be localized into the targeted language
 *      (for static linking use {@code {\@x}} reference)</li>
 *      <li>{@link LocalizableMessage} will automatically be localized into the target language,
 *      it can be used to reference messages dynamically with additional object arguments using {@link MessageKey#withArgs(Object...)}</li>
 *      <li>{@link net.kyori.adventure.translation.Translatable} like {@link org.bukkit.Material}, {@link org.bukkit.entity.EntityType}, {@link org.bukkit.GameMode}</li>
 *      <li>{@link org.bukkit.entity.Player} using their name</li>
 *      <li>Unknown Enums will be formatted as {@code TEST_ENUM -> "Test Enum"}</li>
 *      <li>Unresolvable types will be serialized using {@link Object#toString()}</li>
 *    </ul>
 *  </li>
 * </ul>
 * @see TranslationManager TranslationManager for localization
 * @see MessageFormatter MessageFormatter for references
 */
public interface MessageKey extends LocalizableMessage {

  /**
   * @implSpec Do <b>NOT</b> store {@link MessageKey} instances {@code statically}, but rather retrieve them when needed.
   * @see #withArgs(Object...)
   */
  @NotNull
  @CheckReturnValue
  static MessageKey of(@NotNull String id) {
    TranslationManager i18n = Challenges.getInstance().getTranslationManager();
    if (i18n == null) throw new IllegalStateException("TranslationManager not initialized yet");
    return i18n.getMessageKey(id);
  }

  @NotNull
  @CheckReturnValue
  static MessageKey pluralize(@NotNull String singularKey, @NotNull String pluralKey, @NotNull Number count) {
    return of(count.intValue() == 1 ? singularKey : pluralKey);
  }

  @NotNull
  @CheckReturnValue
  static MessageKey pluralize(@NotNull String baseKey, @NotNull Number count) {
    return pluralize(baseKey, baseKey + "s", count);
  }

  @NotNull
  @CheckReturnValue
  @ApiStatus.Internal
  static MessageKey empty(@NotNull String id) {
    return new MessageKeyImpl(id);
  }

  @NotNull
  static String formatMissingTranslation(@NotNull String key, @NotNull Locale locale) {
    return String.format("%s/%s", key, locale);
  }

  @NotNull
  default MessageKey getChildKey(@NotNull String child) {
    return of(getKey() + "." + child);
  }

  @NotNull
  String getKey();

  /**
   * @return whether this key exists in any language
   */
  boolean exists();

  boolean isCached(@NotNull Locale locale);

  void removeValue(@NotNull Locale locale);

  void setValue(@NotNull Locale locale, @NotNull String[] values);

  /**
   * @param locale language to translate to
   * @return the raw value of this translated message or {@link #formatMissingTranslation(String, Locale)}
   * @implSpec the returned array must never be empty
   */
  @NotNull
  String[] localizeRawValue(@NotNull Locale locale);

  /**
   * @param locale language to translate to
   * @return the raw value of this translated message as one string (if it consists of only one line,
   * joined by newlines {@code \n} if multiple lines) or {@link #formatMissingTranslation(String, Locale)}
   */
  @NotNull
  String localizeRawValueAsSingleLine(@NotNull Locale locale);

  @NotNull
  @Contract(pure = true)
  LocalizableMessage withArgs(@NotNull Object... args);

  // Helpers

  /**
   * If the target ({@link CommandSender}) is not an instance of {@link Player},
   * the message will be translated using the global server language set in the {@code plugin.yml}
   *
   * @see net.codingarea.challenges.plugin.content.loader.LanguageLoader#getConfigLanguage()
   */
  void send(@NotNull CommandSender target, @Nullable Prefix prefix, @NotNull Object... args);

  void send(@NotNull Player target, @Nullable Prefix prefix, @NotNull Object... args);

  void sendRandom(@NotNull Player target, @Nullable Prefix prefix, @NotNull Object... args);

  void broadcast(@Nullable Prefix prefix, @NotNull Object... args);

  /**
   * Broadcasts one of the entries in the array at random.
   * All players receive the same message translated into their language set by the {@link LanguageProvider}
   */
  void broadcastRandom(@Nullable Prefix prefix, @NotNull Object... args);

  void sendTitle(@NotNull Player target, @NotNull Object... args);

  void sendTitleInstantly(@NotNull Player target, @NotNull Object... args);

  void broadcastTitle(@NotNull Object... args);

  void broadcastTitleInstantly(@NotNull Object... args);

  void sendActionBar(@NotNull Player target, @NotNull Object... args);

  void broadcastActionBar(@NotNull Object... args);

  // Formatting

  @NotNull
  Component asComponent(@NotNull Locale locale, @Nullable Prefix prefix, @NotNull Object... args);

  @NotNull
  default Component asComponent(@NotNull Player playerLocale, @Nullable Prefix prefix, @NotNull Object... args) {
    return asComponent(findPlayerLocale(playerLocale), prefix, args);
  }

  @NotNull
  default Component asComponent(@NotNull Locale locale, @NotNull Object... args) {
    return asComponent(locale, null, args);
  }

  @NotNull
  default Component asComponent(@NotNull Player playerLocale, @NotNull Object... args) {
    return asComponent(findPlayerLocale(playerLocale), args);
  }

  @NotNull
  List<Component> asComponents(@NotNull Locale locale, @NotNull Object... args);

  @NotNull
  default List<Component> asComponents(@NotNull Player playerLocale, @NotNull Object... args) {
    return asComponents(findPlayerLocale(playerLocale), args);
  }

  // Utils

  @NotNull
  private Locale findPlayerLocale(@NotNull Player player) {
    return Challenges.getInstance().getTranslationManager().getLanguageProvider().getPlayerLanguage(player);
  }

}
