package net.codingarea.commons.database.sql.sqlite;

import net.codingarea.commons.common.misc.FileUtils;
import net.codingarea.commons.database.DatabaseConfig;
import net.codingarea.commons.database.action.DatabaseListTables;
import net.codingarea.commons.database.exceptions.DatabaseException;
import net.codingarea.commons.database.sql.abstraction.AbstractSQLDatabase;
import net.codingarea.commons.database.sql.sqlite.list.SQLiteListTables;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

public class SQLiteDatabase extends AbstractSQLDatabase {

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException ex) {
			LOGGER.error("Could not load sqlite driver");
		}
	}

	protected final File file;

	public SQLiteDatabase(@Nonnull DatabaseConfig config) {
		super(config);
		file = new File(config.getFile());
	}

	@Override
	public void connect() throws DatabaseException {
		try {
			FileUtils.createFilesIfNecessary(file);
		} catch (IOException ex) {
			throw new DatabaseException(ex);
		}

		super.connect();
	}

	@Override
	protected String createUrl() {
		return "jdbc:sqlite:" + file;
	}

	@Nonnull
	@Override
	public DatabaseListTables listTables() {
		return new SQLiteListTables(this);
	}

}
