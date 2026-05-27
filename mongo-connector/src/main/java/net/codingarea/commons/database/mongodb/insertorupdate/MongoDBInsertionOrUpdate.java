package net.codingarea.commons.database.mongodb.insertorupdate;

import net.codingarea.commons.common.misc.BsonUtils;
import net.codingarea.commons.database.action.DatabaseInsertionOrUpdate;
import net.codingarea.commons.database.exceptions.DatabaseException;
import net.codingarea.commons.database.mongodb.MongoDBDatabase;
import net.codingarea.commons.database.mongodb.update.MongoDBUpdate;
import net.codingarea.commons.database.mongodb.where.MongoDBWhere;
import org.bson.BsonDocument;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map.Entry;

public class MongoDBInsertionOrUpdate extends MongoDBUpdate implements DatabaseInsertionOrUpdate {

  public MongoDBInsertionOrUpdate(@NotNull MongoDBDatabase database, @NotNull String collection) {
    super(database, collection);
  }

  @NotNull
  @Override
  public DatabaseInsertionOrUpdate where(@NotNull String field, @Nullable String value, boolean ignoreCase) {
    super.where(field, value, ignoreCase);
    return this;
  }

  @NotNull
  @Override
  public DatabaseInsertionOrUpdate where(@NotNull String field, @Nullable Object value) {
    super.where(field, value);
    return this;
  }

  @NotNull
  @Override
  public DatabaseInsertionOrUpdate where(@NotNull String field, @Nullable String value) {
    super.where(field, value);
    return this;
  }

  @NotNull
  @Override
  public DatabaseInsertionOrUpdate where(@NotNull String field, @Nullable Number value) {
    super.where(field, value);
    return this;
  }

  @NotNull
  @Override
  public DatabaseInsertionOrUpdate whereNot(@NotNull String field, @Nullable Object value) {
    super.whereNot(field, value);
    return this;
  }

  @NotNull
  @Override
  public DatabaseInsertionOrUpdate set(@NotNull String field, @Nullable Object value) {
    super.set(field, value);
    return this;
  }

  @Override
  public Void execute() throws DatabaseException {
    if (database.query(collection, where).execute().isSet()) {
      return super.execute();
    } else {
      Document document = new Document(values);
      for (Entry<String, MongoDBWhere> entry : where.entrySet()) {
        BsonDocument bson = BsonUtils.convertBsonToBsonDocument(entry.getValue().toBson());
        document.putAll(bson);
      }

      database.insert(collection, document).execute();
      return null;
    }
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

}
