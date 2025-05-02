package net.codingarea.commons.common.logging.internal.factory;

import net.codingarea.commons.common.logging.ILogger;
import net.codingarea.commons.common.logging.ILoggerFactory;
import net.codingarea.commons.common.logging.LogLevel;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DefaultLoggerFactory implements ILoggerFactory {

	protected final Map<String, ILogger> loggers = new ConcurrentHashMap<>();
	protected final Function<? super String, ? extends ILogger> creator;
	protected LogLevel level = LogLevel.DEBUG;

	public DefaultLoggerFactory(@Nonnull Function<? super String, ? extends ILogger> creator) {
		this.creator = creator;
	}

	@Nonnull
	@Override
	@CheckReturnValue
	public synchronized ILogger forName(@Nullable String name) {
		return loggers.computeIfAbsent(name == null ? "anonymous" : name, unused -> creator.apply(name).setMinLevel(level));
	}

	@Override
	public void setDefaultLevel(@Nonnull LogLevel level) {
		this.level = level;
	}

}
