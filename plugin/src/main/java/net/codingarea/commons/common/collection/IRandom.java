package net.codingarea.commons.common.collection;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public interface IRandom {

	@Nonnull
	@CheckReturnValue
	static IRandom create() {
		return new SeededRandomWrapper();
	}

	@Nonnull
	@CheckReturnValue
	static IRandom create(long seed) {
		return new SeededRandomWrapper(seed);
	}

	@Nonnull
	@CheckReturnValue
	static IRandom wrap(@Nonnull Random random) {
		return new RandomWrapper(random);
	}

	@Nonnull
	@CheckReturnValue
	static IRandom threadLocal() {
		return wrap(ThreadLocalRandom.current());
	}

	@Nonnull
	@CheckReturnValue
	static IRandom secure() {
		return wrap(new SecureRandom());
	}

	@Nonnull
	@CheckReturnValue
	static IRandom singleton() {
		return SingletonRandom.INSTANCE;
	}

	long getSeed();

	void setSeed(long seed);

	void nextBytes(@Nonnull byte[] bytes);

	boolean nextBoolean();

	int nextInt();

	int nextInt(int bound);

	@Nonnull
	@CheckReturnValue
	IntStream ints();

	@Nonnull
	@CheckReturnValue
	IntStream ints(@Nonnegative long streamSize);

	@Nonnull
	@CheckReturnValue
	IntStream ints(int randomNumberOrigin, int randomNumberBound);

	@Nonnull
	@CheckReturnValue
	IntStream ints(@Nonnegative long streamSize, int randomNumberOrigin, int randomNumberBound);

	@Nonnull
	@CheckReturnValue
	LongStream longs();

	long nextLong();

	@Nonnull
	@CheckReturnValue
	LongStream longs(@Nonnegative long streamSize);

	@Nonnull
	@CheckReturnValue
	LongStream longs(long randomNumberOrigin, long randomNumberBound);

	@Nonnull
	@CheckReturnValue
	LongStream longs(@Nonnegative long streamSize, long randomNumberOrigin, long randomNumberBound);

	double nextDouble();

	double nextGaussian();

	@Nonnull
	@CheckReturnValue
	DoubleStream doubles();

	@Nonnull
	@CheckReturnValue
	DoubleStream doubles(@Nonnegative long streamSize);

	@Nonnull
	@CheckReturnValue
	DoubleStream doubles(double randomNumberOrigin, double randomNumberBound);

	@Nonnull
	@CheckReturnValue
	DoubleStream doubles(@Nonnegative long streamSize, double randomNumberOrigin, double randomNumberBound);

	float nextFloat();

	default <T> T choose(@Nonnull T... array) {
		return array[nextInt(array.length)];
	}

	default <T> T choose(@Nonnull List<? extends T> list) {
		return list.get(nextInt(list.size()));
	}

	default <T> T choose(@Nonnull Collection<? extends T> collection) {
		return choose(new ArrayList<>(collection));
	}

	default void shuffle(@Nonnull List<?> list) {
		Collections.shuffle(list, asRandom());
	}

	default int around(int value, @Nonnegative int range) {
		return range(value - range, value + range);
	}

	default int range(int min, int max) {
		if (min >= max) throw new IllegalArgumentException("min >= max");
		return nextInt(max - min) + min;
	}

	@Nonnull
	@CheckReturnValue
	default Random asRandom() {
		if (!(this instanceof Random))
			throw new IllegalStateException(this.getClass().getName() + " cannot be converted a java.util.Random");
		return (Random) this;
	}

}
