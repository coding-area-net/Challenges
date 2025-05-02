package net.codingarea.commons.database.mongodb;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.codingarea.commons.database.DatabaseConfig;
import net.codingarea.commons.database.SQLColumn;
import net.codingarea.commons.database.action.*;
import net.codingarea.commons.database.exceptions.DatabaseException;
import net.codingarea.commons.database.abstraction.AbstractDatabase;
import net.codingarea.commons.database.mongodb.count.MongoDBCountEntries;
import net.codingarea.commons.database.mongodb.deletion.MongoDBDeletion;
import net.codingarea.commons.database.mongodb.insertion.MongoDBInsertion;
import net.codingarea.commons.database.mongodb.insertorupdate.MongoDBInsertionOrUpdate;
import net.codingarea.commons.database.mongodb.list.MongoDBListTables;
import net.codingarea.commons.database.mongodb.query.MongoDBQuery;
import net.codingarea.commons.database.mongodb.update.MongoDBUpdate;
import net.codingarea.commons.database.mongodb.where.MongoDBWhere;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDBDatabase extends AbstractDatabase {

	static {
		Logger.getLogger("org.mongodb").setLevel(Level.SEVERE);
	}

	protected MongoClient client;
	protected MongoDatabase database;

	public MongoDBDatabase(@Nonnull DatabaseConfig config) {
		super(config);
	}

	@Override
	public void connect0() throws Exception {
		MongoCredential credential = MongoCredential.createCredential(config.getUser(), config.getAuthDatabase(), config.getPassword().toCharArray());
		MongoClientSettings settings = MongoClientSettings.builder()
				.retryReads(false).retryReads(false)
				.credential(credential)
				.applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(config.getHost(), config.isPortSet() ? config.getPort() : ServerAddress.defaultPort()))))
				.build();
		client = MongoClients.create(settings);
		database = client.getDatabase(config.getDatabase());
	}

	@Override
	public void disconnect0() throws Exception {
		client.close();
		client = null;
	}

	@Override
	public void createTable(@Nonnull String name, @Nonnull SQLColumn... columns) throws DatabaseException {
		checkConnection();

		boolean collectionExists = listTables().execute().contains(name);
		if (collectionExists) return;

		try {
			database.createCollection(name);
		} catch (Exception ex) {
			throw new DatabaseException(ex);
		}
	}

	@Nonnull
	@Override
	public DatabaseListTables listTables() {
		return new MongoDBListTables(this);
	}

	@Nonnull
	@Override
	public DatabaseCountEntries countEntries(@Nonnull String table) {
		return new MongoDBCountEntries(this, table);
	}

	@Nonnull
	@Override
	public DatabaseQuery query(@Nonnull String table) {
		return new MongoDBQuery(this, table);
	}

	@Nonnull
	public DatabaseQuery query(@Nonnull String table, @Nonnull Map<String, MongoDBWhere> where) {
		return new MongoDBQuery(this, table, where);
	}

	@Nonnull
	@Override
	public DatabaseUpdate update(@Nonnull String table) {
		return new MongoDBUpdate(this, table);
	}

	@Nonnull
	@Override
	public DatabaseInsertion insert(@Nonnull String table) {
		return new MongoDBInsertion(this, table);
	}

	@Nonnull
	public DatabaseInsertion insert(@Nonnull String table, @Nonnull Map<String, Object> values) {
		return new MongoDBInsertion(this, table, new Document(values));
	}

	@Nonnull
	public DatabaseInsertion insert(@Nonnull String table, @Nonnull Document document) {
		return new MongoDBInsertion(this, table, document);
	}

	@Nonnull
	@Override
	public DatabaseInsertionOrUpdate insertOrUpdate(@Nonnull String table) {
		return new MongoDBInsertionOrUpdate(this, table);
	}

	@Nonnull
	@Override
	public DatabaseDeletion delete(@Nonnull String table) {
		return new MongoDBDeletion(this, table);
	}

	@Nonnull
	public MongoCollection<Document> getCollection(@Nonnull String collection) {
		return database.getCollection(collection);
	}

	@Nonnull
	public MongoDatabase getDatabase() {
		return database;
	}

	@Override
	public boolean isConnected() {
		return client != null && database != null;
	}

}
