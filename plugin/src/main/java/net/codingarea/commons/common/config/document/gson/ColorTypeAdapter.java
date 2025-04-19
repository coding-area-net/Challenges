package net.codingarea.commons.common.config.document.gson;

import com.google.gson.Gson;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.codingarea.commons.common.collection.Colors;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;

public class ColorTypeAdapter implements GsonTypeAdapter<Color> {

	@Override
	public void write(@Nonnull Gson gson, @Nonnull JsonWriter writer, @Nonnull Color color) throws IOException {
		TypeAdapters.STRING.write(writer, Colors.asHex(color));
	}

	@Override
	public Color read(@Nonnull Gson gson, @Nonnull JsonReader reader) throws IOException {
		String value = TypeAdapters.STRING.read(reader);
		if (value == null) return null;
		return Color.decode(value);
	}

}
