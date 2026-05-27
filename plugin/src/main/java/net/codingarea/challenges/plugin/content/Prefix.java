package net.codingarea.challenges.plugin.content;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Prefix {

  private static final Map<String, Prefix> values = new HashMap<>();

  public static final Prefix
    CHALLENGES = forName("challenges", "§6Challenges"),
    CUSTOM = forName("custom", "§bCustom"),
    DAMAGE = forName("damage", "§cDamage"),
    POSITION = forName("position", "§9Position"),
    BACKPACK = forName("backpack", "§aBackpack"),
    TIMER = forName("timer", "§5Timer");

  private final String name;
  private final String defaultValue;
  private String value;

  private Prefix(@NotNull String name, @NotNull String defaultValue) {
    this.defaultValue = getDefaultValueFor(defaultValue);
    this.name = name;
  }

  @NotNull
  public static Collection<Prefix> values() {
    return Collections.unmodifiableCollection(values.values());
  }

  @NotNull
  public static Prefix forName(@NotNull String name, @NotNull String defaultValue) {
    return values.computeIfAbsent(name, key -> new Prefix(name, defaultValue));
  }

  @NotNull
  public static Prefix forName(@NotNull String name) {
    return forName(name, name);
  }

  @NotNull
  public static String getDefaultValueFor(@NotNull String value) {
    return "§8§l┃ " + value + " §8┃ ";
  }

  public void setValue(@Nullable String value) {
    this.value = value == null ? null : value.endsWith(" ") ? value : value + " ";
  }

  @NotNull
  @Override
  public String toString() {
    return (value == null ? defaultValue : value) + "§7";
  }

  @NotNull
  public String getName() {
    return name;
  }

}
