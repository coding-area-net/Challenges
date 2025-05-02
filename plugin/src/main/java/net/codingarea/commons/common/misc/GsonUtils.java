package net.codingarea.commons.common.misc;

import com.google.gson.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
public final class GsonUtils {

	private GsonUtils() {}

	@Nullable
	public static Object unpackJsonElement(@Nullable JsonElement element) {
		if (element == null || element.isJsonNull())
			return null;
		if (element.isJsonObject())
			return convertJsonObjectToMap(element.getAsJsonObject());
		if (element.isJsonArray())
			return convertJsonArrayToStringList(element.getAsJsonArray());
		if (element.isJsonPrimitive()) {
			JsonPrimitive primitive = element.getAsJsonPrimitive();
			if (primitive.isNumber()) return primitive.getAsNumber();
			if (primitive.isString()) return primitive.getAsString();
			if (primitive.isBoolean()) return primitive.getAsBoolean();
		}
		return element;
	}

	@Nullable
	public static String convertJsonElementToString(@Nullable JsonElement element) {
		if (element == null || element.isJsonNull())
			return null;
		if (element.isJsonPrimitive()) {
			JsonPrimitive primitive = element.getAsJsonPrimitive();
			if (primitive.isString()) return primitive.getAsString();
			if (primitive.isNumber()) return primitive.getAsNumber() + "";
			if (primitive.isBoolean()) return primitive.getAsBoolean() + "";
		}
		return element.toString();
	}

	@Nonnull
	public static Map<String, Object> convertJsonObjectToMap(@Nonnull JsonObject object) {
		Map<String, Object> map = new LinkedHashMap<>();
		convertJsonObjectToMap(object, map);
		return map;
	}

	public static void convertJsonObjectToMap(@Nonnull JsonObject object, @Nonnull Map<String, Object> map) {
		for (Entry<String, JsonElement> entry : object.entrySet()) {
			map.put(entry.getKey(), unpackJsonElement(entry.getValue()));
		}
	}


	@Nonnull
	public static List<String> convertJsonArrayToStringList(@Nonnull JsonArray array) {
		List<String> list = new ArrayList<>(array.size());
		for (JsonElement element : array) {
			list.add(convertJsonElementToString(element));
		}
		return list;
	}

	@Nonnull
	public static String[] convertJsonArrayToStringArray(@Nonnull JsonArray array) {
		String[] list = new String[array.size()];
		for (int i = 0; i < array.size(); i++) {
			list[i] = convertJsonElementToString(array.get(i));
		}
		return list;
	}

	@Nonnull
	public static JsonArray convertIterableToJsonArray(@Nonnull Gson gson, @Nonnull Iterable<?> iterable) {
		JsonArray array = new JsonArray();
		iterable.forEach(object -> array.add(gson.toJsonTree(object)));
		return array;
	}

	@Nonnull
	public static JsonArray convertArrayToJsonArray(@Nonnull Gson gson, @Nonnull Object array) {
		JsonArray jsonArray = new JsonArray();
		ReflectionUtils.forEachInArray(array, object -> jsonArray.add(gson.toJsonTree(object)));
		return jsonArray;
	}

	public static void setDocumentProperties(@Nonnull Gson gson, @Nonnull JsonObject object, @Nonnull Map<String, Object> values) {
		for (Entry<String, Object> entry : values.entrySet()) {
			Object value = entry.getValue();

			if (value == null) {
				object.add(entry.getKey(), null);
			} else if (value instanceof JsonElement) {
				object.add(entry.getKey(), (JsonElement) value);
			} else if (value instanceof Iterable) {
				Iterable<?> iterable = (Iterable<?>) value;
				object.add(entry.getKey(), convertIterableToJsonArray(gson, iterable));
			} else if (value.getClass().isArray()) {
				object.add(entry.getKey(), convertArrayToJsonArray(gson, value));
			} else if (value instanceof Map) {
				Map<String, Object> map = (Map<String, Object>) value;
				JsonObject newObject = new JsonObject();
				object.add(entry.getKey(), newObject);
				setDocumentProperties(gson, newObject, map);
			} else {
				object.add(entry.getKey(), gson.toJsonTree(value));
			}
		}
	}

	public static int getSize(@Nonnull JsonObject object) {
		try {
			return object.size();
		} catch (NoSuchMethodError ex) {
		}

		return object.entrySet().size();
	}

}
