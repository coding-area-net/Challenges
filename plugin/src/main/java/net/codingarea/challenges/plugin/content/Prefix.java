package net.codingarea.challenges.plugin.content;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

	private Prefix(@Nonnull String name, @Nonnull String defaultValue) {
		this.defaultValue = getDefaultValueFor(defaultValue);
		this.name = name;
	}

	@Nonnull
	@CheckReturnValue
	public static Collection<Prefix> values() {
		return Collections.unmodifiableCollection(values.values());
	}

	@Nonnull
	@CheckReturnValue
	public static Prefix forName(@Nonnull String name, @Nonnull String defaultValue) {
		return values.computeIfAbsent(name, key -> new Prefix(name, defaultValue));
	}

	@Nonnull
	@CheckReturnValue
	public static Prefix forName(@Nonnull String name) {
		return forName(name, name);
	}

	@Nonnull
	public static String getDefaultValueFor(@Nonnull String value) {
		return "§8§l┃ " + value + " §8┃ ";
	}

	public void setValue(@Nullable String value) {
		this.value = value == null ? null : value.endsWith(" ") ? value : value + " ";
	}

	@Nonnull
	@Override
	public String toString() {
		return (value == null ? defaultValue : value) + "§7";
	}

	@Nonnull
	public String getName() {
		return name;
	}

}
