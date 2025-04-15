package net.codingarea.challenges.plugin.management.scheduler;

import lombok.Getter;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
@Getter
public abstract class AbstractTaskConfig {

	protected final boolean async;

	public AbstractTaskConfig(boolean async) {
		this.async = async;
	}

}
