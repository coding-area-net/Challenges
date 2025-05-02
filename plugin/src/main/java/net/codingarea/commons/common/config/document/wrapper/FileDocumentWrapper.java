package net.codingarea.commons.common.config.document.wrapper;

import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.config.FileDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;

public class FileDocumentWrapper implements WrappedDocument<FileDocument>, FileDocument {

	protected final Document document;
	protected final File file;

	public FileDocumentWrapper(@Nonnull File file, @Nonnull Document document) {
		this.file = file;
		this.document = document;
	}

	@Override
	public Document getWrappedDocument() {
		return document;
	}

	@Nonnull
	@Override
	public File getFile() {
		return file;
	}

	@Nonnull
	@Override
	public Path getPath() {
		return file.toPath();
	}

	@Nonnull
	@Override
	public FileDocument set(@Nonnull String path, @Nullable Object value) {
		return WrappedDocument.super.set(path, value);
	}

	@Nonnull
	@Override
	public FileDocument set(@Nonnull Object value) {
		return WrappedDocument.super.set(value);
	}

	@Nonnull
	@Override
	public FileDocument clear() {
		return WrappedDocument.super.clear();
	}

	@Nonnull
	@Override
	public FileDocument remove(@Nonnull String path) {
		return WrappedDocument.super.remove(path);
	}

}
