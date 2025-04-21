package net.codingarea.commons.common.config.document;

import net.codingarea.commons.common.config.Config;
import net.codingarea.commons.common.logging.ILogger;
import net.codingarea.commons.common.misc.ReflectionUtils;
import net.codingarea.commons.common.version.Version;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractConfig implements Config {

	protected static final ILogger logger = ILogger.forThisClass();

	@Nonnull
	protected <T> T getDef(@Nonnull T def, @Nonnull Supplier<? extends T> getter) {
		T value = getter.get();
		return value == null ? def : value;
	}

	@Nonnull
	@Override
	public Object getObject(@Nonnull String path, @Nonnull Object def) {
		Object value = getObject(path);
		return value == null ? def : value;
	}

	@Nonnull
	@Override
	public String getString(@Nonnull String path, @Nonnull String def) {
		return getDef(def, () -> getString(path));
	}

	@Override
	public char getChar(@Nonnull String path) {
		return getChar(path, (char) 0);
	}

	@Override
	public char getChar(@Nonnull String path, char def) {
		try {
			return getString(path).charAt(0);
		} catch (NullPointerException | IndexOutOfBoundsException ex) {
			return def;
		}
	}

	@Override
	public double getDouble(@Nonnull String path) {
		return getDouble(path, 0);
	}

	@Override
	public float getFloat(@Nonnull String path) {
		return getFloat(path, 0);
	}

	@Override
	public long getLong(@Nonnull String path) {
		return getLong(path, 0);
	}

	@Override
	public int getInt(@Nonnull String path) {
		return getInt(path, 0);
	}

	@Override
	public short getShort(@Nonnull String path) {
		return getShort(path, (short) 0);
	}

	@Override
	public byte getByte(@Nonnull String path) {
		return getByte(path, (byte) 0);
	}

	@Override
	public boolean getBoolean(@Nonnull String path) {
		return getBoolean(path, false);
	}

	@Nonnull
	@Override
	public UUID getUUID(@Nonnull String path, @Nonnull UUID def) {
		return getDef(def, () -> getUUID(path));
	}

	@Nonnull
	@Override
	public OffsetDateTime getDateTime(@Nonnull String path, @Nonnull OffsetDateTime def) {
		return getDef(def, () -> getDateTime(path));
	}

	@Nonnull
	@Override
	public Date getDate(@Nonnull String path, @Nonnull Date def) {
		return getDef(def, () -> getDate(path));
	}

	@Nonnull
	@Override
	public Color getColor(@Nonnull String path, @Nonnull Color def) {
		return getDef(def, () -> getColor(path));
	}

	@Nullable
	@Override
	public <E extends Enum<E>> E getEnum(@Nonnull String path, @Nonnull Class<E> classOfEnum) {
		return ReflectionUtils.getEnumOrNull(getString(path), classOfEnum);
	}

	@Nonnull
	@Override
	public <E extends Enum<E>> E getEnum(@Nonnull String path, @Nonnull E def) {
		E value = getEnum(path, def.getDeclaringClass());
		return value == null ? def : value;
	}

	@Nullable
	@Override
	public Class<?> getClass(@Nonnull String path) {
		try {
			return Class.forName(getString(path));
		} catch (Exception ex) {
			return null;
		}
	}

	@Nonnull
	@Override
	public Class<?> getClass(@Nonnull String path, @Nonnull Class<?> def) {
		return getDef(def, () -> getClass(path));
	}

	@Nullable
	@Override
	public Version getVersion(@Nonnull String path) {
		return Version.parse(getString(path), null);
	}

	@Nonnull
	@Override
	public Version getVersion(@Nonnull String path, @Nonnull Version def) {
		return getDef(def, () -> getVersion(path));
	}

	@Nullable
	@Override
	public byte[] getBinary(@Nonnull String path) {
		String string = getString(path);
		if (string == null) return null;
		return Base64.getDecoder().decode(string);
	}

	@Nonnull
	@Override
	public String[] getStringArray(@Nonnull String path) {
		return getStringList(path).toArray(new String[0]);
	}

	@Nonnull
	@Override
	public <E extends Enum<E>> List<E> getEnumList(@Nonnull String path, @Nonnull Class<E> classOfEnum) {
		return mapList(path, name -> ReflectionUtils.getEnumOrNull(name, classOfEnum));
	}

	@Nonnull
	@Override
	public List<Character> getCharacterList(@Nonnull String path) {
		return mapList(path, string -> string == null || string.length() == 0 ? (char) 0 : string.charAt(0));
	}

	@Nonnull
	@Override
	public List<UUID> getUUIDList(@Nonnull String path) {
		return mapList(path, UUID::fromString);
	}

	@Nonnull
	@Override
	public List<Byte> getByteList(@Nonnull String path) {
		return mapList(path, Byte::parseByte);
	}

	@Nonnull
	@Override
	public List<Short> getShortList(@Nonnull String path) {
		return mapList(path, Short::parseShort);
	}

	@Nonnull
	@Override
	public List<Integer> getIntegerList(@Nonnull String path) {
		return mapList(path, Integer::parseInt);
	}

	@Nonnull
	@Override
	public List<Long> getLongList(@Nonnull String path) {
		return mapList(path, Long::parseLong);
	}

	@Nonnull
	@Override
	public List<Float> getFloatList(@Nonnull String path) {
		return mapList(path, Float::parseFloat);
	}

	@Nonnull
	@Override
	public List<Double> getDoubleList(@Nonnull String path) {
		return mapList(path, Double::parseDouble);
	}

	@Nonnull
	@Override
	public <T> List<T> mapList(@Nonnull String path, @Nonnull Function<String, ? extends T> mapper) {
		List<String> list = getStringList(path);
		List<T> result = new ArrayList<>(list.size());
		for (String string : list) {
			try {
				result.add(mapper.apply(string));
			} catch (Exception ex) {
				logger.error("Unable to map values for '{}'", string, ex);
			}
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Nonnull
	@Override
	public Map<String, String> valuesAsStrings() {
		Map<String, String> map = new HashMap<>();
		values().forEach((key, value) -> map.put(key, String.valueOf(value)));
		return map;
	}

	@Nonnull
	@Override
	public <K, V> Map<K, V> mapValues(@Nonnull Function<? super String, ? extends K> keyMapper, @Nonnull Function<? super String, ? extends V> valueMapper) {
		return map(valuesAsStrings(), keyMapper, valueMapper);
	}

	@Nonnull
	@Override
	public Set<Entry<String, Object>> entrySet() {
		return values().entrySet();
	}

	@Nonnull
	public <FromK, FromV, ToK, ToV> Map<ToK, ToV> map(@Nonnull Map<? extends FromK, ? extends FromV> values,
	                                                  @Nonnull Function<? super FromK, ? extends ToK> keyMapper,
	                                                  @Nonnull Function<? super FromV, ? extends ToV> valueMapper) {
		Map<ToK, ToV> result = new HashMap<>();
		values.forEach((key, value) -> {
			try {
				result.put(keyMapper.apply(key), valueMapper.apply(value));
			} catch (Exception ex) {
				logger.error("Unable to map values for '{}'='{}'", key, value, ex);
			}
		});
		return result;
	}

}
