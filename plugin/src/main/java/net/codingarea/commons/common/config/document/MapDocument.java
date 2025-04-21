package net.codingarea.commons.common.config.document;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.config.PropertyHelper;
import net.codingarea.commons.common.misc.GsonUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.io.Writer;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This class is not thread safe.
 * Documents of this type are not able to be saved or loaded.
 */
public class MapDocument extends AbstractDocument {

	private final Map<String, Object> values;

	public MapDocument(@Nonnull Map<String, Object> values) {
		Preconditions.checkNotNull(values, "Map cannot be null");
		this.values = values;
	}

	public MapDocument(@Nonnull Map<String, Object> values, @Nonnull Document root, @Nullable Document parent) {
		super(root, parent);
		this.values = values;
	}

	public MapDocument() {
		this(new LinkedHashMap<>());
	}

	@Override
	public boolean isReadonly() {
		return false;
	}

	@Nonnull
	@Override
	public Document getDocument0(@Nonnull String path, @Nonnull Document root, @Nullable Document parent) {
		Object value = this.values.computeIfAbsent(path, key -> new HashMap<>());
		if (value instanceof Map) return new MapDocument((Map<String, Object>) value, root, parent);
		if (value instanceof Document) return (Document) value;
		if (value instanceof String) return new GsonDocument((String) value, root, parent);
		throw new IllegalStateException("Expected java.util.Map, found " + values.getClass().getName());
	}

	@Nonnull
	@Override
	public List<Document> getDocumentList(@Nonnull String path) {
		List<Document> documents = new ArrayList<>();
		Object value = values.get(path);
		if (value instanceof List) {
			List<Object> list = (List<Object>) value;
			for (Object object : list) {
				if (object instanceof Map) documents.add(new MapDocument((Map<String, Object>) object, root, parent));
				if (object instanceof Document) documents.add((Document) object);
				if (object instanceof String) documents.add(new GsonDocument((String) object, root, this));
			}
		}
		return documents;
	}

	@Override
	public void set0(@Nonnull String path, @Nullable Object value) {
		values.put(path, value);
	}

	@Override
	public void clear0() {
		values.clear();
	}

	@Override
	public void remove0(@Nonnull String path) {
		values.remove(path);
	}

	@Override
	public void write(@Nonnull Writer writer) throws IOException {
		new GsonDocument(values).write(writer);
	}

	@Nonnull
	@Override
	public String toJson() {
		return new GsonDocument(values).toJson();
	}

	@Nonnull
	@Override
	public String toPrettyJson() {
		return new GsonDocument(values).toPrettyJson();
	}

	@Nullable
	@Override
	public Object getObject(@Nonnull String path) {
		return values.get(path);
	}

	@Override
	public <T> T getInstance(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return classOfT.cast(getObject(path));
	}

	@Override
	public <T> T toInstanceOf(@Nonnull Class<T> classOfT) {
		return copyJson().toInstanceOf(classOfT);
	}

	@Nullable
	@Override
	public String getString(@Nonnull String path) {
		Object value = values.get(path);
		return value == null ? null : value.toString();
	}

	@Override
	public long getLong(@Nonnull String path, long def) {
		try {
			return Long.parseLong(getString(path));
		} catch (Exception ex) {
			return def;
		}
	}

	@Override
	public int getInt(@Nonnull String path, int def) {
		try {
			return Integer.parseInt(getString(path));
		} catch (Exception ex) {
			return def;
		}
	}

	@Override
	public short getShort(@Nonnull String path, short def) {
		try {
			return Short.parseShort(getString(path));
		} catch (Exception ex) {
			return def;
		}
	}

	@Override
	public byte getByte(@Nonnull String path, byte def) {
		try {
			return Byte.parseByte(getString(path));
		} catch (Exception ex) {
			return def;
		}
	}

	@Override
	public float getFloat(@Nonnull String path, float def) {
		try {
			return Float.parseFloat(getString(path));
		} catch (Exception ex) {
			return def;
		}
	}

	@Override
	public double getDouble(@Nonnull String path, double def) {
		try {
			return Double.parseDouble(getString(path));
		} catch (Exception ex) {
			return def;
		}
	}

	@Override
	public boolean getBoolean(@Nonnull String path, boolean def) {
		try {
			if (!contains(path)) return def;
			switch (getString(path).toLowerCase()) {
				case "true":
				case "1":
					return true;
				default:
					return false;
			}
		} catch (Exception ex) {
			return def;
		}
	}

	@Nonnull
	@Override
	public List<String> getStringList(@Nonnull String path) {
		Object object = getObject(path);
		if (object == null) return Collections.emptyList();
		if (object instanceof Iterable) return StreamSupport.stream(((Iterable<?>)object).spliterator(), false).map(String::valueOf).collect(Collectors.toList());
		if (object instanceof String) return GsonUtils.convertJsonArrayToStringList(GsonDocument.GSON.fromJson((String) object, JsonArray.class));
		throw new IllegalStateException("Cannot convert " + object.getClass() + " to a list");
	}

	@Nullable
	@Override
	public UUID getUUID(@Nonnull String path) {
		try {
			Object object = getObject(path);
			if (object instanceof UUID) return (UUID) object;
			return UUID.fromString(String.valueOf(object));
		} catch (Exception ex) {
			return null;
		}
	}

	@Nullable
	@Override
	public Date getDate(@Nonnull String path) {
		Object object = getObject(path);
		if (object instanceof String) return PropertyHelper.parseDate((String) object);
		if (object instanceof Date) return (Date) object;
		return null;
	}

	@Nullable
	@Override
	public OffsetDateTime getDateTime(@Nonnull String path) {
		Object object = getObject(path);
		if (object instanceof CharSequence) return OffsetDateTime.parse((CharSequence) object);
		if (object instanceof OffsetDateTime) return (OffsetDateTime) object;
		return null;
	}

	@Nullable
	@Override
	public Color getColor(@Nonnull String path) {
		Object object = getObject(path);
		if (object instanceof Color) return (Color) object;
		if (object instanceof String) return Color.decode((String) object);
		return null;
	}

	@Override
	public boolean isList(@Nonnull String path) {
		Object value = values.get(path);
		return value instanceof Iterable || (value != null && value.getClass().isArray());
	}

	@Override
	public boolean isObject(@Nonnull String path) {
		return !isDocument(path) && !isList(path);
	}

	@Override
	public boolean isDocument(@Nonnull String path) {
		Object value = values.get(path);
		return value instanceof Map || value instanceof Document;
	}

	@Override
	public boolean contains(@Nonnull String path) {
		return values.containsKey(path);
	}

	@Override
	public int size() {
		return values.size();
	}

	@Nonnull
	@Override
	public Map<String, Object> values() {
		return Collections.unmodifiableMap(values);
	}

	@Nonnull
	@Override
	public Collection<String> keys() {
		return values.keySet();
	}

	@Override
	public void forEach(@Nonnull BiConsumer<? super String, ? super Object> action) {
		values.forEach(action);
	}

}
