package net.codingarea.commons.database.action.hierarchy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SetAction {

	@Nonnull
	SetAction set(@Nonnull String field, @Nullable Object value);

}
