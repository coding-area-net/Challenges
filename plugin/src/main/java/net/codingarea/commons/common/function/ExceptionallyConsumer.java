package net.codingarea.commons.common.function;

import net.codingarea.commons.common.collection.WrappedException;

import java.util.function.Consumer;

@FunctionalInterface
public interface ExceptionallyConsumer<T> extends Consumer<T> {

	@Override
	default void accept(T t) {
		try {
			acceptExceptionally(t);
		} catch (Exception ex) {
			throw WrappedException.rethrow(ex);
		}
	}

	void acceptExceptionally(T t) throws Exception;

}
