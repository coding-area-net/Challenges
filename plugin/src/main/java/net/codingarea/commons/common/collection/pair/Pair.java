package net.codingarea.commons.common.collection.pair;

import org.jetbrains.annotations.NotNull;

/**
 * @see Tuple
 * @see Triple
 * @see Quadro
 */
public interface Pair {

  /**
   * @return The amount of values
   */
  int amount();

  @NotNull
  Object[] values();

  /**
   * @return {@code true} when all of the values are null, {@code false} otherwise
   */
  boolean allNull();

  /**
   * @return {@code true} when none of the values are null, {@code false} otherwise
   */
  boolean noneNull();

}
