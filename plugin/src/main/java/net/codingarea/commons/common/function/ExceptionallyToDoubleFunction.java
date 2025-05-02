package net.codingarea.commons.common.function;

import net.codingarea.commons.common.collection.WrappedException;

import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface ExceptionallyToDoubleFunction<T> extends ToDoubleFunction<T> {

	@Override
	default double applyAsDouble(T t) {
		try {
			return applyExceptionally(t);
		} catch (Exception ex) {
			throw WrappedException.rethrow(ex);
		}
	}

	double applyExceptionally(T T) throws Exception;

}
