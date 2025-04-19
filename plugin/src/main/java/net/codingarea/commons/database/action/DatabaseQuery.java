package net.codingarea.commons.database.action;

import net.codingarea.commons.database.Database;
import net.codingarea.commons.database.Order;
import net.codingarea.commons.database.SpecificDatabase;
import net.codingarea.commons.database.action.hierarchy.OrderedAction;
import net.codingarea.commons.database.action.hierarchy.WhereAction;
import net.codingarea.commons.database.exceptions.DatabaseException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @see Database#query(String)
 * @see SpecificDatabase#query()
 */
public interface DatabaseQuery extends DatabaseAction<ExecutedQuery>, WhereAction, OrderedAction {

	@Nonnull
	@CheckReturnValue
	DatabaseQuery where(@Nonnull String field, @Nullable Object object);

	@Nonnull
	@CheckReturnValue
	DatabaseQuery where(@Nonnull String field, @Nullable Number value);

	@Nonnull
	@CheckReturnValue
	DatabaseQuery where(@Nonnull String field, @Nullable String value, boolean ignoreCase);

	@Nonnull
	@CheckReturnValue
	DatabaseQuery where(@Nonnull String field, @Nullable String value);

	@Nonnull
	@CheckReturnValue
	DatabaseQuery whereNot(@Nonnull String field, @Nullable Object value);

	@Nonnull
	@CheckReturnValue
	DatabaseQuery select(@Nonnull String... selection);

	@Nonnull
	@CheckReturnValue
	DatabaseQuery orderBy(@Nonnull String field, @Nonnull Order order);

	@Nonnull
	@Override
	@CheckReturnValue
	ExecutedQuery execute() throws DatabaseException;

}
