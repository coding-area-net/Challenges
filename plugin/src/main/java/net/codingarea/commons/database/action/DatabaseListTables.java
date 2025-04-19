package net.codingarea.commons.database.action;

import net.codingarea.commons.database.Database;
import net.codingarea.commons.database.exceptions.DatabaseException;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @see Database#listTables()
 */
public interface DatabaseListTables extends DatabaseAction<List<String>> {

	@Nonnull
	@Override
	List<String> execute() throws DatabaseException;

}
