package net.codingarea.commons.database.action.hierarchy;

import net.codingarea.commons.database.Order;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface OrderedAction {

	@Nullable
	OrderedAction orderBy(@Nonnull String field, @Nonnull Order order);

}
