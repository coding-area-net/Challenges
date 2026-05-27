package net.codingarea.commons.database.action;

import net.codingarea.commons.database.Database;
import net.codingarea.commons.database.SpecificDatabase;
import net.codingarea.commons.database.exceptions.DatabaseException;
import org.jetbrains.annotations.NotNull;

/**
 * @see Database#countEntries(String)
 * @see SpecificDatabase#countEntries()
 */
public interface DatabaseCountEntries extends DatabaseAction<Long> {

  @NotNull
  @Override
  Long execute() throws DatabaseException;

}
