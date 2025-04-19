package net.codingarea.commons.database.exceptions;

import javax.annotation.Nonnull;

public class DatabaseUnsupportedFeatureException extends DatabaseException {

	public DatabaseUnsupportedFeatureException() {
	}

	public DatabaseUnsupportedFeatureException(@Nonnull String message) {
		super(message);
	}

}
