package net.codingarea.commons.common.config.document.readonly;

import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.config.document.wrapper.WrappedDocument;

import javax.annotation.Nonnull;

public final class ReadOnlyDocumentWrapper implements WrappedDocument<Document> {

	private final Document document;

	public ReadOnlyDocumentWrapper(@Nonnull Document document) {
		this.document = document;
	}

	@Override
	public Document getWrappedDocument() {
		return document;
	}

	@Override
	public boolean isReadonly() {
		return true;
	}

}
