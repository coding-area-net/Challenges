package net.codingarea.commons.common.collection;

import javax.annotation.Nullable;

/**
 * @param <F> The type of the first value
 * @param <S> The type of the second value
 * @param <T> The type of the third value
 */
@Deprecated
public class Triple<F, S, T> extends net.codingarea.commons.common.collection.pair.Triple<F, S, T> {

	public Triple() {
	}

	public Triple(@Nullable F first, @Nullable S second, @Nullable T third) {
		super(first, second, third);
	}

}
