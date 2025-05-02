package net.codingarea.commons.database.abstraction;

import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.database.action.ExecutedQuery;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DefaultExecutedQuery implements ExecutedQuery {

	protected final List<Document> results;

	public DefaultExecutedQuery(@Nonnull List<Document> results) {
		this.results = results;
	}

	@Nonnull
	@Override
	public Optional<Document> first() {
		if (results.isEmpty()) return Optional.empty();
		return Optional.ofNullable(results.get(0));
	}

	@Nonnull
	@Override
	public Optional<Document> get(int index) {
		if (index >= results.size()) return Optional.empty();
		return Optional.ofNullable(results.get(index));
	}

	@Nonnull
	@Override
	public Stream<Document> all() {
		return results.stream();
	}

	@Nonnull
	@Override
	public <C extends Collection<? super Document>> C toCollection(@Nonnull C collection) {
		collection.addAll(results);
		return collection;
	}

	@Nonnull
	@Override
	public Document[] toArray(@Nonnull IntFunction<Document[]> arraySupplier) {
		Document[] array = arraySupplier.apply(size());
		for (int i = 0; i < size(); i++) {
			array[i] = results.get(i);
		}
		return array;
	}

	@Override
	public int index(@Nonnull Predicate<? super Document> filter) {
		int index = 0;
		for (Document result : results) {
			if (filter.test(result))
				return index;
			index++;
		}
		return -1;
	}

	@Override
	public boolean isEmpty() {
		return results.isEmpty();
	}

	@Override
	public boolean isSet() {
		return !results.isEmpty();
	}

	@Override
	public int size() {
		return results.size();
	}

	@Override
	public void print(@Nonnull PrintStream out) {
		if (results.isEmpty()) {
			out.println("<Empty ExecutedQuery Result>");
			return;
		}

		int index = 0;
		for (Document result : results) {
			out.print(index + " | ");
			result.forEach((key, value) -> {
				out.print(key + " = '" + value + "' ");
			});
			out.println();
			index++;
		}
	}

	@Override
	public Iterator<Document> iterator() {
		return Collections.unmodifiableCollection(results).iterator();
	}

	@Override
	public String toString() {
		return "ExecutedQuery[size=" + size() + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefaultExecutedQuery documents = (DefaultExecutedQuery) o;
		return Objects.equals(results, documents.results);
	}

	@Override
	public int hashCode() {
		return Objects.hash(results);
	}
}
