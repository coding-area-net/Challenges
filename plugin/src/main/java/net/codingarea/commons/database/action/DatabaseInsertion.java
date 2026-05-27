package net.codingarea.commons.database.action;

import net.codingarea.commons.database.Database;
import net.codingarea.commons.database.SpecificDatabase;
import net.codingarea.commons.database.action.hierarchy.SetAction;
import net.codingarea.commons.database.exceptions.DatabaseException;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see Database#insert(String)
 * @see SpecificDatabase#insert()
 */
public interface DatabaseInsertion extends DatabaseAction<Void>, SetAction {

  @NotNull
  @CheckReturnValue
  DatabaseInsertion set(@NotNull String field, @Nullable Object value);

  @Nullable
  @Override
  Void execute() throws DatabaseException;

}
