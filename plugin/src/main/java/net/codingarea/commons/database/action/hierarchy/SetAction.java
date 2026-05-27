package net.codingarea.commons.database.action.hierarchy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SetAction {

  @NotNull
  SetAction set(@NotNull String field, @Nullable Object value);

}
