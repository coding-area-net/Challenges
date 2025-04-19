package net.codingarea.commons.common.misc;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public final class PropertiesUtils {

	private PropertiesUtils() {}

	public static void setProperties(@Nonnull Properties properties, @Nonnull Map<String, Object> map) {
		for (Entry<Object, Object> entry : properties.entrySet()) {
			map.put((String) entry.getKey(), entry.getValue());
		}
	}

}
