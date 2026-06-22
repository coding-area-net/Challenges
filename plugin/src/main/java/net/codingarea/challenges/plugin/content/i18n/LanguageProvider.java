package net.codingarea.challenges.plugin.content.i18n;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public interface LanguageProvider {

  /**
   * Currently only the language tag of the locale is used, country code and variant are ignored.
   * Default behavior currently implements one global language set in the plugin.yml.
   */
  @NotNull
  Locale getPlayerLanguage(@NotNull Player player);

  /**
   * Whether this provider uses the default global language set in the plugin.yml.
   * Return {@code false} when implementing custom behavior.
   */
  boolean isDefaultBehaviour();

  boolean isUserSpecific();

}
