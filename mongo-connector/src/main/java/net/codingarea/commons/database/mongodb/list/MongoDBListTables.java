package net.codingarea.commons.database.mongodb.list;

import net.codingarea.commons.database.action.DatabaseListTables;
import net.codingarea.commons.database.exceptions.DatabaseException;
import net.codingarea.commons.database.mongodb.MongoDBDatabase;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MongoDBListTables implements DatabaseListTables {

	protected final MongoDBDatabase database;

	public MongoDBListTables(@Nonnull MongoDBDatabase database) {
		this.database = database;
	}

	@Nonnull
	@Override
	public List<String> execute() throws DatabaseException {
		try {
			return database.getDatabase().listCollectionNames().into(new ArrayList<>());
		} catch (Exception ex) {
			throw new DatabaseException(ex);
		}
	}

}
