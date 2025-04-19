package net.codingarea.commons.common.config;

import net.codingarea.commons.common.concurrent.task.Task;
import net.codingarea.commons.common.config.document.GsonDocument;
import net.codingarea.commons.common.config.document.PropertiesDocument;
import net.codingarea.commons.common.config.document.wrapper.FileDocumentWrapper;
import net.codingarea.commons.common.logging.ILogger;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public interface FileDocument extends Document {

	/**
	 * Logger used to log errors which are caught in the safe version of an exceptionally method
	 */
	ILogger LOGGER = ILogger.forThisClass();

	default void saveExceptionally() throws IOException {
		saveToFile(getFile());
	}

	/**
	 * Executes {@link #saveExceptionally()} and prints all caught errors to {@link #LOGGER}
	 *
	 * @see #saveExceptionally()
	 */
	default void save() {
		try {
			saveExceptionally();
		} catch (IOException ex) {
			LOGGER.error("Could not save config to file \"{}\"", getFile(), ex);
		}
	}

	@Nonnull
	default Task<Void> saveAsync() {
		return Task.asyncRunExceptionally(this::save);
	}

	/**
	 * @param async whether this should be saved asynchronously or synchronously
	 *
	 * @see #save()
	 * @see #saveAsync()
	 */
	default void save(boolean async) {
		if (async)  saveAsync();
		else        save();
	}

	@Nonnull
	File getFile();

	@Nonnull
	Path getPath();

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	@Override
	FileDocument set(@Nonnull String path, @Nullable Object value);

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	@Override
	FileDocument set(@Nonnull Object value);

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	@Override
	FileDocument clear();

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	@Override
	FileDocument remove(@Nonnull String path);

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	@Override
	default <O extends Propertyable> FileDocument apply(@Nonnull Consumer<O> action) {
		return (FileDocument) Document.super.apply(action);
	}

	@Nonnull
	@Override
	default FileDocument setIfAbsent(@Nonnull String path, @Nonnull Object defaultValue) {
		return (FileDocument) Document.super.setIfAbsent(path, defaultValue);
	}

	@Nonnull
	@Override
	default FileDocument increment(@Nonnull String path, double amount) {
		return (FileDocument) Document.super.increment(path, amount);
	}

	@Nonnull
	@Override
	default FileDocument decrement(@Nonnull String path, double amount) {
		return (FileDocument) Document.super.decrement(path, amount);
	}

	@Nonnull
	@Override
	default FileDocument multiply(@Nonnull String path, double factor) {
		return (FileDocument) Document.super.multiply(path, factor);
	}

	@Nonnull
	@Override
	default FileDocument divide(@Nonnull String path, double divisor) {
		return (FileDocument) Document.super.divide(path, divisor);
	}

	@Nonnull
	@CheckReturnValue
	static FileDocument wrap(@Nonnull Document document, @Nonnull File file) {
		return new FileDocumentWrapper(file, document);
	}

	@Nonnull
	@CheckReturnValue
	static FileDocument readFile(@Nonnull Class<? extends Document> classOfDocument, @Nonnull File file) {
		return Document.readFile(classOfDocument, file).asFileDocument(file);
	}

	@Nonnull
	@CheckReturnValue
	static FileDocument readFile(@Nonnull Class<? extends Document> classOfDocument, @Nonnull Path file) {
		return Document.readFile(classOfDocument, file).asFileDocument(file);
	}

	@Nonnull
	@CheckReturnValue
	static FileDocument readJsonFile(@Nonnull File file) {
		return readFile(GsonDocument.class, file);
	}

	@Nonnull
	@CheckReturnValue
	static FileDocument readJsonFile(@Nonnull Path file) {
		return readFile(GsonDocument.class, file);
	}

	@Nonnull
	@CheckReturnValue
	static FileDocument readPropertiesFile(@Nonnull File file) {
		return readFile(PropertiesDocument.class, file);
	}

	@Nonnull
	@CheckReturnValue
	static FileDocument readPropertiesFile(@Nonnull Path file) {
		return readFile(PropertiesDocument.class, file);
	}

}
