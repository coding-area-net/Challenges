package net.codingarea.challenges.plugin.content.i18n;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class Prefix {

  public static final Prefix
    CHALLENGES = create("challenges", "§6Challenges"),
    CUSTOM = create("custom", "§bCustom"),
    DAMAGE = create("damage", "§cDamage"),
    POSITION = create("position", "§9Position"),
    BACKPACK = create("backpack", "§aBackpack"),
    TIMER = create("timer", "§5Timer");

  private final String messageKey;

  @Getter
  private final String legacyFallback; // will only ever be used if language files could not be loaded

  @NotNull
  public MessageKey getKey() {
    return MessageKey.of(messageKey);
  }

  @Override
  public String toString() {
    return legacyFallback;
  }

  public static Prefix create(@NotNull String messageKeySuffix, @NotNull String legacyFallbackFormat) {
    String prefixKey = "prefix." + messageKeySuffix;
    return new Prefix(prefixKey, createLegacyFallback(legacyFallbackFormat));
  }

  private static String createLegacyFallback(@NotNull String legacyPrefixFormat) {
    return "§8§l┃ " + legacyPrefixFormat + " §8┃ ";
  }

}
