package net.codingarea.commons.common.config.document;

import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.config.Propertyable;
import net.codingarea.commons.common.config.exceptions.ConfigReadOnlyException;
import net.codingarea.commons.common.version.Version;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EmptyDocument implements Document {

	public static final EmptyDocument ROOT = new EmptyDocument();

	protected final Document root, parent;

	public EmptyDocument(@Nonnull Document root, @Nullable Document parent) {
		this.root = root;
		this.parent = parent;
	}

	public EmptyDocument() {
		this.root = this;
		this.parent = null;
	}

	@Nonnull
	@Override
	public Document getDocument(@Nonnull String path) {
		return new EmptyDocument();
	}

	@Nonnull
	@Override
	public <R> R mapDocument(@Nonnull String path, @Nonnull Function<? super Document, ? extends R> mapper) {
		return mapper.apply(Document.empty());
	}

	@Nullable
	@Override
	public <R> R mapDocumentNullable(@Nonnull String path, @Nonnull Function<? super Document, ? extends R> mapper) {
		return null;
	}

	@Nonnull
	@Override
	public List<Document> getDocumentList(@Nonnull String path) {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public <T> List<T> getSerializableList(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public Document set(@Nonnull String path, @Nullable Object value) {
		throw new ConfigReadOnlyException("set");
	}

	@Nonnull
	@Override
	public Document set(@Nonnull Object value) {
		throw new ConfigReadOnlyException("set");
	}

	@Nonnull
	@Override
	public Document clear() {
		return this;
	}

	@Nonnull
	@Override
	public Document remove(@Nonnull String path) {
		return this;
	}

	@Override
	public void write(@Nonnull Writer writer) throws IOException {
		throw new UnsupportedOperationException("EmptyDocument.write(Writer)");
	}

	@Override
	public void saveToFile(@Nonnull File file) throws IOException {
		throw new UnsupportedOperationException("EmptyDocument.save(File)");
	}

	@Nullable
	@Override
	public Object getObject(@Nonnull String path) {
		return null;
	}

	@Nonnull
	@Override
	public Object getObject(@Nonnull String path, @Nonnull Object def) {
		return def;
	}

	@Override
	public <T> T getInstance(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return null;
	}

	@Override
	public <T> T toInstanceOf(@Nonnull Class<T> classOfT) {
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public <T, O extends Propertyable> Optional<T> getOptional(@Nonnull String key, @Nonnull BiFunction<O, ? super String, ? extends T> extractor) {
		return Optional.empty();
	}

	@Nullable
	@Override
	public String getString(@Nonnull String path) {
		return null;
	}

	@Nonnull
	@Override
	public String getString(@Nonnull String path, @Nonnull String def) {
		return def;
	}

	@Override
	public char getChar(@Nonnull String path) {
		return 0;
	}

	@Override
	public char getChar(@Nonnull String path, char def) {
		return def;
	}

	@Override
	public long getLong(@Nonnull String path) {
		return 0;
	}

	@Override
	public long getLong(@Nonnull String path, long def) {
		return def;
	}

	@Override
	public int getInt(@Nonnull String path) {
		return 0;
	}

	@Override
	public int getInt(@Nonnull String path, int def) {
		return def;
	}

	@Override
	public short getShort(@Nonnull String path) {
		return 0;
	}

	@Override
	public short getShort(@Nonnull String path, short def) {
		return def;
	}

	@Override
	public byte getByte(@Nonnull String path) {
		return 0;
	}

	@Override
	public byte getByte(@Nonnull String path, byte def) {
		return def;
	}

	@Override
	public float getFloat(@Nonnull String path) {
		return 0;
	}

	@Override
	public float getFloat(@Nonnull String path, float def) {
		return def;
	}

	@Override
	public double getDouble(@Nonnull String path) {
		return 0;
	}

	@Override
	public double getDouble(@Nonnull String path, double def) {
		return def;
	}

	@Override
	public boolean getBoolean(@Nonnull String path) {
		return false;
	}

	@Override
	public boolean getBoolean(@Nonnull String path, boolean def) {
		return def;
	}

	@Nullable
	@Override
	public byte[] getBinary(@Nonnull String path) {
		return null;
	}

	@Nonnull
	@Override
	public List<String> getStringList(@Nonnull String path) {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public String[] getStringArray(@Nonnull String path) {
		return new String[0];
	}

	@Nonnull
	@Override
	public <E extends Enum<E>> List<E> getEnumList(@Nonnull String path, @Nonnull Class<E> classOfEnum) {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public <T> List<T> mapList(@Nonnull String path, @Nonnull Function<String, ? extends T> mapper) {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public List<UUID> getUUIDList(@Nonnull String path) {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public List<Character> getCharacterList(@Nonnull String path) {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public List<Byte> getByteList(@Nonnull String path) {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public List<Short> getShortList(@Nonnull String path) {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public List<Integer> getIntegerList(@Nonnull String path) {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public List<Long> getLongList(@Nonnull String path) {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public List<Float> getFloatList(@Nonnull String path) {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public List<Double> getDoubleList(@Nonnull String path) {
		return new ArrayList<>();
	}

	@Nullable
	@Override
	public UUID getUUID(@Nonnull String path) {
		return null;
	}

	@Nonnull
	@Override
	public UUID getUUID(@Nonnull String path, @Nonnull UUID def) {
		return def;
	}

	@Nullable
	@Override
	public Date getDate(@Nonnull String path) {
		return null;
	}

	@Nonnull
	@Override
	public Date getDate(@Nonnull String path, @Nonnull Date def) {
		return def;
	}

	@Nullable
	@Override
	public OffsetDateTime getDateTime(@Nonnull String path) {
		return null;
	}

	@Nonnull
	@Override
	public OffsetDateTime getDateTime(@Nonnull String path, @Nonnull OffsetDateTime def) {
		return def;
	}

	@Nullable
	@Override
	public Color getColor(@Nonnull String path) {
		return null;
	}

	@Nonnull
	@Override
	public Color getColor(@Nonnull String path, @Nonnull Color def) {
		return def;
	}

	@Nullable
	@Override
	public <E extends Enum<E>> E getEnum(@Nonnull String path, @Nonnull Class<E> classOfEnum) {
		return null;
	}

	@Nonnull
	@Override
	public <E extends Enum<E>> E getEnum(@Nonnull String path, @Nonnull E def) {
		return def;
	}

	@Nullable
	@Override
	public <T> T getSerializable(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return null;
	}

	@Nonnull
	@Override
	public <T> T getSerializable(@Nonnull String path, @Nonnull T def) {
		return def;
	}

	@Nullable
	@Override
	public Class<?> getClass(@Nonnull String path) {
		return null;
	}

	@Nonnull
	@Override
	public Class<?> getClass(@Nonnull String path, @Nonnull Class<?> def) {
		return def;
	}

	@Nullable
	@Override
	public Version getVersion(@Nonnull String path) {
		return null;
	}

	@Nonnull
	@Override
	public Version getVersion(@Nonnull String path, @Nonnull Version def) {
		return def;
	}

	@Override
	public boolean contains(@Nonnull String path) {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean hasChildren(@Nonnull String path) {
		return false;
	}

	@Override
	public boolean isList(@Nonnull String path) {
		return false;
	}

	@Override
	public boolean isObject(@Nonnull String path) {
		return false;
	}

	@Override
	public boolean isDocument(@Nonnull String path) {
		return false;
	}

	@Override
	public int size() {
		return 0;
	}

	@Nonnull
	@Override
	public Map<String, Object> values() {
		return Collections.emptyMap();
	}

	@Nonnull
	@Override
	public Map<String, String> valuesAsStrings() {
		return new HashMap<>();
	}

	@Nonnull
	@Override
	public Map<String, Document> children() {
		return new HashMap<>();
	}

	@Nonnull
	@Override
	public <K, V> Map<K, V> mapValues(@Nonnull Function<? super String, ? extends K> keyMapper, @Nonnull Function<? super String, ? extends V> valueMapper) {
		return new HashMap<>();
	}

	@Nonnull
	@Override
	public <K, V> Map<K, V> mapDocuments(@Nonnull Function<? super String, ? extends K> keyMapper, @Nonnull Function<? super Document, ? extends V> valueMapper) {
		return new HashMap<>();
	}

	@Nonnull
	@Override
	public Collection<String> keys() {
		return Collections.emptyList();
	}

	@Nonnull
	@Override
	public Set<Entry<String, Object>> entrySet() {
		return Collections.emptySet();
	}

	@Override
	public void forEach(@Nonnull BiConsumer<? super String, ? super Object> action) {
	}

	@Nonnull
	@Override
	public String toJson() {
		return "{}";
	}

	@Nonnull
	@Override
	public String toPrettyJson() {
		return "{}";
	}

	@Nonnull
	@Override
	public String toString() {
		return "{}";
	}

	@Override
	public boolean isReadonly() {
		return true;
	}

	@Nonnull
	@Override
	public Document readonly() {
		return this;
	}

	@Nullable
	@Override
	public Document getParent() {
		return parent;
	}

	@Nonnull
	@Override
	public Document getRoot() {
		return root;
	}

}
