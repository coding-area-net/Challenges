package net.codingarea.commons.common.logging;

import com.google.common.base.Preconditions;
import net.codingarea.commons.common.collection.WrappedException;
import net.codingarea.commons.common.logging.internal.FallbackLogger;
import net.codingarea.commons.common.logging.internal.JavaLoggerWrapper;
import net.codingarea.commons.common.logging.internal.SimpleLogger;
import net.codingarea.commons.common.logging.internal.Slf4jLoggerWrapper;
import net.codingarea.commons.common.logging.internal.factory.ConstantLoggerFactory;
import net.codingarea.commons.common.logging.internal.factory.DefaultLoggerFactory;
import net.codingarea.commons.common.logging.internal.factory.Slf4jLoggerFactory;
import net.codingarea.commons.common.logging.lib.JavaILogger;
import net.codingarea.commons.common.logging.lib.Slf4jILogger;
import net.codingarea.commons.common.misc.ReflectionUtils;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ServiceLoader;

public interface ILogger {

	final class Holder {

		private static ILoggerFactory factory;
		private static Data data;

		private static class Data {
			private boolean slf4j, slf4jApi;
		}

		@Nonnull
		private static Data getData() {
			if (data == null)
				createData();

			return data;
		}

		private static synchronized void createData() {
			boolean slf4j = false;
			boolean slf4jApi = true;

			try {
				Class.forName("org.slf4j.impl.StaticLoggerBinder");
				slf4j = true;
			} catch (ClassNotFoundException eStatic) { // there was no static logger binder (SLF4J pre-1.8.x)
				try {
					Class<?> serviceProviderInterface = Class.forName("org.slf4j.spi.SLF4JServiceProvider");
					// check if there is a service implementation for the service, indicating a provider for SLF4J 1.8.x+ is installed
					slf4j = ServiceLoader.load(serviceProviderInterface).iterator().hasNext();
				} catch (ClassNotFoundException eService) { // there was no service provider interface (SLF4J 1.8.x+)
					try {
						// prints warning of missing implementation
						LoggerFactory.getLogger(ILogger.class);
					} catch (NoClassDefFoundError eApi) {
						slf4jApi = false;
					}
				}
			}

			data          = new Data();
			data.slf4j    = slf4j;
			data.slf4jApi = slf4jApi;
		}

		@Nonnull
		public static ILoggerFactory getFactory() {
			if (factory == null)
				factory = getFallbackFactory();

			return factory;
		}

		@Nonnull
		private static ILoggerFactory getFallbackFactory() {
			return isSlf4jImplAvailable() ? new Slf4jLoggerFactory() :
				   isSlf4jApiAvailable() ? new DefaultLoggerFactory(SimpleLogger::new) :
						                   new DefaultLoggerFactory(FallbackLogger::new);
		}

		private Holder() {}

	}

	static boolean isSlf4jImplAvailable() {
		return Holder.getData().slf4j;
	}

	static boolean isSlf4jApiAvailable() {
		return Holder.getData().slf4jApi;
	}

	@Nonnull
	@CheckReturnValue
	static ILogger forName(@Nullable String name) {
		return getFactory().forName(name);
	}

	@Nonnull
	@CheckReturnValue
	static ILogger forClass(@Nullable Class<?> clazz) {
		return forName(clazz == null ? null : clazz.getSimpleName());
	}

	@Nonnull
	@CheckReturnValue
	static ILogger forClassOf(@Nonnull Object object) {
		return forClass(object.getClass());
	}

	@Nonnull
	@CheckReturnValue
	static ILogger forThisClass() {
		return forClass(ReflectionUtils.getCaller());
	}

	@Nonnull
	@CheckReturnValue
	static JavaILogger forJavaLogger(@Nonnull java.util.logging.Logger logger) {
		return new JavaLoggerWrapper(logger);
	}

	@Nonnull
	@CheckReturnValue
	static ILogger forSlf4jLogger(@Nonnull org.slf4j.Logger logger) {
		return logger instanceof ILogger ? (ILogger) logger : new Slf4jLoggerWrapper(logger);
	}

	static void setFactory(@Nonnull ILoggerFactory factory) {
		Preconditions.checkNotNull(factory);
		Holder.factory = factory;
	}

	static void setConstantFactory(@Nonnull ILogger logger) {
		setFactory(new ConstantLoggerFactory(logger));
	}

