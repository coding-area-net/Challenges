package net.codingarea.commons.common.config.document.wrapper;


import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.config.Propertyable;
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

public interface WrappedDocument<D extends Document> extends Document {

	Document getWrappedDocument();

	@Override
	default boolean isReadonly() {
		return getWrappedDocument().isReadonly();
	}

	@Nonnull
	@Override
	default Document getDocument(@Nonnull String path) {
		return getWrappedDocument().getDocument(path);
	}

	@Nonnull
	@Override
	default List<Document> getDocumentList(@Nonnull String path) {
		return getWrappedDocument().getDocumentList(path);
	}

	@Nonnull
	@Override
	default <T> List<T> getSerializableList(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return getWrappedDocument().getSerializableList(path, classOfT);
	}

	@Nonnull
	@Override
	default D set(@Nonnull String path, @Nullable Object value) {
		getWrappedDocument().set(path, value);
		return self();
	}

	@Nonnull
	@Override
	default D set(@Nonnull Object value) {
		getWrappedDocument().set(value);
		return self();
	}

	@Nonnull
	@Override
	default D clear() {
		getWrappedDocument().clear();
		return self();
	}

	@Nonnull
	@Override
	default D remove(@Nonnull String path) {
		getWrappedDocument().remove(path);
		return self();
	}

	@Override
	default void write(@Nonnull Writer writer) throws IOException {
		getWrappedDocument().write(writer);
	}

	@Override
	default void saveToFile(@Nonnull File file) throws IOException {
		getWrappedDocument().saveToFile(file);
	}

	@Nonnull
	@Override
	default String toJson() {
		return getWrappedDocument().toJson();
	}

	@Nonnull
	@Override
	default String toPrettyJson() {
		return getWrappedDocument().toPrettyJson();
	}

	@Override
	default <T> T getInstance(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return getWrappedDocument().getInstance(path, classOfT);
	}

	@Override
	default <T> T toInstanceOf(@Nonnull Class<T> classOfT) {
		return getWrappedDocument().toInstanceOf(classOfT);
	}

	@Nullable
	@Override
	default Object getObject(@Nonnull String path) {
		return getWrappedDocument().getObject(path);
	}

	@Nonnull
	@Override
	default Object getObject(@Nonnull String path, @Nonnull Object def) {
		return getWrappedDocument().getObject(path, def);
	}

	@Nonnull
	@Override
	default <T, O extends Propertyable> Optional<T> getOptional(@Nonnull String key, @Nonnull BiFunction<O, ? super String, ? extends T> extractor) {
		return getWrappedDocument().getOptional(key, extractor);
	}

	@Nullable
	@Override
	default String getString(@Nonnull String path) {
		return getWrappedDocument().getString(path);
	}

	@Nonnull
	@Override
	default String getString(@Nonnull String path, @Nonnull String def) {
		return getWrappedDocument().getString(path, def);
	}

	@Override
	default char getChar(@Nonnull String path) {
		return getWrappedDocument().getChar(path);
	}

	@Override
	default char getChar(@Nonnull String path, char def) {
		return getWrappedDocument().getChar(path, def);
	}

	@Override
	default long getLong(@Nonnull String path) {
		return getWrappedDocument().getLong(path);
	}

	@Override
	default long getLong(@Nonnull String path, long def) {
		return getWrappedDocument().getLong(path, def);
	}

	@Override
	default int getInt(@Nonnull String path) {
		return getWrappedDocument().getInt(path);
	}

	@Override
	default int getInt(@Nonnull String path, int def) {
		return getWrappedDocument().getInt(path, def);
	}

	@Override
	default short getShort(@Nonnull String path) {
		return getWrappedDocument().getShort(path);
	}

	@Override
	default short getShort(@Nonnull String path, short def) {
		return getWrappedDocument().getShort(path, def);
	}

	@Override
	default byte getByte(@Nonnull String path) {
		return getWrappedDocument().getByte(path);
	}

	@Override
	default byte getByte(@Nonnull String path, byte def) {
		return getWrappedDocument().getByte(path, def);
	}

	@Override
	default float getFloat(@Nonnull String path) {
		return getWrappedDocument().getFloat(path);
	}

