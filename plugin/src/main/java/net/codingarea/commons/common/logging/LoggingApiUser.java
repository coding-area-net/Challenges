package net.codingarea.commons.common.logging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface LoggingApiUser {

	@Nonnull
  ILogger getTargetLogger();

	default void error(@Nullable Object message, @Nonnull Object... args) {
		getTargetLogger().error(message, args);
	}

	default void warn(@Nullable Object message, @Nonnull Object... args) {
		getTargetLogger().warn(message, args);
	}

	default void info(@Nullable Object message, @Nonnull Object... args)  {
		getTargetLogger().info(message, args);
	}

	default void status(@Nullable Object message, @Nonnull Object... args) {
		getTargetLogger().status(message, args);
	}

	default void extended(@Nullable Object message, @Nonnull Object... args) {
		getTargetLogger().extended(message, args);
	}

	default void debug(@Nullable Object message, @Nonnull Object... args) {
		getTargetLogger().debug(message, args);
	}

	default void trace(@Nullable Object message, @Nonnull Object... args) {
		getTargetLogger().trace(message, args);
	}

	default boolean isTraceEnabled() {
		return getTargetLogger().isTraceEnabled();
	}

	default boolean isDebugEnabled() {
		return getTargetLogger().isDebugEnabled();
	}

}
