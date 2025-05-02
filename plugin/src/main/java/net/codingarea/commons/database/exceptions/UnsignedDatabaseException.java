package net.codingarea.commons.database.exceptions;

import net.codingarea.commons.common.collection.WrappedException;
import net.codingarea.commons.database.action.DatabaseAction;

import javax.annotation.Nonnull;

/**
 * @see DatabaseException
 *
 * @see DatabaseAction#executeUnsigned()
 */
public class UnsignedDatabaseException extends WrappedException {

	public UnsignedDatabaseException(@Nonnull DatabaseException cause) {
		super(cause);
	}

	@Nonnull
	@Override
	public DatabaseException getCause() {
		return (DatabaseException) super.getCause();
	}
}
