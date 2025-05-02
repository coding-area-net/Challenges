package net.codingarea.commons.common.logging.handler;

import javax.annotation.Nonnull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public interface LogHandler {

	DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

	void handle(@Nonnull LogEntry entry) throws Exception;

}
