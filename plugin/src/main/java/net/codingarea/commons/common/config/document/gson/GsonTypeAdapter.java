package net.codingarea.commons.common.config.document.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public interface GsonTypeAdapter<T> {

	void write(@Nonnull Gson gson, @Nonnull JsonWriter writer, @Nonnull T object) throws IOException;

	T read(@Nonnull Gson gson, @Nonnull JsonReader reader) throws IOException;

	default TypeAdapter<T> toTypeAdapter(@Nonnull Gson gson) {
		return new TypeAdapter<T>() {
			@Override
			public void write(JsonWriter writer, T object) throws IOException {
				if (object == null) {
					writer.nullValue();
					return;
				}
				GsonTypeAdapter.this.write(gson, writer, object);
			}

			@Override
			public T read(JsonReader reader) throws IOException {
				return GsonTypeAdapter.this.read(gson, reader);
			}
		};
	}

	@Nonnull
	static TypeAdapterFactory newTypeHierarchyFactory(@Nonnull Class<?> clazz, @Nonnull GsonTypeAdapter<?> adapter) {
		return new TypeAdapterFactory() {
			@Override
			public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> token) {
				Class<? super R> requestedType = token.getRawType();
				if (!clazz.isAssignableFrom(requestedType)) return null;

				return (TypeAdapter<R>) adapter.toTypeAdapter(gson);
			}
		};
	}

	@Nonnull
	static TypeAdapterFactory newPredictableFactory(@Nonnull Predicate<Class<?>> predicate, @Nonnull GsonTypeAdapter<?> adapter) {
		return new TypeAdapterFactory() {
			@Override
			public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
				return predicate.test(type.getRawType()) ? (TypeAdapter<T>) adapter.toTypeAdapter(gson) : null;
			}
		};
	}

}
