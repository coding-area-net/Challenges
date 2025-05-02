package net.codingarea.commons.common.config.document;

import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.config.document.readonly.ReadOnlyDocumentWrapper;
import net.codingarea.commons.common.config.exceptions.ConfigReadOnlyException;
import net.codingarea.commons.common.misc.BukkitReflectionSerializationUtils;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractDocument extends AbstractConfig implements Document {

	protected final Document root, parent;

	public AbstractDocument(@Nonnull Document root, @Nullable Document parent) {
		this.root = root;
		this.parent = parent;
	}

	public AbstractDocument() {
		this.root = this;
		this.parent = null;
	}

	@Nonnull
	@Override
	public <T> T getSerializable(@Nonnull String path, @Nonnull T def) {
		T value = getSerializable(path, (Class<T>) def.getClass());
		return value == null ? def : value;
	}

	@Nullable
	@Override
	public <T> T getSerializable(@Nonnull String path, @Nonnull Class<T> classOfT) {
		if (!contains(path)) return null;
		return BukkitReflectionSerializationUtils.deserializeObject(getDocument(path).values(), classOfT);
	}

	@Nonnull
	@Override
	public <T> List<T> getSerializableList(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return getDocumentList(path).stream()
				.map(Document::values)
				.map(map -> BukkitReflectionSerializationUtils.deserializeObject(map, classOfT))
				.collect(Collectors.toList());
	}

	@Nonnull
	@Override
	public <K, V> Map<K, V> mapDocuments(@Nonnull Function<? super String, ? extends K> keyMapper, @Nonnull Function<? super Document, ? extends V> valueMapper) {
		return map(children(), keyMapper, valueMapper);
	}

	@Nonnull
	@Override
	public <R> R mapDocument(@Nonnull String path, @Nonnull Function<? super Document, ? extends R> mapper) {
		Document document = getDocument(path);
		return mapper.apply(document);
	}

	@Nullable
	@Override
	public <R> R mapDocumentNullable(@Nonnull String path, @Nonnull Function<? super Document, ? extends R> mapper) {
		if (!contains(path)) return null;
		return mapDocument(path, mapper);
	}

	@Nonnull
	@Override
	public Document getDocument(@Nonnull String path) {
		Document document = getDocument0(path, root, this);
		return isReadonly() && !document.isReadonly() ? new ReadOnlyDocumentWrapper(document) : document;
	}

	@Nonnull
	@Override
	public Document set(@Nonnull String path, @Nullable Object value) {
		if (isReadonly()) throw new ConfigReadOnlyException("set");

		if (value instanceof byte[])
			value = Base64.getEncoder().encodeToString((byte[]) value);

		set0(path, value);
		return this;
	}

	@Nonnull
	@Override
	public Document set(@Nonnull Object object) {
		if (isReadonly()) throw new ConfigReadOnlyException("set");

		Document.of(object).forEach(this::set);
		return this;
	}

	@Nonnull
	@Override
	public Document remove(@Nonnull String path) {
		if (isReadonly()) throw new ConfigReadOnlyException("remove");
		remove0(path);
		return this;
	}

	@Nonnull
	@Override
	public Document clear() {
		if (isReadonly()) throw new ConfigReadOnlyException("clear");
		clear0();
		return this;
	}

	@Nonnull
	@CheckReturnValue
	public Document readonly() {
		return isReadonly() ? this : new ReadOnlyDocumentWrapper(this);
	}

	@Nonnull
	protected abstract Document getDocument0(@Nonnull String path, @Nonnull Document root, @Nullable Document parent);

	protected abstract void set0(@Nonnull String path, @Nullable Object value);

	protected abstract void remove0(@Nonnull String path);

	protected abstract void clear0();

	@Nonnull
	@Override
	public Map<String, Document> children() {
		Map<String, Document> map = new HashMap<>();
		keys().forEach(key -> {
			if (!isDocument(key)) return;
			map.put(key, getDocument(key));
		});
		return map;
	}

	@Override
	public boolean hasChildren(@Nonnull String path) {
		return !getDocument(path).isEmpty();
	}

	@Nonnull
	@Override
	public Document getRoot() {
		return root;
	}

	@Nullable
	@Override
	public Document getParent() {
		return parent;
	}

}
