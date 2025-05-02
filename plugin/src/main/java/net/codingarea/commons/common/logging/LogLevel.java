package net.codingarea.commons.common.logging;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public enum LogLevel {

	TRACE   (0,     "TRACE",    "trace",    Level.FINEST,   false),
	DEBUG   (2,     "DEBUG",    "debug",    Level.FINER,    false),
	EXTENDED(5,     "EXTENDED", "extended", Level.FINE,     false),
	STATUS  (7,     "STATUS",   "status",   Level.CONFIG,   false),
	INFO    (10,    "INFO",     "info",     Level.INFO,     false),
	WARN    (15,    "WARN",     "warn",     Level.WARNING,  true),
	ERROR   (25,    "ERROR",    "error",    Level.SEVERE,   true);

	private final String uppercaseName, lowercaseName;
	private final Level javaLevel;
	@Getter
    private final int value;
	@Getter
    private final boolean highlighted;

	LogLevel(int value, @Nonnull String uppercaseName, @Nonnull String lowercaseName, @Nonnull Level javaLevel, boolean highlighted) {
		this.uppercaseName = uppercaseName;
		this.lowercaseName = lowercaseName;
		this.javaLevel = javaLevel;
		this.value = value;
		this.highlighted = highlighted;
	}

	@Nonnull
	public Level getJavaUtilLevel() {
		return javaLevel;
	}

	public boolean isShownAtLoggerLevel(@Nonnull LogLevel loggerLevel) {
		return this.getValue() >= loggerLevel.getValue();
	}

    @Nonnull
	public String getLowerCaseName() {
		return lowercaseName;
	}

	@Nonnull
	public String getUpperCaseName() {
		return uppercaseName;
	}

    @Nonnull
	public static LogLevel fromJavaLevel(@Nonnull Level level) {
		for (LogLevel logLevel : values()) {
			if (logLevel.getJavaUtilLevel().intValue() == level.intValue())
				return logLevel;
		}
		return INFO;
	}

	@Nonnull
	public static LogLevel fromValue(int value) {
		for (LogLevel level : values()) {
			if (level.getValue() == value)
				return level;
		}
		return INFO;
	}

	@Nonnull
	public static LogLevel fromName(@Nonnull String name) {
		for (LogLevel level : values()) {
			if (level.getUpperCaseName().equalsIgnoreCase(name))
				return level;
		}
		return INFO;
	}
}
