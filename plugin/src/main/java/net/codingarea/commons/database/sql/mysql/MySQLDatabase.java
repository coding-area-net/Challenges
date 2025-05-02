package net.codingarea.commons.database.sql.mysql;

import net.codingarea.commons.database.DatabaseConfig;
import net.codingarea.commons.database.action.DatabaseListTables;
import net.codingarea.commons.database.sql.abstraction.AbstractSQLDatabase;
import net.codingarea.commons.database.sql.mysql.list.MySQLListTables;

import javax.annotation.Nonnull;

public class MySQLDatabase extends AbstractSQLDatabase {

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			LOGGER.error("Could not load mysql driver");
		}
	}

	public MySQLDatabase(@Nonnull DatabaseConfig config) {
		super(config);
	}

	@Nonnull
	@Override
	protected String createUrl() {
		return "jdbc:mysql://" + config.getHost() + (config.isPortSet() ? ":" + config.getPort() : "") + "/" + config.getDatabase();
	}

	@Nonnull
	@Override
	public DatabaseListTables listTables() {
		return new MySQLListTables(this);
	}

}
