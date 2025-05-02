package net.codingarea.commons.common.misc;

import net.codingarea.commons.common.logging.ILogger;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public final class SimpleCollectionUtils {

	@Deprecated
	public static final String REGEX_1 = ",",
							   REGEX_2 = "=";
	private static final ILogger logger = ILogger.forThisClass();
	private static boolean logMappingError = true;

	private SimpleCollectionUtils() {}

	public static void disableErrorLogging() {
		logMappingError = false;
	}

	/**
	 * You should not use this to serialize maps due to it's unsafe because of strings which may contain the regex chars = or ,
	 * Use better and safer serialization strategies like json.
	 *
	 * @deprecated Unsafe because of strings containing , or =
	 */
	@Nonnull
	@Deprecated
	@CheckReturnValue
	public static <K, V> String convertMapToString(@Nonnull Map<K, V> map, @Nonnull Function<K, String> key, @Nonnull Function<V, String> value) {
		StringBuilder builder = new StringBuilder();
		for (Entry<K, V> entry : map.entrySet()) {
			if (builder.length() != 0) builder.append(REGEX_1);
			builder.append(key.apply(entry.getKey()));
			builder.append(REGEX_2);
			builder.append(value.apply(entry.getValue()));
		}
		return builder.toString();
	}

	/**
	 * You should not use this to serialize maps due to it's unsafe because of strings which may contain the regex chars = or ,
	 * Use better and safer serialization strategies like json.
	 *
	 * @deprecated Unsafe because of strings containing , or =
	 */
	@Nonnull
	@Deprecated
	@CheckReturnValue
	public static <K, V> Map<K, V> convertStringToMap(@Nullable String string, @Nonnull Function<String, K> key, @Nonnull Function<String, V> value) {

		Map<K, V> map = new HashMap<>();
		if (string == null) return map;

		String[] args = string.split(REGEX_1);
		for (String arg : args) {
			try {

				String[] elements = arg.split(REGEX_2);
				K keyElement = key.apply(elements[0]);
				V valueElement = value.apply(elements[1]);

				if (keyElement == null || valueElement == null)
					throw new NullPointerException();

				map.put(keyElement, valueElement);

			} catch (Exception ex) {
				if (logMappingError)
					logger.error("Cannot generate key/value: " + ex.getClass().getName() + ": " + ex.getMessage());
			}
		}

		return map;

	}

	@Nonnull
	@CheckReturnValue
	public static <FromK, FromV, ToK, ToV> Map<ToK, ToV> convertMap(@Nonnull Map<FromK, FromV> map,
	                                                                @Nonnull Function<? super FromK, ? extends ToK> keyMapper,
	                                                                @Nonnull Function<? super FromV, ? extends ToV> valueMapper) {
		Map<ToK, ToV> result = new HashMap<>();
		map.forEach((key, value) -> {
			try {
				result.put(keyMapper.apply(key), valueMapper.apply(value));
			} catch (Exception ex) {
				if (logMappingError)
					logger.error("Unable to map '{}'='{}'", key, value, ex);
			}
		});
		return result;
	}

	@Nonnull
	@CheckReturnValue
	public static <From, To> List<To> convert(@Nonnull Collection<From> collection,
	                                          @Nonnull Function<? super From, ? extends To> mapper) {
		List<To> result = new ArrayList<>(collection.size());
		collection.forEach(value -> {
			try {
				result.add(mapper.apply(value));
			} catch (Exception ex) {
				if (logMappingError)
					logger.error("Unable map '{}'", value, ex);
			}
		});
		return result;
	}

	public static <K, V> V getMostFrequentValue(@Nonnull Map<K, V> map) {
		Collection<V> values = map.values();
		List<V> list = new ArrayList<>(values);
		Set<V> set = new HashSet<>(values);

		V valueMax = null;
		int max = 0;
		for (V value : set) {
			int frequency = Collections.frequency(list, value);
			if (frequency > max) {
				max = frequency;
				valueMax = value;
			}
		}

		return valueMax;
	}

	@SafeVarargs
	public static <T> Set<T> setOf(@Nonnull Collection<T>... collections) {
		Set<T> set = new HashSet<>();
		for (Collection<T> collection : collections) {
			set.addAll(collection);
		}
		return set;
	}

}
