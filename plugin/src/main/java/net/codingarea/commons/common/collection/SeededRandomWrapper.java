package net.codingarea.commons.common.collection;

import lombok.Getter;

import java.util.Random;

/**
 * Since there is no way of getting the seed of a {@link Random} we create a wrapper
 * which will save seed. This allows us to save randomization and reload it.
 */
@Getter
public class SeededRandomWrapper extends Random implements IRandom {

	protected long seed;

	public SeededRandomWrapper() {
		super();
	}

	public SeededRandomWrapper(long seed) {
		super(seed);
	}

	@Override
	public void setSeed(long seed) {
		super.setSeed(seed);
		this.seed = seed;
	}

    @Override
	public String toString() {
		return "Random[seed=" + seed + "]";
	}
}
