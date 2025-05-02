package net.codingarea.commons.bukkit.utils.logging;

import net.codingarea.commons.bukkit.core.BukkitModule;
import net.codingarea.commons.common.logging.ILogger;
import net.codingarea.commons.common.misc.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Logger {

	private Logger() {}

	@Nonnull
	public static ILogger getInstance() {
		return BukkitModule.getProvidingModule(ReflectionUtils.getCaller()).getILogger();
	}

	public static void error(@Nullable Object message, @Nonnull Object... args) {
		getInstance().error(message, args);
	}

	public static void warn(@Nullable Object message, @Nonnull Object... args) {
		getInstance().warn(message, args);
	}

	public static void info(@Nullable Object message, @Nonnull Object... args) {
		getInstance().info(message, args);
	}

	public static void debug(@Nullable Object message, @Nonnull Object... args) {
		getInstance().debug(message, args);
	}

	public static void trace(@Nullable Object message, @Nonnull Object... args) {
		getInstance().trace(message, args);
	}

}
