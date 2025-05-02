package net.codingarea.commons.common.misc;

import com.mongodb.MongoClient;
import net.codingarea.commons.common.logging.ILogger;
import org.bson.*;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class BsonUtils {

	protected static final ILogger logger = ILogger.forThisClass();

	private BsonUtils() {
	}

	@Nonnull
	public static BsonDocument convertBsonToBsonDocument(@Nonnull Bson bson) {
		return bson.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());
	}

	@Nullable
	public static Object unpackBsonElement(@Nullable Object value) {
		if (value == null || value instanceof BsonNull) {
			return null;
		} else if (value instanceof BsonString) {
			return ((BsonString) value).getValue();
		} else if (value instanceof BsonDouble) {
			return ((BsonDouble) value).getValue();
		} else if (value instanceof BsonInt32) {
			return ((BsonInt32) value).getValue();
		} else if (value instanceof BsonInt64) {
			return ((BsonInt64) value).getValue();
		} else if (value instanceof BsonBinary) {
			return ((BsonBinary) value).getData();
		} else if (value instanceof BsonObjectId) {
			return ((BsonObjectId) value).getValue();
		} else if (value instanceof BsonArray) {
			return convertBsonArrayToList((BsonArray) value);
		} else if (value instanceof BsonDocument) {
			return convertBsonDocumentToMap((BsonDocument) value);
		} else if (value instanceof Document) {
			return value;
		} else {
			return value;
		}
	}

	@Nonnull
	public static List<Object> convertBsonArrayToList(@Nonnull BsonArray array) {
		List<Object> list = new ArrayList<>(array.size());
		for (BsonValue value : array) {
			list.add(unpackBsonElement(value));
		}
		return list;
	}

	@Nonnull
	public static Map<String, Object> convertBsonDocumentToMap(@Nonnull BsonDocument document) {
		Map<String, Object> map = new HashMap<>();
		for (Entry<String, BsonValue> entry : document.entrySet()) {
			map.put(entry.getKey(), unpackBsonElement(entry.getValue()));
		}
		return map;
	}

	@Nonnull
	public static BsonValue convertObjectToBsonElement(@Nullable Object value) {
		if (value == null) {
			return BsonNull.VALUE;
		} else if (value instanceof BsonValue) {
			return (BsonValue) value;
		} else if (value instanceof Bson) {
			return convertBsonToBsonDocument((Bson) value);
		} else if (value instanceof String) {
			return new BsonString((String) value);
		} else if (value instanceof Boolean) {
			return new BsonBoolean((Boolean) value);
		} else if (value instanceof ObjectId) {
			return new BsonObjectId((ObjectId) value);
		} else if (value instanceof Double) {
			return new BsonDouble((Double) value);
		} else if (value instanceof Float) {
			return new BsonDouble((Float) value);
		} else if (value instanceof Long) {
			return new BsonInt64((Long) value);
		} else if (value instanceof Integer) {
			return new BsonInt32((Integer) value);
		} else if (value instanceof Number) {
			return new BsonInt32(((Number) value).intValue());
		} else if (value instanceof byte[]) {
			return new BsonBinary((byte[]) value);
		} else if (value instanceof Iterable) {
			return convertIterableToBsonArray((Iterable<?>) value);
		} else if (value.getClass().isArray()) {
			return convertArrayToBsonArray(value);
		} else if (value instanceof Map) {
			BsonDocument document = new BsonDocument();
			setDocumentProperties(document, (Map<String, Object>) value);
			return document;
		} else {
			logger.error("Could not serialize " + value.getClass().getName() + " to BsonValue");
			return BsonNull.VALUE;
		}
	}

	public static void setDocumentProperties(@Nonnull BsonDocument document, @Nonnull Map<String, Object> values) {
		for (Entry<String, Object> entry : values.entrySet()) {
			Object value = entry.getValue();
			document.put(entry.getKey(), convertObjectToBsonElement(value));
		}
	}

	@Nonnull
	public static BsonArray convertIterableToBsonArray(@Nonnull Iterable<?> iterable) {
		BsonArray bsonArray = new BsonArray();
		iterable.forEach(object -> bsonArray.add(convertObjectToBsonElement(object)));
		return bsonArray;
	}

	@Nonnull
	public static BsonArray convertArrayToBsonArray(@Nonnull Object array) {
		BsonArray bsonArray = new BsonArray();
		ReflectionUtils.forEachInArray(array, object -> bsonArray.add(convertObjectToBsonElement(object)));
		return bsonArray;
	}

}
