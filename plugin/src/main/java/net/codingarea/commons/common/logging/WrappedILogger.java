package net.codingarea.commons.common.logging;

import net.codingarea.commons.common.logging.lib.JavaILogger;
import net.codingarea.commons.common.logging.lib.Slf4jILogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintStream;

public interface WrappedILogger extends ILogger {

	@Nonnull
	ILogger getWrappedLogger();

	@Override
	default void log(@Nonnull LogLevel level, @Nullable String message, @Nonnull Object... args) {
		getWrappedLogger().log(level, message, args);
	}

	@Override
	default void log(@Nonnull LogLevel level, @Nullable Object message, @Nonnull Object... args) {
		getWrappedLogger().log(level, message, args);
	}

	@Override
	default void error(@Nullable String message, @Nonnull Object... args) {
		getWrappedLogger().error(message, args);
	}

	@Override
	default void error(@Nullable Object message, @Nonnull Object... args) {
		getWrappedLogger().error(message, args);
	}

	@Override
	default void warn(@Nullable String message, @Nonnull Object... args) {
		getWrappedLogger().warn(message, args);
	}

	@Override
	default void warn(@Nullable Object message, @Nonnull Object... args) {
		getWrappedLogger().warn(message, args);
	}

	@Override
	default void info(@Nullable String message, @Nonnull Object... args) {
		getWrappedLogger().info(message, args);
	}

	@Override
	default void info(@Nullable Object message, @Nonnull Object... args) {
		getWrappedLogger().info(message, args);
	}

	@Override
	default void status(@Nullable String message, @Nonnull Object... args) {
		getWrappedLogger().status(message, args);
	}

	@Override
	default void status(@Nullable Object message, @Nonnull Object... args) {
		getWrappedLogger().status(message, args);
	}

	@Override
	default void extended(@Nullable String message, @Nonnull Object... args) {
		getWrappedLogger().extended(message, args);
	}

	@Override
	default void extended(@Nullable Object message, @Nonnull Object... args) {
		getWrappedLogger().extended(message, args);
	}

	@Override
	default void debug(@Nullable String message, @Nonnull Object... args) {
		getWrappedLogger().debug(message, args);
	}

	@Override
	default void debug(@Nullable Object message, @Nonnull Object... args) {
		getWrappedLogger().debug(message, args);
	}

	@Override
	default void trace(@Nullable String message, @Nonnull Object... args) {
		getWrappedLogger().trace(message, args);
	}

	@Override
	default void trace(@Nullable Object message, @Nonnull Object... args) {
		getWrappedLogger().trace(message, args);
	}

	@Override
	default boolean isLevelEnabled(@Nonnull LogLevel level) {
		return getWrappedLogger().isLevelEnabled(level);
	}

	@Override
	default boolean isTraceEnabled() {
		return getWrappedLogger().isTraceEnabled();
	}

	@Override
	default boolean isDebugEnabled() {
		return getWrappedLogger().isDebugEnabled();
	}

	@Override
	default boolean isExtendedEnabled() {
		return getWrappedLogger().isExtendedEnabled();
	}

	@Override
	default boolean isInfoEnabled() {
		return getWrappedLogger().isInfoEnabled();
	}

	@Override
	default boolean isWarnEnabled() {
		return getWrappedLogger().isWarnEnabled();
	}

	@Override
	default boolean isErrorEnabled() {
		return getWrappedLogger().isErrorEnabled();
	}

	@Nonnull
	@Override
	default LogLevel getMinLevel() {
		return getWrappedLogger().getMinLevel();
	}

	@Nonnull
	@Override
	default ILogger setMinLevel(@Nonnull LogLevel level) {
		return getWrappedLogger().setMinLevel(level);
	}

	@Nonnull
	@Override
	default Slf4jILogger slf4j() {
		return getWrappedLogger().slf4j();
	}

	@Nonnull
	@Override
	default JavaILogger java() {
		return getWrappedLogger().java();
	}

	@Nonnull
	@Override
	default PrintStream asPrintStream(@Nonnull LogLevel level) {
		return getWrappedLogger().asPrintStream(level);
	}
}
