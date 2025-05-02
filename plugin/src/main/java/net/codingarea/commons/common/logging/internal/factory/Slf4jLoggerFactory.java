package net.codingarea.commons.common.logging.internal.factory;

import net.codingarea.commons.common.logging.ILogger;
import net.codingarea.commons.common.logging.ILoggerFactory;
import net.codingarea.commons.common.logging.LogLevel;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Slf4jLoggerFactory implements ILoggerFactory {

	@Nonnull
	@Override
	public ILogger forName(@Nullable String name) {
		return ILogger.forSlf4jLogger(
			LoggerFactory.getLogger(name == null ? "Logger" : name)
		);
	}

	@Override
	public void setDefaultLevel(@Nonnull LogLevel level) {
	}

}
