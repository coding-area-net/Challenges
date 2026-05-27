package net.codingarea.commons.common.logging.handler;

import net.codingarea.commons.common.logging.LogLevel;
import org.jetbrains.annotations.NotNull;

public class HandledSyncLogger extends HandledLogger {

  public HandledSyncLogger(@NotNull LogLevel initialLevel) {
    super(initialLevel);
  }

  @Override
  protected void log0(@NotNull LogEntry entry) {
    logNow(entry);
  }
}
