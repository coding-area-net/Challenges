package net.codingarea.commons.database.action;

import net.codingarea.commons.database.Database;
import net.codingarea.commons.database.SpecificDatabase;
import net.codingarea.commons.database.action.hierarchy.SetAction;
import net.codingarea.commons.database.exceptions.DatabaseException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @see Database#insert(String)
 * @see SpecificDatabase#insert()
 */
public interface DatabaseInsertion extends DatabaseAction<Void>, SetAction {

	@Nonnull
	@CheckReturnValue
	DatabaseInsertion set(@Nonnull String field, @Nullable Object value);

	@Nullable
	@Override
	Void execute() throws DatabaseException;

}
