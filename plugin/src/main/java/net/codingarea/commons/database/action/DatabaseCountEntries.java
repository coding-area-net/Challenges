package net.codingarea.commons.database.action;

import net.codingarea.commons.database.Database;
import net.codingarea.commons.database.SpecificDatabase;
import net.codingarea.commons.database.exceptions.DatabaseException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * @see Database#countEntries(String)
 * @see SpecificDatabase#countEntries()
 */
public interface DatabaseCountEntries extends DatabaseAction<Long> {

	@Nonnull
	@Override
	@Nonnegative
	Long execute() throws DatabaseException;

}
