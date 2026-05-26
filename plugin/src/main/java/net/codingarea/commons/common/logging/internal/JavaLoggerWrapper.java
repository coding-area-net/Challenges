package net.codingarea.commons.common.logging.internal;

import net.codingarea.commons.common.logging.ILogger;
import net.codingarea.commons.common.logging.LogLevel;
import net.codingarea.commons.common.logging.lib.JavaILogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.*;

public class JavaLoggerWrapper extends JavaILogger {

	protected final Logger logger;

	public JavaLoggerWrapper(@Nonnull Logger logger) {
		super(null, null);
		this.logger = logger;
	}

	@Override
	public boolean getUseParentHandlers() {
		return logger.getUseParentHandlers();
	}

	@Override
	public Filter getFilter() {
		return logger.getFilter();
	}

	@Override
	public Handler[] getHandlers() {
		return logger.getHandlers();
	}

	@Override
	public Level getLevel() {
		return logger.getLevel();
	}

	@Override
	public Logger getParent() {
		return logger.getParent();
	}

	@Override
	public ResourceBundle getResourceBundle() {
		return logger.getResourceBundle();
	}

	@Override
	public String getResourceBundleName() {
		return logger.getResourceBundleName();
	}

	@Override
	public void log(LogRecord record) {
		mapLevel(record);
		logger.log(record);
	}

	@Override
	public void log(Level level, String msg, Object[] params) {
		logger.log(mapLevel(level), msg, params);
	}

	@Override
	public void log(Level level, String msg, Object param1) {
		logger.log(mapLevel(level), msg, param1);
	}

	@Override
	public void log(Level level, Supplier<String> msgSupplier) {
		logger.log(mapLevel(level), msgSupplier);
	}

	@Override
	public void log(Level level, String msg) {
		logger.log(mapLevel(level), msg);
	}

	@Override
	public void log(Level level, String msg, Throwable thrown) {
		logger.log(mapLevel(level), msg, thrown);
	}

	@Override
	public void log(Level level, Throwable thrown, Supplier<String> msgSupplier) {
		logger.log(mapLevel(level), thrown, msgSupplier);
	}

