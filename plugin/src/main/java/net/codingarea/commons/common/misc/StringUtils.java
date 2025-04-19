package net.codingarea.commons.common.misc;

import net.codingarea.commons.common.collection.WrappedException;
import net.codingarea.commons.common.logging.ILogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

public final class StringUtils {

	private static final ILogger logger = ILogger.forThisClass();

	private StringUtils() {}

	@Nonnull
	public static String getEnumName(@Nonnull Enum<?> enun) {
		return getEnumName(enun.name());
	}

	@Nonnull
	public static String getEnumName(@Nonnull String name) {
		StringBuilder builder = new StringBuilder();
		boolean nextUpperCase = true;
		for (char letter : name.toCharArray()) {
			// Replace _ with space
			if (letter == '_') {
				builder.append(' ');
				nextUpperCase = true;
				continue;
			}
			builder.append(nextUpperCase ? Character.toUpperCase(letter) : Character.toLowerCase(letter));
			nextUpperCase = false;
		}
		return builder.toString();
	}

	@Nonnull
	public static String format(@Nonnull String sequence, @Nonnull Object... args) {
		char start = '{', end = '}';
		boolean inArgument = false;
		StringBuilder argument = new StringBuilder();
		StringBuilder builder = new StringBuilder();
		for (char c : sequence.toCharArray()) {
			if (c == end && inArgument) {
				inArgument = false;
				try {
					int arg = Integer.parseInt(argument.toString());
					Object current = args[arg];
					Object replacement =
							current instanceof Supplier ? ((Supplier<?>)current).get() :
							current instanceof Callable ? ((Callable<?>)current).call() :
							current;
					builder.append(replacement);
				} catch (NumberFormatException | IndexOutOfBoundsException ex) {
					logger.warn("Invalid argument index '{}'", argument);
					builder.append(start).append(argument).append(end);
				} catch (Exception ex) {
					throw new WrappedException(ex);
				}
				argument = new StringBuilder();
				continue;
			}
			if (c == start && !inArgument) {
				inArgument = true;
				continue;
			}
			if (inArgument) {
				argument.append(c);
				continue;
			}
			builder.append(c);
		}
		if (argument.length() > 0) builder.append(start).append(argument);
		return builder.toString();
	}

	@Nonnull
	public static String getAfterLastIndex(@Nonnull String input, @Nonnull String separator) {
		return Optional.of(input)
				.filter(name -> name.contains(separator))
				.map(name -> name.substring(name.lastIndexOf(separator) + separator.length()))
				.orElse("");
	}

	@Nonnull
	public static String[] format(@Nonnull String[] array, @Nonnull Object... args) {
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = format(array[i], args);
		}
		return result;
	}

	@Nonnull
	public static String getArrayAsString(@Nonnull String[] array, @Nonnull String separator) {
		StringBuilder builder = new StringBuilder();
		for (String string : array) {
			if (builder.length() != 0) builder.append(separator);
			builder.append(string);
		}
		return builder.toString();
	}

	@Nonnull
	public static String[] getStringAsArray(@Nonnull String string) {
		return string.split("\n");
	}

	@Nonnull
	public static <T> String getIterableAsString(@Nonnull Iterable<T> iterable, @Nonnull String separator, @Nonnull Function<T, String> mapper) {
		StringBuilder builder = new StringBuilder();
		for (T t : iterable) {
			if (builder.length() > 0) builder.append(separator);
			String string = mapper.apply(t);
			builder.append(string);
		}
		return builder.toString();
	}

	@Nonnull
	public static String repeat(@Nullable Object sequence, int amount) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < amount; i++) builder.append(sequence);
		return builder.toString();
	}

	private static int getMultiplier(char c) {
		switch (Character.toLowerCase(c)) {
			default:    return 1;
			case 'm':   return 60;
			case 'h':   return 60*60;
			case 'd':   return 24*60*60;
			case 'w':   return 7*24*60*60;
			case 'y':   return 365*24*60*60;
		}
	}

	public static long parseSeconds(@Nonnull String input) {
		if (input.toLowerCase().startsWith("perm")) return -1;
		long current = 0;
		long seconds = 0;
		for (char c : input.toCharArray()) {
			try {
				long i = Long.parseUnsignedLong(String.valueOf(c));
				current *= 10;
				current += i;
			} catch (Exception ignored) {
				int multiplier = getMultiplier(c);
				seconds += current * multiplier;
				current = 0;
			}
		}
		seconds += current;
		return seconds;
	}

	public static boolean isNumber(@Nonnull String sequence) {
		try {
			Double.parseDouble(sequence);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private static int indexOf(@Nonnull String string, @Nonnull String pattern, int occurrenceIndex) {
		int lastIndex = 0;
		for (int currentLayer = 0; currentLayer <= occurrenceIndex; currentLayer++) {
			int index = string.indexOf(pattern, (lastIndex > 0) ? lastIndex + 1 : 0);
			if (index == -1) return -1;
			lastIndex = index + 1;
		}

		return lastIndex;
	}

}
