package net.codingarea.commons.database.action;

import net.codingarea.commons.common.concurrent.task.Task;
import net.codingarea.commons.database.action.hierarchy.OrderedAction;
import net.codingarea.commons.database.action.hierarchy.SetAction;
import net.codingarea.commons.database.action.hierarchy.WhereAction;
import net.codingarea.commons.database.exceptions.DatabaseConnectionClosedException;
import net.codingarea.commons.database.exceptions.DatabaseException;
import net.codingarea.commons.database.exceptions.UnsignedDatabaseException;

import javax.annotation.Nonnull;

/**
 * Some action which will be executed on a database.
 *
 * This action is only prepared.
 *
 * It will be executed synchronously by calling {@link #execute()},
 * a {@link DatabaseException} will be thrown when something goes from or
 * a {@link DatabaseConnectionClosedException} will be thrown
 * when the connection to the database is already closed.
 *
 * It will also be executed synchronously by calling {@link #executeUnsigned()} but this method has no signed exceptions ({@code throws} declaration),
 * so when an exception occurs it will be rethrown as a {@link UnsignedDatabaseException}.
 *
 * Calling {@link #executeAsync()} will return a new {@link Task} which will complete when the action is done or fail if something went wrong (a {@link DatabaseException} was thrown).
 *
 * @param <R> The type of the result this action will return
 *
 * @see WhereAction
 * @see SetAction
 * @see OrderedAction
 *
 * @see DatabaseListTables
 * @see DatabaseCountEntries
 * @see DatabaseDeletion
 * @see DatabaseUpdate
 * @see DatabaseInsertion
 * @see DatabaseInsertionOrUpdate
 * @see DatabaseQuery
 */
public interface DatabaseAction<R> {

	/**
	 * Executes this action synchronously
	 *
	 * @return the result of type {@link R} returned by the database
	 *
	 * @throws DatabaseException
	 *         If a database error occurs
	 * @throws DatabaseConnectionClosedException
	 *         If the database is no longer connected
	 */
	R execute() throws DatabaseException;

	/**
	 * Executes this action synchronously without any signed exceptions
	 *
	 * @return the result of type {@link R} returned by the database
	 *
	 * @throws UnsignedDatabaseException
	 *         If a database error occurs
	 */
	default R executeUnsigned() {
		try {
			return execute();
		} catch (DatabaseException ex) {
			throw new UnsignedDatabaseException(ex);
		}
	}

	/**
	 * Executes this action asynchronous
	 *
	 * @return a new {@link Task} which will be completed when the action was executed
	 */
	@Nonnull
	default Task<R> executeAsync() {
		return Task.asyncCall(this::execute);
	}

}
