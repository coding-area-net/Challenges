package net.codingarea.commons.database.sql.abstraction;

import net.codingarea.commons.common.config.Json;
import net.codingarea.commons.common.config.document.GsonDocument;
import net.codingarea.commons.common.misc.GsonUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public final class SQLHelper {

	private SQLHelper() {}

	public static void fillParams(@Nonnull PreparedStatement statement, @Nonnull Object... params) throws SQLException {
		for (int i = 0; i < params.length; i++) {
			Object param = serializeObject(params[i]);
			statement.setObject(i + 1 /* in sql we count from 1 */, param);
		}
	}

	@Nullable
	public static Object serializeObject(@Nullable Object object) {
		if (object == null)                 return null;
		if (object instanceof Number)       return object;
		if (object instanceof Boolean)      return object;
		if (object instanceof Enum<?>)      return ((Enum<?>)object).name();
		if (object instanceof Json)         return ((Json)object).toJson();
		if (object instanceof Map)          return new GsonDocument((Map<String, Object>) object).toJson();
		if (object instanceof Iterable)     return GsonUtils.convertIterableToJsonArray(GsonDocument.GSON, (Iterable<?>) object).toString();
		if (object.getClass().isArray())    return GsonUtils.convertArrayToJsonArray(GsonDocument.GSON, object).toString();
		return object.toString();
	}

}
