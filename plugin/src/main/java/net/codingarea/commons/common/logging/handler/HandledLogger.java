package net.codingarea.commons.common.logging.handler;

import net.codingarea.commons.common.logging.ILogger;
import net.codingarea.commons.common.logging.LogLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class HandledLogger implements ILogger {

	protected final Collection<LogHandler> handlers = new CopyOnWriteArrayList<>();
	protected LogLevel level;

	public HandledLogger(@Nonnull LogLevel initialLevel) {
		this.level = initialLevel;
	}

	@Override
	public void log(@Nonnull LogLevel level, @Nullable String message, @Nonnull Object... args) {
		if (!level.isShownAtLoggerLevel(this.level)) return;
		Throwable exception = null;
		for (Object arg : args) {
			if (arg instanceof Throwable)
				exception = (Throwable) arg;
		}
		log0(new LogEntry(Instant.now(), Thread.currentThread().getName(), ILogger.formatMessage(message, args), level, exception));
	}

	public void log(@Nonnull LogEntry entry) {
		if (!entry.getLevel().isShownAtLoggerLevel(this.level)) return;
		log0(entry);
	}

	protected abstract void log0(@Nonnull LogEntry entry);

	protected void logNow(@Nonnull LogEntry entry) {
		for (LogHandler handler : handlers) {
			try {
				handler.handle(entry);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Nonnull
	public HandledLogger addHandler(@Nonnull LogHandler... handler) {
		handlers.addAll(Arrays.asList(handler));
		return this;
	}

	@Nonnull
	@Override
	public LogLevel getMinLevel() {
		return level;
	}

	@Nonnull
	@Override
	public ILogger setMinLevel(@Nonnull LogLevel level) {
		this.level = level;
		return this;
	}

}
