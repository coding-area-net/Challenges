package net.codingarea.commons.common.config.document;

import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.misc.FileUtils;
import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.json.JsonWriterSettings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

public class BsonDocument extends AbstractDocument {

	protected org.bson.Document bsonDocument;

	public BsonDocument(@Nonnull File file) throws IOException {
		this(FileUtils.newBufferedReader(file));
	}

	public BsonDocument(@Nonnull Reader reader) {
		BufferedReader buffered = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
		StringBuilder content = new StringBuilder();
		buffered.lines().forEach(content::append);
		bsonDocument = org.bson.Document.parse(content.toString());
	}

	public BsonDocument(@Nonnull org.bson.Document bsonDocument) {
		this.bsonDocument = bsonDocument;
	}

	public BsonDocument(@Nonnull org.bson.Document bsonDocument, @Nonnull Document root, @Nullable Document parent) {
		super(root, parent);
		this.bsonDocument = bsonDocument;
	}

	public BsonDocument() {
		this(new org.bson.Document());
	}

	@Nonnull
	@Override
	public Document getDocument0(@Nonnull String path, @Nonnull Document root, @Nullable Document parent) {
		org.bson.Document document = bsonDocument.get(path, org.bson.Document.class);
		if (document == null) {
			bsonDocument.put(path, document = new org.bson.Document());
		}

		return new BsonDocument(document, root, parent);
	}

	@Nonnull
	@Override
	public List<Document> getDocumentList(@Nonnull String path) {
		BsonArray array = bsonDocument.get(path, BsonArray.class);
		if (array == null) return new ArrayList<>();
		List<Document> documents = new ArrayList<>(array.size());
		for (BsonValue value : array) {
			if (!value.isDocument()) continue;
			String json = value.asDocument().toJson();
			org.bson.Document document = org.bson.Document.parse(json);
			documents.add(new BsonDocument(document, root, this));
		}
		return documents;
	}

	@Nullable
	@Override
	public String getString(@Nonnull String path) {
		Object value = getObject(path);
		return value == null ? null : value.toString();
	}

	@Override
	public long getLong(@Nonnull String path, long def) {
		try {
			return Long.parseLong(Objects.requireNonNull(getString(path)));
		} catch (Exception ex) {
			return def;
		}
	}

	@Override
	public int getInt(@Nonnull String path, int def) {
		try {
			return Integer.parseInt(Objects.requireNonNull(getString(path)));
		} catch (Exception ex) {
			return def;
		}
	}

	@Override
	public short getShort(@Nonnull String path, short def) {
		try {
			return Short.parseShort(Objects.requireNonNull(getString(path)));
		} catch (Exception ex) {
			return def;
		}
	}

	@Override
	public byte getByte(@Nonnull String path, byte def) {
		try {
			return Byte.parseByte(Objects.requireNonNull(getString(path)));
		} catch (Exception ex) {
			return def;
		}
	}

	@Override
	public double getDouble(@Nonnull String path, double def) {
		try {
			return Double.parseDouble(Objects.requireNonNull(getString(path)));
		} catch (Exception ex) {
			return def;
		}
	}

	@Override
	public float getFloat(@Nonnull String path, float def) {
		try {
			return Float.parseFloat(Objects.requireNonNull(getString(path)));
		} catch (Exception ex) {
			return def;
		}
	}

	@Override
	public boolean getBoolean(@Nonnull String path, boolean def) {
		try {
			Object value = bsonDocument.get(path);
			if (value instanceof Boolean) return (Boolean) value;
			if (value instanceof String) return Boolean.parseBoolean((String) value);
		} catch (Exception ignored) {
		}
		return def;
	}

	@Nullable
	@Override
	public Object getObject(@Nonnull String path) {
		return bsonDocument.get(path);
	}

	@Nonnull
	@Override
	public List<String> getStringList(@Nonnull String path) {
		return bsonDocument.getList(path, String.class);
	}

	@Nullable
	@Override
	public UUID getUUID(@Nonnull String path) {
		try {
			Object value = bsonDocument.get(path);
			if (value instanceof UUID) return (UUID) value;
			if (value instanceof String) return UUID.fromString((String) value);
		} catch (Exception ignored) {
		}
		return null;
	}

	@Nullable
	@Override
	public Date getDate(@Nonnull String path) {
		return bsonDocument.getDate(path);
	}

	@Nullable
	@Override
	public OffsetDateTime getDateTime(@Nonnull String path) {
		Object value = getObject(path);

		if (value == null)
			return null;
		if (value instanceof OffsetDateTime)
			return (OffsetDateTime) value;
		if (value instanceof Date)
			return ((Date)value).toInstant().atOffset(ZoneOffset.UTC);
		if (value instanceof String)
			return OffsetDateTime.parse((CharSequence) value);

		throw new IllegalStateException(value.getClass().getName() + " cannot be converted to java.time.OffsetDateTime");
	}

	@Nullable
	@Override
	public Color getColor(@Nonnull String path) {
		Object value = getObject(path);

		if (value == null)
			return null;
		if (value instanceof Color)
			return (Color) value;
		if (value instanceof String)
			return Color.decode((String) value);

		throw new IllegalStateException(value.getClass().getName() + " cannot be converted to java.awt.Color");
	}

	@Override
	public <T> T getInstance(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return copyJson().getInstance(path, classOfT);
	}

	@Override
	public <T> T toInstanceOf(@Nonnull Class<T> classOfT) {
		return copyJson().toInstanceOf(classOfT);
	}

	@Override
	public boolean contains(@Nonnull String path) {
		return bsonDocument.containsKey(path);
	}

	@Override
	public boolean isList(@Nonnull String path) {
		Object value = bsonDocument.get(path);
		return value instanceof Iterable || (value != null && value.getClass().isArray());
	}

	@Override
	public boolean isDocument(@Nonnull String path) {
		Object value = bsonDocument.get(path);
		return value instanceof org.bson.Document;
	}

	@Override
	public boolean isObject(@Nonnull String path) {
		return !isList(path) && !isDocument(path);
	}

	@Override
	public int size() {
		return bsonDocument.size();
	}

	@Override
	public void clear0() {
		bsonDocument.clear();
	}

	@Override
	public void set0(@Nonnull String path, @Nullable Object value) {
		bsonDocument.put(path, value);
	}

	@Override
	public void remove0(@Nonnull String path) {
		bsonDocument.remove(path);
	}

	@Override
	public void write(@Nonnull Writer writer) throws IOException {
		String json = bsonDocument.toString();
		writer.write(json);
	}

	@Nonnull
	@Override
	public Map<String, Object> values() {
		return Collections.unmodifiableMap(bsonDocument);
	}

	@Nonnull
	@Override
	public Collection<String> keys() {
		return bsonDocument.keySet();
	}

	@Override
	public void forEach(@Nonnull BiConsumer<? super String, ? super Object> action) {
		bsonDocument.forEach(action);
	}

	@Nonnull
	@Override
	public String toJson() {
		return bsonDocument.toJson();
	}

	@Nonnull
	@Override
	public String toPrettyJson() {
		return bsonDocument.toJson(JsonWriterSettings.builder().indent(true).build());
	}

	@Override
	public String toString() {
		return toJson();
	}

	@Override
	public boolean isReadonly() {
		return false;
	}

}
