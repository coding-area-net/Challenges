package net.codingarea.commons.database.sql.abstraction.query;

import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.config.document.EmptyDocument;
import net.codingarea.commons.common.config.document.GsonDocument;
import net.codingarea.commons.common.config.document.MapDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public final class SQLResult extends MapDocument {

	public SQLResult(@Nonnull Map<String, Object> values) {
		super(values);
	}

	@Nonnull
	@Override
	public Document getDocument0(@Nonnull String path, @Nonnull Document root, @Nullable Document parent) {
		try {
			return new GsonDocument(Objects.requireNonNull(getString(path)), this, this).readonly();
		} catch (Exception ex) {
			return new EmptyDocument(this, null);
		}
	}

	@Override
	public boolean isReadonly() {
		return true;
	}

}
