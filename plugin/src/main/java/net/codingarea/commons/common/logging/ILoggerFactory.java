package net.codingarea.commons.common.logging;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ILoggerFactory {

	@Nonnull
	@CheckReturnValue
  ILogger forName(@Nullable String name);

	void setDefaultLevel(@Nonnull LogLevel level);

}
