package net.codingarea.commons.database;

import net.codingarea.commons.database.action.*;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

/**
 * Represents a table/collection of a database
 *
 * @see Database
 * @see Database#getSpecificDatabase(String)
 */
public interface SpecificDatabase {

	boolean isConnected();

	@Nonnull
	String getName();

	/**
	 * @see Database#countEntries(String)
	 */
	@Nonnull
	@CheckReturnValue
	DatabaseCountEntries countEntries();

	/**
	 * @see Database#query(String)
	 */
	@Nonnull
	@CheckReturnValue
	DatabaseQuery query();

	/**
	 * @see Database#update(String)
	 */
	@Nonnull
	@CheckReturnValue
	DatabaseUpdate update();

	/**
	 * @see Database#insert(String)
	 */
	@Nonnull
	@CheckReturnValue
	DatabaseInsertion insert();

	/**
	 * @see Database#insertOrUpdate(String)
	 */
	@Nonnull
	@CheckReturnValue
	DatabaseInsertionOrUpdate insertOrUpdate();

	/**
	 * @see Database#delete(String)
	 */
	@Nonnull
	@CheckReturnValue
	DatabaseDeletion delete();

	@Nonnull
	Database getParent();

}
