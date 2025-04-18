package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class MenuGoal extends MenuSetting implements IGoal {
	public MenuGoal(@NotNull MenuType menu, @NotNull Message title) {
		super(menu, title);
	}

	@Override
	public final void setEnabled(boolean enabled) {
		if (isEnabled() == enabled) return;
		GoalHelper.handleSetEnabled(this, enabled);
		super.setEnabled(enabled);
	}

	@Nonnull
	@Override
	public SoundSample getStartSound() {
		return SoundSample.DRAGON_BREATH;
	}

	@Nullable
	@Override
	public SoundSample getWinSound() {
		return SoundSample.WIN;
	}
}
