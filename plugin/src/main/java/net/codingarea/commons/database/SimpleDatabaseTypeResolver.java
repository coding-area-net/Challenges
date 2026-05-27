package net.codingarea.commons.database;

import net.codingarea.commons.common.misc.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class SimpleDatabaseTypeResolver {

  private SimpleDatabaseTypeResolver() {
  }

  private static final Map<String, String> registry = new HashMap<>();

  static {
    registerType("mongodb", "net.codingarea.commons.database.mongodb.MongoDBDatabase");
    registerType("mysql", "net.codingarea.commons.database.sql.mysql.MySQLDatabase");
    registerType("sqlite", "net.codingarea.commons.database.sql.sqlite.SQLiteDatabase");
  }

  @Nullable
  public static Class<? extends Database> findDatabaseType(@NotNull String name) {
    return ReflectionUtils.getClassOrNull(registry.get(name));
  }

  @Nullable
  public static Class<? extends Database> findDatabaseType(@NotNull String name, boolean initialize, @NotNull ClassLoader classLoader) {
    return ReflectionUtils.getClassOrNull(registry.get(name), initialize, classLoader);
  }

  public static void registerType(@NotNull String name, @NotNull String className) {
    registry.put(name, className);
  }

  public static void registerType(@NotNull String name, @NotNull Class<? extends Database> databaseClass) {
    registerType(name, databaseClass.getName());
  }

}
