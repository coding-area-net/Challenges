package net.codingarea.challenges.plugin.management.scheduler;

public abstract class AbstractTaskConfig {

	protected final boolean async;

	public AbstractTaskConfig(boolean async) {
		this.async = async;
	}

	public boolean isAsync() {
		return async;
	}

}
