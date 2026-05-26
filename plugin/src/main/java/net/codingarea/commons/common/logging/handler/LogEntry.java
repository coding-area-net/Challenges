package net.codingarea.commons.common.logging.handler;

import net.codingarea.commons.common.logging.LogLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

public class LogEntry {

	private final Instant timestamp;
	private final String threadName;
	private final String message;
	private final LogLevel level;
	private final Throwable exception;

	public LogEntry(@Nonnull Instant timestamp, @Nonnull String threadName, @Nonnull String message, @Nonnull LogLevel level, @Nullable Throwable exception) {
		this.timestamp = timestamp;
		this.threadName = threadName;
		this.message = message;
		this.level = level;
		this.exception = exception;
	}

	@Nonnull
	public Instant getTimestamp() {
		return timestamp;
	}

	@Nonnull
	public String getThreadName() {
		return threadName;
	}

	@Nonnull
	public String getMessage() {
		return message;
	}

	@Nonnull
	public LogLevel getLevel() {
		return level;
	}

	@Nullable
	public Throwable getException() {
		return exception;
	}
}
