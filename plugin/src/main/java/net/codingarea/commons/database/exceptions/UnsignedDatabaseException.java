package net.codingarea.commons.database.exceptions;

import net.codingarea.commons.common.collection.WrappedException;
import net.codingarea.commons.database.action.DatabaseAction;
import org.jetbrains.annotations.NotNull;

/**
 * @see DatabaseException
 * @see DatabaseAction#executeUnsigned()
 */
public class UnsignedDatabaseException extends WrappedException {

  public UnsignedDatabaseException(@NotNull DatabaseException cause) {
    super(cause);
  }

  @NotNull
  @Override
  public DatabaseException getCause() {
    return (DatabaseException) super.getCause();
  }
}
