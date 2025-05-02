package net.codingarea.commons.database.sql.abstraction.insertion;

import net.codingarea.commons.database.action.DatabaseInsertion;
import net.codingarea.commons.database.exceptions.DatabaseException;
import net.codingarea.commons.database.sql.abstraction.AbstractSQLDatabase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class SQLInsertion implements DatabaseInsertion {

	protected final Map<String, Object> values;
	protected final AbstractSQLDatabase database;
	protected final String table;

	public SQLInsertion(@Nonnull AbstractSQLDatabase database, @Nonnull String table) {
		this.database = database;
		this.table = table;
		this.values = new HashMap<>();
	}

	public SQLInsertion(@Nonnull AbstractSQLDatabase database, @Nonnull String table, @Nonnull Map<String, Object> values) {
		this.database = database;
		this.table = table;
		this.values = values;
	}

	@Nonnull
	@Override
	public DatabaseInsertion set(@Nonnull String field, @Nullable Object value) {
		values.put(field, value);
		return this;
	}

	@Nonnull
	protected PreparedStatement prepare() throws SQLException, DatabaseException {
		if (values.isEmpty()) throw new IllegalArgumentException("Cannot insert nothing");

		StringBuilder command = new StringBuilder();
		List<Object> args = new ArrayList<>(values.size());

		command.append("INSERT INTO ");
		command.append(table);
		command.append(" (");
		{
			int index = 0;
			for (String column : values.keySet()) {
				if (index > 0) command.append(", ");
				command.append(column);
				index++;
			}
		}
		command.append(") VALUES (");
		{
			int index = 0;
			for (Object value : values.values()) {
				if (index > 0) command.append(", ");
				command.append("?");
				args.add(value);
				index++;
			}
		}
		command.append(")");

		return database.prepare(command.toString(), args.toArray());
	}

	@Override
	public Void execute() throws DatabaseException {
		try {
			PreparedStatement statement = prepare();
			statement.execute();
			return null;
		} catch (Exception ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SQLInsertion that = (SQLInsertion) o;
		return values.equals(that.values) && database.equals(that.database) && table.equals(that.table);
	}

	@Override
	public int hashCode() {
		return Objects.hash(values, database, table);
	}

}
