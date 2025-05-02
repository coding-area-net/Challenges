package net.codingarea.commons.database.action;

import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.logging.ILogger;
import net.codingarea.commons.common.logging.LogLevel;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @see DatabaseQuery#execute()
 */
public interface ExecutedQuery extends Iterable<Document> {

	@Nonnull
	@CheckReturnValue
	Optional<Document> first();

	@Nonnull
	@CheckReturnValue
	default Document firstOrEmpty() {
		return first().orElse(Document.empty());
	}

	@Nonnull
	@CheckReturnValue
	Optional<Document> get(int index);

	@Nonnull
	@CheckReturnValue
	default Document getOrEmpty(int index) {
		return get(index).orElse(Document.empty());
	}

	@Nonnull
	@CheckReturnValue
	Stream<Document> all();

	@Nonnull
	@CheckReturnValue
	default List<Document> toList() {
		return toCollection((IntFunction<List<Document>>) ArrayList::new);
	}

	@Nonnull
	@CheckReturnValue
	default Set<Document> toSet() {
		return toCollection((IntFunction<Set<Document>>) HashSet::new);
	}

	@Nonnull
	<C extends Collection<? super Document>> C toCollection(@Nonnull C collection);

	@Nonnull
	@CheckReturnValue
	default <C extends Collection<? super Document>> C toCollection(@Nonnull IntFunction<C> collectionSupplier) {
		return toCollection(collectionSupplier.apply(size()));
	}

	@Nonnull
	Document[] toArray(@Nonnull IntFunction<Document[]> arraySupplier);

	int index(@Nonnull Predicate<? super Document> filter);

	boolean isEmpty();

	boolean isSet();

	int size();

	void print(@Nonnull PrintStream out);

	default void print(@Nonnull ILogger logger) {
		print(logger.asPrintStream(LogLevel.INFO));
	}

	default void print() {
		print(System.out);
	}

}
