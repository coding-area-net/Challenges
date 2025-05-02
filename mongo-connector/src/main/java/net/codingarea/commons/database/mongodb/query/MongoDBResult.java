package net.codingarea.commons.database.mongodb.query;

import net.codingarea.commons.common.config.document.BsonDocument;

import javax.annotation.Nonnull;

public final class MongoDBResult extends BsonDocument {

	public MongoDBResult(@Nonnull org.bson.Document bsonDocument) {
		super(bsonDocument);
	}

	@Override
	public boolean isReadonly() {
		return true;
	}

}
