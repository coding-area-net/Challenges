package net.codingarea.commons.database.sql.abstraction.insertorupdate;

import net.codingarea.commons.database.action.DatabaseInsertionOrUpdate;
import net.codingarea.commons.database.exceptions.DatabaseException;
import net.codingarea.commons.database.sql.abstraction.AbstractSQLDatabase;
import net.codingarea.commons.database.sql.abstraction.update.SQLUpdate;
import net.codingarea.commons.database.sql.abstraction.where.SQLWhere;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SQLInsertionOrUpdate extends SQLUpdate implements DatabaseInsertionOrUpdate {

	public SQLInsertionOrUpdate(@Nonnull AbstractSQLDatabase database, @Nonnull String table) {
		super(database, table);
	}

	@Nonnull
	@Override
	public DatabaseInsertionOrUpdate where(@Nonnull String column, @Nullable Object value) {
		super.where(column, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseInsertionOrUpdate where(@Nonnull String column, @Nullable Number value) {
		super.where(column, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseInsertionOrUpdate where(@Nonnull String column, @Nullable String value, boolean ignoreCase) {
		super.where(column, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseInsertionOrUpdate where(@Nonnull String column, @Nullable String value) {
		super.where(column, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseInsertionOrUpdate whereNot(@Nonnull String column, @Nullable Object value) {
		super.whereNot(column, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseInsertionOrUpdate set(@Nonnull String column, @Nullable Object value) {
		super.set(column, value);
		return this;
	}

	@Override
	public Void execute() throws DatabaseException {
		if (database.query(table, where).execute().isSet()) {
			return super.execute();
		} else {
			Map<String, Object> insert = new HashMap<>(values);
			for (Entry<String, SQLWhere> entry : where.entrySet()) {
				Object[] args = entry.getValue().getArgs();
				if (args.length == 0) continue;
				insert.put(entry.getKey(), args[0]);
			}

			database.insert(table, insert).execute();
			return null;
		}
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

}