	@Override
	public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
		logger.logp(mapLevel(level), sourceClass, sourceMethod, msg);
	}

	@Override
	public void logp(Level level, String sourceClass, String sourceMethod, Supplier<String> msgSupplier) {
		logger.logp(mapLevel(level), sourceClass, sourceMethod, msgSupplier);
	}

	@Override
	public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
		logger.logp(mapLevel(level), sourceClass, sourceMethod, msg, param1);
	}

	@Override
	public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
		logger.logp(mapLevel(level), sourceClass, sourceMethod, msg, params);
	}

	@Override
	public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
		logger.logp(mapLevel(level), sourceClass, sourceMethod, msg, thrown);
	}

	@Override
	public void logp(Level level, String sourceClass, String sourceMethod, Throwable thrown, Supplier<String> msgSupplier) {
		logger.logp(mapLevel(level), sourceClass, sourceMethod, thrown, msgSupplier);
	}

	@Override
	public void logrb(Level level, String sourceClass, String sourceMethod, ResourceBundle bundle, String msg, Object... params) {
		logger.logrb(mapLevel(level), sourceClass, sourceMethod, bundle, msg, params);
	}

	@Override
	public void logrb(Level level, String sourceClass, String sourceMethod, ResourceBundle bundle, String msg, Throwable thrown) {
		logger.logrb(mapLevel(level), sourceClass, sourceMethod, bundle, msg, thrown);
	}

	@Override
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
		logger.logrb(mapLevel(level), sourceClass, sourceMethod, bundleName, msg);
	}

	@Override
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {
		logger.logrb(mapLevel(level), sourceClass, sourceMethod, bundleName, msg, param1);
	}

	@Override
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {
		logger.logrb(mapLevel(level), sourceClass, sourceMethod, bundleName, msg, params);
	}

	@Override
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {
		logger.logrb(mapLevel(level), sourceClass, sourceMethod, bundleName, msg, thrown);
	}

	@Override
	public boolean isLoggable(Level level) {
		return logger.isLoggable(level);
	}

	@Override
	public void setLevel(Level newLevel) throws SecurityException {
		logger.setLevel(newLevel);
	}

	@Override
	public void setFilter(Filter newFilter) throws SecurityException {
		logger.setFilter(newFilter);
	}

	@Override
	public void setParent(Logger parent) {
		logger.setParent(parent);
	}

	@Override
	public void setResourceBundle(ResourceBundle bundle) {
		logger.setResourceBundle(bundle);
	}

	@Override
	public void setUseParentHandlers(boolean useParentHandlers) {
		logger.setUseParentHandlers(useParentHandlers);
	}

	@Override
	public void severe(String msg) {
		logger.severe(msg);
	}

	@Override
	public void severe(Supplier<String> msgSupplier) {
		logger.severe(msgSupplier);
	}

	@Override
	public void entering(String sourceClass, String sourceMethod) {
		logger.entering(sourceClass, sourceMethod);
	}

	@Override
	public void entering(String sourceClass, String sourceMethod, Object param1) {
		logger.entering(sourceClass, sourceMethod, param1);
	}

	@Override
	public void entering(String sourceClass, String sourceMethod, Object[] params) {
		logger.entering(sourceClass, sourceMethod, params);
	}

	@Override
	public void exiting(String sourceClass, String sourceMethod) {
		logger.exiting(sourceClass, sourceMethod);
	}

	@Override
	public void exiting(String sourceClass, String sourceMethod, Object result) {
		logger.exiting(sourceClass, sourceMethod, result);
	}

	@Override
	public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
		logger.throwing(sourceClass, sourceMethod, thrown);
	}

	@Override
	public void warning(String msg) {
		logger.warning(msg);
	}

	@Override
	public void info(String msg) {
		logger.info(msg);
	}

	@Override
	public void config(String msg) {
		logger.config(msg);
	}

	@Override
	public void fine(String msg) {
		logger.fine(msg);
	}

	@Override
	public void finer(String msg) {
		logger.finer(msg);
	}

	@Override
	public void finest(String msg) {
		logger.finest(msg);
	}

	@Override
	public void warning(Supplier<String> msgSupplier) {
		logger.warning(msgSupplier);
	}

	@Override
	public void info(Supplier<String> msgSupplier) {
		logger.info(msgSupplier);
	}

	@Override
	public void config(Supplier<String> msgSupplier) {
		logger.config(msgSupplier);
	}

	@Override
	public void fine(Supplier<String> msgSupplier) {
		logger.fine(msgSupplier);
	}

	@Override
	public void finer(Supplier<String> msgSupplier) {
		logger.finer(msgSupplier);
	}

	@Override
	public void finest(Supplier<String> msgSupplier) {
		logger.finest(msgSupplier);
	}

	@Override
	public void addHandler(Handler handler) throws SecurityException {
		logger.addHandler(handler);
	}

	@Override
	public void removeHandler(Handler handler) throws SecurityException {
		logger.removeHandler(handler);
	}

	@Override
	public int hashCode() {
		return logger.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return logger.equals(obj);
	}

	@Override
	public String toString() {
		return logger.toString();
	}

	protected void mapLevel(@Nonnull LogRecord record) {
		record.setLevel(mapLevel(record.getLevel()));
	}

	@Nonnull
	protected Level mapLevel(@Nonnull Level level) {
		return level;
	}

	@Nonnull
	@Override
	public JavaILogger setMinLevel(@Nonnull LogLevel level) {
		setLevel(level.getJavaUtilLevel());
		return this;
	}

	@Nonnull
	@Override
	public LogLevel getMinLevel() {
		return LogLevel.fromJavaLevel(logger.getLevel());
	}

	@Override
	public void log(@Nonnull LogLevel level, @Nullable String message, @Nonnull Object... args) {
		Throwable thrown = null;
		for (Object arg : args) {
			if (arg instanceof Throwable)
				thrown = (Throwable) arg;
		}
		log(level.getJavaUtilLevel(), ILogger.formatMessage(message, args), thrown);
	}

	@Override
	public void log(@Nonnull LogLevel level, @Nullable Object message, @Nonnull Object... args) {
		log(level, String.valueOf(message), args);
	}

	@Override
	public void error(@Nullable String message, @Nonnull Object... args) {
		log(LogLevel.ERROR, message, args);
	}

	@Override
	public void error(@Nullable Object message, @Nonnull Object... args) {
		log(LogLevel.ERROR, message, args);
	}

	@Override
	public void warn(@Nullable String message, @Nonnull Object... args) {
		log(LogLevel.WARN, message, args);
	}

	@Override
	public void warn(@Nullable Object message, @Nonnull Object... args) {
		log(LogLevel.WARN, message, args);
	}

	@Override
	public void info(@Nullable String message, @Nonnull Object... args) {
		log(LogLevel.INFO, message, args);
	}

	@Override
	public void info(@Nullable Object message, @Nonnull Object... args) {
		log(LogLevel.INFO, message, args);
	}

	@Override
	public void status(@Nullable String message, @Nonnull Object... args) {
		log(LogLevel.STATUS, message, args);
	}

	@Override
	public void status(@Nullable Object message, @Nonnull Object... args) {
		log(LogLevel.STATUS, message, args);
	}

	@Override
	public void debug(@Nullable String message, @Nonnull Object... args) {
		log(LogLevel.DEBUG, message, args);
	}

	@Override
	public void debug(@Nullable Object message, @Nonnull Object... args) {
		log(LogLevel.DEBUG, message, args);
	}


}
