package net.codingarea.challenges.plugin.challenges.custom.settings.action;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;

import java.util.function.Supplier;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public abstract class EntityTargetAction extends ChallengeAction implements IEntityTargetAction {

	public EntityTargetAction(String name,
							  SubSettingsBuilder subSettingsBuilder) {
		super(name, subSettingsBuilder);
	}

	public EntityTargetAction(String name) {
		super(name);
	}

	public EntityTargetAction(String name, Supplier<SubSettingsBuilder> builderSupplier) {
		super(name, builderSupplier);
	}

}
