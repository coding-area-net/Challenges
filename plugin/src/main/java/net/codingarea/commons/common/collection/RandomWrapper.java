package net.codingarea.commons.common.collection;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class RandomWrapper implements IRandom {

	private final Random random;

	public RandomWrapper(@Nonnull Random random) {
		this.random = random;
	}

	@Override
	public long getSeed() {
		throw new UnsupportedOperationException("Random.getSeed()");
	}

	@Override
	public void setSeed(long seed) {
		random.setSeed(seed);
	}

	@Override
	public void nextBytes(@Nonnull byte[] bytes) {
		random.nextBytes(bytes);
	}

	@Override
	public boolean nextBoolean() {
		return random.nextBoolean();
	}

	@Override
	public int nextInt() {
		return random.nextInt();
	}

	@Override
	public int nextInt(int bound) {
		return random.nextInt(bound);
	}

	@Nonnull
	@Override
	public IntStream ints() {
		return random.ints();
	}

	@Nonnull
	@Override
	public IntStream ints(long streamSize) {
		return random.ints(streamSize);
	}

	@Nonnull
	@Override
	public IntStream ints(int randomNumberOrigin, int randomNumberBound) {
		return random.ints(randomNumberOrigin, randomNumberBound);
	}

	@Nonnull
	@Override
	public IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) {
		return random.ints(streamSize, randomNumberOrigin, randomNumberBound);
	}

	@Nonnull
	@Override
	public LongStream longs() {
		return random.longs();
	}

	@Override
	public long nextLong() {
		return random.nextLong();
	}

	@Nonnull
	@Override
	public LongStream longs(long streamSize) {
		return random.longs(streamSize);
	}

	@Nonnull
	@Override
	public LongStream longs(long randomNumberOrigin, long randomNumberBound) {
		return random.longs(randomNumberOrigin, randomNumberBound);
	}

	@Nonnull
	@Override
	public LongStream longs(long streamSize, long randomNumberOrigin, long randomNumberBound) {
		return random.longs(streamSize, randomNumberOrigin, randomNumberBound);
	}

	@Override
	public double nextDouble() {
		return random.nextDouble();
	}

	@Override
	public double nextGaussian() {
		return random.nextGaussian();
	}

	@Nonnull
	@Override
	public DoubleStream doubles() {
		return random.doubles();
	}

	@Nonnull
	@Override
	public DoubleStream doubles(long streamSize) {
		return random.doubles(streamSize);
	}

	@Nonnull
	@Override
	public DoubleStream doubles(double randomNumberOrigin, double randomNumberBound) {
		return random.doubles(randomNumberOrigin, randomNumberBound);
	}

	@Nonnull
	@Override
	public DoubleStream doubles(long streamSize, double randomNumberOrigin, double randomNumberBound) {
		return random.doubles(streamSize, randomNumberOrigin, randomNumberBound);
	}

	@Override
	public float nextFloat() {
		return random.nextFloat();
	}

	@Nonnull
	@Override
	public Random asRandom() {
		return random;
	}

	@Override
	public String toString() {
		return "Random[wrapped=" + random.getClass().getSimpleName() + "]";
	}
}
