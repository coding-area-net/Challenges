package net.codingarea.challenges.platform.message.paper;

import org.jetbrains.annotations.NotNull;

public final class NativePaperMessagePlatformChecker {

  private NativePaperMessagePlatformChecker() {
  }

  public static boolean isAvailable() {
    try {
      // test if Paper's native MiniMessage is present on the classpath
      Class.forName(buildClassName("net", "kyori", "adventure", "text", "minimessage", "MiniMessage"));
      // impl version we use internally; fallback if it has changed as it would lead to errors
      Class.forName(buildClassName("net", "kyori", "adventure", "text", "minimessage", "MiniMessageImpl"));
      // required translation api
      Class.forName(buildClassName("net", "kyori", "adventure", "translation", "Translatable"));
      return true;
    } catch (Throwable ex) {
      return false;
    }
  }

  private static String buildClassName(@NotNull String... location) {
    // use string builder to avoid constants being remapped due to relocation
    // as we don't care about our shaded impl but the native one bundled by paper
    return String.join(".", location);
  }

}
