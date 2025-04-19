package net.codingarea.commons.database.mongodb.insertion;

import net.codingarea.commons.common.misc.MongoUtils;
import net.codingarea.commons.database.action.DatabaseInsertion;
import net.codingarea.commons.database.exceptions.DatabaseException;
import net.codingarea.commons.database.mongodb.MongoDBDatabase;
import org.bson.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class MongoDBInsertion implements DatabaseInsertion {

	protected final MongoDBDatabase database;
	protected final String collection;
	protected final Document values;

	public MongoDBInsertion(@Nonnull MongoDBDatabase database, @Nonnull String collection) {
		this.database = database;
		this.collection = collection;
		this.values = new Document();
	}

	public MongoDBInsertion(@Nonnull MongoDBDatabase database, @Nonnull String collection, @Nonnull Document values) {
		this.database = database;
		this.collection = collection;
		this.values = values;
	}

	@Nonnull
	@Override
	public DatabaseInsertion set(@Nonnull String field, @Nullable Object value) {
		values.put(field, MongoUtils.packObject(value));
		return this;
	}

	@Override
	public Void execute() throws DatabaseException {
		try {
			database.getCollection(collection).insertOne(values);
			return null;
		} catch (Exception ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MongoDBInsertion that = (MongoDBInsertion) o;
		return database.equals(that.database) && collection.equals(that.collection) && values.equals(that.values);
	}

	@Override
	public int hashCode() {
		return Objects.hash(database, collection, values);
	}

}
