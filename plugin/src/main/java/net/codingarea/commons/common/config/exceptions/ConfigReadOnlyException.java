package net.codingarea.commons.common.config.exceptions;

import javax.annotation.Nonnull;

public final class ConfigReadOnlyException extends IllegalStateException {

	public ConfigReadOnlyException(@Nonnull String action) {
		super("Config." + action);
	}

}
