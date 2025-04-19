package net.codingarea.commons.common.collection;

import javax.annotation.Nullable;

/**
 * @param <F> The type of the first value
 * @param <S> The type of the second value
 */
@Deprecated
public class Tuple<F, S> extends net.codingarea.commons.common.collection.pair.Tuple<F, S> {

	public Tuple() {
	}

	public Tuple(@Nullable F first, @Nullable S second) {
		super(first, second);
	}

}
