package net.codingarea.commons.database.action;

import net.codingarea.commons.database.Database;
import net.codingarea.commons.database.exceptions.DatabaseException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @see Database#listTables()
 */
public interface DatabaseListTables extends DatabaseAction<List<String>> {

  @NotNull
  @Override
  List<String> execute() throws DatabaseException;

}
