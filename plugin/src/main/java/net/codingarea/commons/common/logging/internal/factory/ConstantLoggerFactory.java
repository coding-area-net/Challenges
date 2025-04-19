package net.codingarea.commons.common.logging.internal.factory;

import net.codingarea.commons.common.logging.ILogger;
import net.codingarea.commons.common.logging.ILoggerFactory;
import net.codingarea.commons.common.logging.LogLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConstantLoggerFactory implements ILoggerFactory {

	protected final ILogger logger;

	public ConstantLoggerFactory(@Nonnull ILogger logger) {
		this.logger = logger;
	}

	@Nonnull
	@Override
	public ILogger forName(@Nullable String name) {
		return logger;
	}

	@Override
	public void setDefaultLevel(@Nonnull LogLevel level) {
		logger.setMinLevel(level);
	}

}