	@Override
	default float getFloat(@Nonnull String path, float def) {
		return getWrappedDocument().getFloat(path, def);
	}

	@Override
	default double getDouble(@Nonnull String path) {
		return getWrappedDocument().getDouble(path);
	}

	@Override
	default double getDouble(@Nonnull String path, double def) {
		return getWrappedDocument().getDouble(path, def);
	}

	@Override
	default boolean getBoolean(@Nonnull String path) {
		return getWrappedDocument().getBoolean(path);
	}

	@Override
	default boolean getBoolean(@Nonnull String path, boolean def) {
		return getWrappedDocument().getBoolean(path, def);
	}

	@Nonnull
	@Override
	default List<String> getStringList(@Nonnull String path) {
		return getWrappedDocument().getStringList(path);
	}

	@Nonnull
	@Override
	default String[] getStringArray(@Nonnull String path) {
		return getWrappedDocument().getStringArray(path);
	}

	@Nonnull
	@Override
	default <T> List<T> mapList(@Nonnull String path, @Nonnull Function<String, ? extends T> mapper) {
		return getWrappedDocument().mapList(path, mapper);
	}

	@Nonnull
	@Override
	default <E extends Enum<E>> List<E> getEnumList(@Nonnull String path, @Nonnull Class<E> classOfEnum) {
		return getWrappedDocument().getEnumList(path, classOfEnum);
	}

	@Nonnull
	@Override
	default List<UUID> getUUIDList(@Nonnull String path) {
		return getWrappedDocument().getUUIDList(path);
	}

	@Nonnull
	@Override
	default List<Character> getCharacterList(@Nonnull String path) {
		return getWrappedDocument().getCharacterList(path);
	}

	@Nonnull
	@Override
	default List<Byte> getByteList(@Nonnull String path) {
		return getWrappedDocument().getByteList(path);
	}

	@Nonnull
	@Override
	default List<Short> getShortList(@Nonnull String path) {
		return getWrappedDocument().getShortList(path);
	}

	@Nonnull
	@Override
	default List<Integer> getIntegerList(@Nonnull String path) {
		return getWrappedDocument().getIntegerList(path);
	}

	@Nonnull
	@Override
	default List<Long> getLongList(@Nonnull String path) {
		return getWrappedDocument().getLongList(path);
	}

	@Nonnull
	@Override
	default List<Float> getFloatList(@Nonnull String path) {
		return getWrappedDocument().getFloatList(path);
	}

	@Nonnull
	@Override
	default List<Double> getDoubleList(@Nonnull String path) {
		return getWrappedDocument().getDoubleList(path);
	}

	@Nullable
	@Override
	default UUID getUUID(@Nonnull String path) {
		return getWrappedDocument().getUUID(path);
	}

	@Nonnull
	@Override
	default UUID getUUID(@Nonnull String path, @Nonnull UUID def) {
		return getWrappedDocument().getUUID(path, def);
	}

	@Nullable
	@Override
	default Date getDate(@Nonnull String path) {
		return getWrappedDocument().getDate(path);
	}

	@Nonnull
	@Override
	default Date getDate(@Nonnull String path, @Nonnull Date def) {
		return getWrappedDocument().getDate(path, def);
	}

	@Nullable
	@Override
	default OffsetDateTime getDateTime(@Nonnull String path) {
		return getWrappedDocument().getDateTime(path);
	}

	@Nonnull
	@Override
	default OffsetDateTime getDateTime(@Nonnull String path, @Nonnull OffsetDateTime def) {
		return getWrappedDocument().getDateTime(path, def);
	}

	@Nullable
	@Override
	default Color getColor(@Nonnull String path) {
		return getWrappedDocument().getColor(path);
	}

	@Nonnull
	@Override
	default Color getColor(@Nonnull String path, @Nonnull Color def) {
		return getWrappedDocument().getColor(path, def);
	}

	@Nullable
	@Override
	default <E extends Enum<E>> E getEnum(@Nonnull String path, @Nonnull Class<E> classOfEnum) {
		return getWrappedDocument().getEnum(path, classOfEnum);
	}

