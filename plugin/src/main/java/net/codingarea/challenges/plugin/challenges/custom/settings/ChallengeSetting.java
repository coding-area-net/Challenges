package net.codingarea.challenges.plugin.challenges.custom.settings;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;

import java.util.function.Supplier;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public abstract class ChallengeSetting implements IChallengeSetting {

	private final String name;
	private final SubSettingsBuilder subSettingsBuilder;

	public ChallengeSetting(String name, SubSettingsBuilder subSettingsBuilder) {
		this.name = name;
		this.subSettingsBuilder = subSettingsBuilder.build();
	}

	public ChallengeSetting(String name) {
		this(name, SubSettingsBuilder.createEmpty());
	}

	public ChallengeSetting(String name, Supplier<SubSettingsBuilder> builderSupplier) {
		this(name, builderSupplier.get());
	}

	public String getMessageSuffix() {
		return name.toLowerCase();
	}

	@Override
	public SubSettingsBuilder getSubSettingsBuilder() {
		return subSettingsBuilder;
	}

	@Override
	public String getName() {
		return name;
	}

}