	@Nonnull
	static ILoggerFactory getFactory() {
		return Holder.getFactory();
	}

	void log(@Nonnull LogLevel level, @Nullable String message, @Nonnull Object... args);

	default void log(@Nonnull LogLevel level, @Nullable Object message, @Nonnull Object... args) {
		log(level, String.valueOf(message), args);
	}

	default void error(@Nullable String message, @Nonnull Object... args) {
		log(LogLevel.ERROR, message, args);
	}

	default void error(@Nullable Object message, @Nonnull Object... args) {
		log(LogLevel.ERROR, message, args);
	}

	default void warn(@Nullable String message, @Nonnull Object... args) {
		log(LogLevel.WARN, message, args);
	}

	default void warn(@Nullable Object message, @Nonnull Object... args) {
		log(LogLevel.WARN, message, args);
	}

	default void info(@Nullable String message, @Nonnull Object... args) {
		log(LogLevel.INFO, message, args);
	}

	default void info(@Nullable Object message, @Nonnull Object... args)  {
		log(LogLevel.INFO, message, args);
	}

	default void status(@Nullable String message, @Nonnull Object... args) {
		log(LogLevel.STATUS, message, args);
	}

	default void status(@Nullable Object message, @Nonnull Object... args) {
		log(LogLevel.STATUS, message, args);
	}

	default void extended(@Nullable String message, @Nonnull Object... args) {
		log(LogLevel.EXTENDED, message, args);
	}

	default void extended(@Nullable Object message, @Nonnull Object... args) {
		log(LogLevel.EXTENDED, message, args);
	}

	default void debug(@Nullable String message, @Nonnull Object... args) {
		log(LogLevel.DEBUG, message, args);
	}

	default void debug(@Nullable Object message, @Nonnull Object... args) {
		log(LogLevel.DEBUG, message, args);
	}

	default void trace(@Nullable String message, @Nonnull Object... args) {
		log(LogLevel.TRACE, message, args);
	}

	default void trace(@Nullable Object message, @Nonnull Object... args) {
		log(LogLevel.TRACE, message, args);
	}

	default boolean isLevelEnabled(@Nonnull LogLevel level) {
		return level.isShownAtLoggerLevel(getMinLevel());
	}

	default boolean isTraceEnabled() {
		return isLevelEnabled(LogLevel.TRACE);
	}

	default boolean isDebugEnabled() {
		return isLevelEnabled(LogLevel.DEBUG);
	}

	default boolean isExtendedEnabled() {
		return isLevelEnabled(LogLevel.EXTENDED);
	}

	default boolean isInfoEnabled() {
		return isLevelEnabled(LogLevel.INFO);
	}

	default boolean isWarnEnabled() {
		return isLevelEnabled(LogLevel.WARN);
	}

	default boolean isErrorEnabled() {
		return isLevelEnabled(LogLevel.ERROR);
	}

	@Nonnull
	LogLevel getMinLevel();

	@Nonnull
	ILogger setMinLevel(@Nonnull LogLevel level);

	@Nonnull
	@CheckReturnValue
	default Slf4jILogger slf4j() {
		if (this instanceof Slf4jILogger)
			return (Slf4jILogger) this;
		throw new IllegalStateException(this.getClass().getName() + " cannot be converted to Slf4jILogger");
	}

	@Nonnull
	@CheckReturnValue
	default JavaILogger java() {
		if (this instanceof JavaILogger)
			return (JavaILogger) this;
		throw new IllegalStateException(this.getClass().getName() + " cannot be converted to JavaILogger");
	}

	@Nonnull
	@CheckReturnValue
	default PrintStream asPrintStream(@Nonnull LogLevel level) {
		try {
			return new PrintStream(new LogOutputStream(this, level), true, StandardCharsets.UTF_8.name());
		} catch (Exception ex) {
			throw new WrappedException(ex);
		}
	}

	@Nonnull
	@CheckReturnValue
	static String formatMessage(@Nullable Object messageObject, @Nonnull Object... args) {
		StringBuilder message = new StringBuilder(String.valueOf(messageObject));
		for (Object arg : args) {
			if (arg instanceof Throwable) continue;
			int index = message.indexOf("{}");
			if (index == -1) break;
			message.replace(index, index+2, String.valueOf(arg));
		}
		return message.toString();
	}

}
