package net.codingarea.commons.database.action.hierarchy;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface WhereAction {

	@Nonnull
	@CheckReturnValue
	WhereAction where(@Nonnull String field, @Nullable Object value);

	@Nonnull
	@CheckReturnValue
	WhereAction where(@Nonnull String field, @Nullable Number value);

	@Nonnull
	@CheckReturnValue
	WhereAction where(@Nonnull String field, @Nullable String value, boolean ignoreCase);

	@Nonnull
	@CheckReturnValue
	WhereAction where(@Nonnull String field, @Nullable String value);

	@Nonnull
	@CheckReturnValue
	WhereAction whereNot(@Nonnull String field, @Nullable Object value);

}
