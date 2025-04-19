package net.codingarea.commons.common.config;

import com.google.gson.JsonArray;
import net.codingarea.commons.common.collection.WrappedException;
import net.codingarea.commons.common.config.document.EmptyDocument;
import net.codingarea.commons.common.config.document.GsonDocument;
import net.codingarea.commons.common.config.document.PropertiesDocument;
import net.codingarea.commons.common.misc.FileUtils;
import net.codingarea.commons.common.misc.GsonUtils;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Document extends Config, Json {

	/**
	 * Gets the document located at the given path.
	 * If there is no document assigned to this path,
	 * a new document is being created and assigned to this path.
	 *
	 * @return the document assigned to this path
	 */
	@Nonnull
	Document getDocument(@Nonnull String path);

	/**
	 * Returns the list of documents located at the given path.
	 * The returned list will not contain any null elements.
	 * If there are no documents, the list will be empty.
	 * Elements which are no documents in the original list will be skipped.
	 * If an element is {@code null} an empty document will be added
	 * if this document is not {@link #isReadonly() readonly}.
	 *
	 * @return the list of documents assigned to this path
	 */
	@Nonnull
	List<Document> getDocumentList(@Nonnull String path);

	@Nonnull
	default <T> List<T> getInstanceList(@Nonnull String path, @Nonnull Class<T> classOfT) {
		List<Document> documents = getDocumentList(path);
		List<T> result = new ArrayList<>(documents.size());
		for (Document document : documents) {
			result.add(document.toInstanceOf(classOfT));
		}
		return result;
	}

	@Nonnull
	<T> List<T> getSerializableList(@Nonnull String path, @Nonnull Class<T> classOfT);

	@Nullable
	<T> T getSerializable(@Nonnull String path, @Nonnull Class<T> classOfT);

	@Nonnull
	<T> T getSerializable(@Nonnull String path, @Nonnull T def);

	@Nonnull
	<K, V> Map<K, V> mapDocuments(@Nonnull Function<? super String, ? extends K> keyMapper, @Nonnull Function<? super Document, ? extends V> valueMapper);

	@Nonnull
	<R> R mapDocument(@Nonnull String path, @Nonnull Function<? super Document, ? extends R> mapper);

	@Nullable
	<R> R mapDocumentNullable(@Nonnull String path, @Nonnull Function<? super Document, ? extends R> mapper);

	@Nullable
	<T> T toInstanceOf(@Nonnull Class<T> classOfT);

	/**
	 * Returns the parent document of this document.
	 * If {@code this} is the {@link #getRoot() root} this method will return {@code null}.
	 *
	 * @return the parent document of this document
	 */
	@Nullable
	Document getParent();

	/**
	 * Returns the root document of this document.
	 * If the {@link #getParent() parent} is {@code null} this will return {@code this}.
	 *
	 * @return the root document of this document
	 */
	@Nonnull
	Document getRoot();

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	@Override
	Document set(@Nonnull String path, @Nullable Object value);

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	@Override
	Document clear();

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	@Override
	Document remove(@Nonnull String path);

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	@Override
	default <O extends Propertyable> Document apply(@Nonnull Consumer<O> action) {
		return (Document) Config.super.apply(action);
	}

	@Nonnull
	@Override
	default <O extends Propertyable> Document applyIf(boolean expression, @Nonnull Consumer<O> action) {
		return (Document) Config.super.applyIf(expression, action);
	}

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	@Override
	default Document setIfAbsent(@Nonnull String path, @Nonnull Object defaultValue) {
		return (Document) Config.super.setIfAbsent(path, defaultValue);
	}

	@Nonnull
	Document set(@Nonnull Object value);

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	@Override
	@CheckReturnValue
	Document readonly();

	@Nonnull
	Map<String, Document> children();

	boolean isDocument(@Nonnull String path);

	boolean hasChildren(@Nonnull String path);

	void write(@Nonnull Writer writer) throws IOException;

	default void saveToFile(@Nonnull File file) throws IOException {
		FileUtils.createFilesIfNecessary(file);
		Writer writer = FileUtils.newBufferedWriter(file);
		write(writer);
		writer.flush();
		writer.close();
	}

	default void saveToFile(@Nonnull Path file) throws IOException {
		FileUtils.createFile(file);
		Writer writer = FileUtils.newBufferedWriter(file);
		write(writer);
		writer.flush();
		writer.close();
	}

	@Nonnull
	@CheckReturnValue
	default FileDocument asFileDocument(@Nonnull File file) {
		return (this instanceof FileDocument && ((FileDocument)this).getFile().equals(file))
			 ? (FileDocument) this : FileDocument.wrap(this, file);
	}

	@Nonnull
	@CheckReturnValue
	default FileDocument asFileDocument(@Nonnull Path file) {
		return asFileDocument(file.toFile());
	}

	@Nonnull
	@CheckReturnValue
	default Document copyJson() {
		Document document = create();
		this.forEach(document::set);
		return document;
	}

	/**
	 * @return an empty and immutable document
	 *
	 * @see EmptyDocument
	 */
	@Nonnull
	@CheckReturnValue
	static Document empty() {
		return EmptyDocument.ROOT;
	}

	@Nonnull
	@CheckReturnValue
	static Document readFile(@Nonnull Class<? extends Document> classOfDocument, @Nonnull File file) {
		try {
			if (file.exists()) {
				Constructor<? extends Document> constructor = classOfDocument.getConstructor(File.class);
				return constructor.newInstance(file);
			} else {
				Constructor<? extends Document> constructor = classOfDocument.getConstructor();
				return constructor.newInstance();
			}
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException ex) {
			throw new UnsupportedOperationException(classOfDocument.getName() + " does not support File creation");
		} catch (InvocationTargetException ex) {
			throw new WrappedException(ex);
		}
	}

	@Nonnull
	@CheckReturnValue
	static Document readFile(@Nonnull Class<? extends Document> classOfDocument, @Nonnull Path file) {
		return readFile(classOfDocument, file.toFile());
	}

	/**
	 * @return a json document parsed by the input
	 *
	 * @see GsonDocument
	 */
	@Nonnull
	@CheckReturnValue
	static Document parseJson(@Nonnull String jsonInput) {
		return new GsonDocument(jsonInput);
	}

	@Nonnull
	@CheckReturnValue
	static Document parseJson(@Nonnull Reader reader) throws IOException {
		return new GsonDocument(reader);
	}

	@Nonnull
	@CheckReturnValue
	static Document parseJson(@Nonnull InputStream input) throws IOException {
		return new GsonDocument(new InputStreamReader(input, StandardCharsets.UTF_8));
	}

	@Nonnull
	@CheckReturnValue
	static List<Document> parseJsonArray(@Nonnull String jsonInput) {
		return GsonDocument.convertArrayToDocuments(GsonDocument.GSON.fromJson(jsonInput, JsonArray.class));
	}

	@Nonnull
	@CheckReturnValue
	static List<String> parseStringArray(@Nonnull String jsonInput) {
		return GsonDocument.convertArrayToStrings(GsonDocument.GSON.fromJson(jsonInput, JsonArray.class));
	}

	static void saveArray(@Nonnull Iterable<?> objects, @Nonnull Path file) throws IOException {
		FileUtils.createFile(file);
		Writer writer = FileUtils.newBufferedWriter(file);
		JsonArray array = GsonUtils.convertIterableToJsonArray(GsonDocument.GSON, objects);
		(GsonDocument.isWritePrettyJson() ? GsonDocument.GSON_PRETTY_PRINT : GsonDocument.GSON).toJson(array, writer);
		writer.flush();
		writer.close();
	}

	@Nonnull
	@CheckReturnValue
	static Document readJsonFile(@Nonnull File file) {
		return readFile(GsonDocument.class, file);
	}

	@Nonnull
	@CheckReturnValue
	static Document readJsonFile(@Nonnull Path file) {
		return readJsonFile(file.toFile());
	}

	@Nonnull
	@CheckReturnValue
	static List<Document> readJsonArrayFile(@Nonnull Path file) {
		try {
			JsonArray array = GsonDocument.GSON.fromJson(FileUtils.newBufferedReader(file), JsonArray.class);
			if (array == null) return new ArrayList<>();
			List<Document> documents = new ArrayList<>(array.size());
			array.forEach(element -> documents.add(new GsonDocument(element.getAsJsonObject())));
			return documents;
		} catch (IOException ex) {
			throw new WrappedException(ex);
		}
	}

	@Nonnull
	@CheckReturnValue
	static Document readPropertiesFile(@Nonnull File file) {
		return readFile(PropertiesDocument.class, file);
	}

	@Nonnull
	@CheckReturnValue
	static Document readPropertiesFile(@Nonnull Path file) {
		return readPropertiesFile(file.toFile());
	}

	@Nonnull
	@CheckReturnValue
	static Document create() {
		return new GsonDocument();
	}

	@Nonnull
	@CheckReturnValue
	static Document of(@Nonnull Object object) {
		return new GsonDocument(object);
	}

	@Nullable
	@CheckReturnValue
	static Document ofNullable(@Nullable Object object) {
		return object == null ? null : of(object);
	}

	@Nonnull
	@CheckReturnValue
	static List<Document> arrayOf(@Nonnull Collection<?> objects) {
		List<Document> documents = new ArrayList<>(objects.size());
		objects.forEach(object -> documents.add(Document.of(object)));
		return documents;
	}

}
