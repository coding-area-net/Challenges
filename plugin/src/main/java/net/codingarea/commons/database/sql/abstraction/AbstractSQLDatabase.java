package net.codingarea.commons.database.sql.abstraction;

import net.codingarea.commons.database.DatabaseConfig;
import net.codingarea.commons.database.SQLColumn;
import net.codingarea.commons.database.action.*;
import net.codingarea.commons.database.exceptions.DatabaseException;
import net.codingarea.commons.database.abstraction.AbstractDatabase;
import net.codingarea.commons.database.sql.abstraction.count.SQLCountEntries;
import net.codingarea.commons.database.sql.abstraction.deletion.SQLDeletion;
import net.codingarea.commons.database.sql.abstraction.insertion.SQLInsertion;
import net.codingarea.commons.database.sql.abstraction.insertorupdate.SQLInsertionOrUpdate;
import net.codingarea.commons.database.sql.abstraction.query.SQLQuery;
import net.codingarea.commons.database.sql.abstraction.update.SQLUpdate;
import net.codingarea.commons.database.sql.abstraction.where.SQLWhere;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public abstract class AbstractSQLDatabase extends AbstractDatabase {

	protected Connection connection;

	public AbstractSQLDatabase(@Nonnull DatabaseConfig config) {
		super(config);
	}

	@Override
	public void disconnect0() throws Exception {
		connection.close();
		connection = null;
	}

	@Override
	public void connect0() throws Exception {
		connection = DriverManager.getConnection(createUrl(), config.getUser(), config.getPassword());
	}

	protected abstract String createUrl();

	@Override
	public boolean isConnected() {
		try {
			if (connection == null) return false;
			connection.isClosed();
			return true;
		} catch (SQLException ex) {
			LOGGER.error("Could not check connection state: " + ex.getMessage());
			return false;
		}
	}

	@Override
	public void createTable(@Nonnull String name, @Nonnull SQLColumn... columns) throws DatabaseException {
		try {
			StringBuilder command = new StringBuilder();
			command.append("CREATE TABLE IF NOT EXISTS `");
			command.append(name);
			command.append("` (");
			{
				int index = 0;
				for (SQLColumn column : columns) {
					if (index > 0) command.append(", ");
					command.append(column);
					index++;
				}
			}
			command.append(")");

			PreparedStatement statement = prepare(command.toString());
			statement.execute();
		} catch (Exception ex) {
			throw new DatabaseException(ex);
		}
	}

	@Nonnull
	@Override
	public DatabaseCountEntries countEntries(@Nonnull String table) {
		return new SQLCountEntries(this, table);
	}

	@Nonnull
	@Override
	public DatabaseQuery query(@Nonnull String table) {
		return new SQLQuery(this, table);
	}

	@Nonnull
	public DatabaseQuery query(@Nonnull String table, @Nonnull Map<String, SQLWhere> where) {
		return new SQLQuery(this, table, where);
	}

	@Nonnull
	@Override
	public DatabaseUpdate update(@Nonnull String table) {
		return new SQLUpdate(this, table);
	}

	@Nonnull
	@Override
	public DatabaseInsertion insert(@Nonnull String table) {
		return new SQLInsertion(this, table);
	}

	@Nonnull
	public DatabaseInsertion insert(@Nonnull String table, @Nonnull Map<String, Object> values) {
		return new SQLInsertion(this, table, values);
	}

	@Nonnull
	@Override
	public DatabaseInsertionOrUpdate insertOrUpdate(@Nonnull String table) {
		return new SQLInsertionOrUpdate(this, table);
	}

	@Nonnull
	@Override
	public DatabaseDeletion delete(@Nonnull String table) {
		return new SQLDeletion(this, table);
	}

	@Nonnull
	public PreparedStatement prepare(@Nonnull CharSequence command, @Nonnull Object... args) throws SQLException, DatabaseException {
		checkConnection();
		PreparedStatement statement = connection.prepareStatement(command.toString());
		SQLHelper.fillParams(statement, args);
		return statement;
	}

}
