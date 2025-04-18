package net.codingarea.challenges.plugin.management.scheduler;

import lombok.Getter;

@Getter
public abstract class AbstractTaskConfig {

	protected final boolean async;

	public AbstractTaskConfig(boolean async) {
		this.async = async;
	}

}
