package net.codingarea.commons.common.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is used to rethrow signed exception as unsigned exceptions.
 */
public class WrappedException extends RuntimeException {

	public static class SilentWrappedException extends WrappedException {

		public SilentWrappedException(@Nullable String message, @Nonnull Throwable cause) {
			super(message, cause);
		}

		public SilentWrappedException(@Nonnull Throwable cause) {
			super(cause);
		}

		@Override
		public synchronized Throwable fillInStackTrace() {
			return this;
		}

	}

	public WrappedException(@Nullable String message, @Nonnull Throwable cause) {
		super(message, cause);
	}

	public WrappedException(@Nonnull Throwable cause) {
		super(cause);
	}

	@Nonnull
	@Override
	public Throwable getCause() {
		return super.getCause();
	}

	@Nonnull
	public static RuntimeException rethrow(@Nonnull Throwable ex) {
		if (ex instanceof Error)
			throw (Error) ex;
		if (ex instanceof RuntimeException)
			throw (RuntimeException) ex;
		throw silent(ex);
	}

	@Nonnull
	public static WrappedException silent(@Nonnull Throwable cause) {
		return new SilentWrappedException(cause);
	}

	@Nonnull
	public static WrappedException silent(@Nullable String message, @Nonnull Throwable cause) {
		return new SilentWrappedException(message, cause);
	}

}