	@Nonnull
	@Override
	default <E extends Enum<E>> E getEnum(@Nonnull String path, @Nonnull E def) {
		return getWrappedDocument().getEnum(path, def);
	}

	@Nullable
	@Override
	default <T> T getSerializable(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return getWrappedDocument().getSerializable(path, classOfT);
	}

	@Nonnull
	@Override
	default <T> T getSerializable(@Nonnull String path, @Nonnull T def) {
		return getWrappedDocument().getSerializable(path, def);
	}

	@Nullable
	@Override
	default Class<?> getClass(@Nonnull String path) {
		return getWrappedDocument().getClass(path);
	}

	@Nonnull
	@Override
	default Class<?> getClass(@Nonnull String path, @Nonnull Class<?> def) {
		return getWrappedDocument().getClass(path, def);
	}

	@Nullable
	@Override
	default Version getVersion(@Nonnull String path) {
		return getWrappedDocument().getVersion(path);
	}

	@Nonnull
	@Override
	default Version getVersion(@Nonnull String path, @Nonnull Version def) {
		return getWrappedDocument().getVersion(path, def);
	}

	@Nullable
	@Override
	default byte[] getBinary(@Nonnull String path) {
		return getWrappedDocument().getBinary(path);
	}

	@Override
	default boolean contains(@Nonnull String path) {
		return getWrappedDocument().contains(path);
	}

	@Override
	default boolean hasChildren(@Nonnull String path) {
		return getWrappedDocument().hasChildren(path);
	}

	@Override
	default boolean isObject(@Nonnull String path) {
		return getWrappedDocument().isObject(path);
	}

	@Override
	default boolean isList(@Nonnull String path) {
		return getWrappedDocument().isList(path);
	}

	@Override
	default boolean isDocument(@Nonnull String path) {
		return getWrappedDocument().isDocument(path);
	}

	@Override
	default boolean isEmpty() {
		return getWrappedDocument().isEmpty();
	}

	@Override
	default int size() {
		return getWrappedDocument().size();
	}

	@Nonnull
	@Override
	default Map<String, Object> values() {
		return getWrappedDocument().values();
	}

	@Nonnull
	@Override
	default Map<String, String> valuesAsStrings() {
		return getWrappedDocument().valuesAsStrings();
	}

	@Nonnull
	@Override
	default Map<String, Document> children() {
		return getWrappedDocument().children();
	}

	@Nonnull
	@Override
	default <K, V> Map<K, V> mapValues(@Nonnull Function<? super String, ? extends K> keyMapper, @Nonnull Function<? super String, ? extends V> valueMapper) {
		return getWrappedDocument().mapValues(keyMapper, valueMapper);
	}

	@Nonnull
	@Override
	default <K, V> Map<K, V> mapDocuments(@Nonnull Function<? super String, ? extends K> keyMapper, @Nonnull Function<? super Document, ? extends V> valueMapper) {
		return getWrappedDocument().mapDocuments(keyMapper, valueMapper);
	}

	@Nonnull
	@Override
	default <R> R mapDocument(@Nonnull String path, @Nonnull Function<? super Document, ? extends R> mapper) {
		return getWrappedDocument().mapDocument(path, mapper);
	}

	@Nullable
	@Override
	default <R> R mapDocumentNullable(@Nonnull String path, @Nonnull Function<? super Document, ? extends R> mapper) {
		return getWrappedDocument().mapDocumentNullable(path, mapper);
	}

	@Nonnull
	@Override
	default Collection<String> keys() {
		return getWrappedDocument().keys();
	}

	@Nonnull
	@Override
	default Set<Entry<String, Object>> entrySet() {
		return getWrappedDocument().entrySet();
	}

	@Override
	default void forEach(@Nonnull BiConsumer<? super String, ? super Object> action) {
		getWrappedDocument().forEach(action);
	}

	@Nonnull
	@Override
	default Document readonly() {
		return getWrappedDocument().readonly();
	}

	@Nullable
	@Override
	default Document getParent() {
		return getWrappedDocument().getParent();
	}

	@Nonnull
	@Override
	default Document getRoot() {
		return getWrappedDocument().getRoot();
	}

	@SuppressWarnings("unchecked")
	default D self() {
		return (D) this;
	}

}
