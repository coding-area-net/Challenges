package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.management.menu.MenuType;

import javax.annotation.Nonnull;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public abstract class OneEnabledSetting extends Setting {

	private final String typeId;

	public OneEnabledSetting(@Nonnull MenuType menu, @Nonnull String typeId) {
		super(menu);
		this.typeId = typeId;
	}

	public OneEnabledSetting(@Nonnull MenuType menu, boolean enabledByDefault, @Nonnull String typeId) {
		super(menu, enabledByDefault);
		this.typeId = typeId;
	}


	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (isEnabled()) disableOthers();
	}

	protected final void disableOthers() {
		Challenges.getInstance().getChallengeManager().getChallenges().stream()
				.filter(challenge -> challenge != this)
				.filter(challenge -> challenge instanceof OneEnabledSetting)
				.map(challenge -> (OneEnabledSetting) challenge)
				.filter(setting -> setting.typeId.equals(typeId))
				.forEach(setting -> setting.setEnabled(false));
	}

}
