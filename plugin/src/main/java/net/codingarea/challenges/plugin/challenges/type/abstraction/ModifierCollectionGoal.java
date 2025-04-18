package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.type.IModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ModifierCollectionGoal extends CollectionGoal implements IModifier {

	private final int max, min;
	private final int defaultValue;
	private int value;

	public ModifierCollectionGoal(int min, int max, @Nonnull Object[] target) {
		this(min, max, min, target);
	}

	public ModifierCollectionGoal(int min, int max, int defaultValue, @Nonnull Object[] target) {
		super(target);
		if (max < min) throw new IllegalArgumentException("max < min");
		if (min < 0) throw new IllegalArgumentException("min < 0");
		if (defaultValue > max) throw new IllegalArgumentException("defaultValue > max");
		if (defaultValue < min) throw new IllegalArgumentException("defaultValue < min");
		this.max = max;
		this.min = min;
		this.value = defaultValue;
		this.defaultValue = defaultValue;
	}

	@Override
	public void restoreDefaults() {
		setValue(defaultValue);
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

	@Override
	public void handleClick(@Nonnull ChallengeMenuClickInfo info) {
		ChallengeHelper.handleModifierClick(info, this);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (isEnabled() == enabled) return;
		GoalHelper.handleSetEnabled(this, enabled);
		super.setEnabled(enabled);
	}

	@Override
	@Nonnegative
	public final int getValue() {
		return value;
	}

	@Override
	public void setValue(int value) {
		if (value > max) throw new IllegalArgumentException("value > max");
		if (value < min) throw new IllegalArgumentException("value < min");
		this.value = value;

		if (isEnabled()) onValueChange();

		updateItems();
	}

	@Override
	@Nonnegative
	public final int getMaxValue() {
		return max;
	}

	@Override
	@Nonnegative
	public final int getMinValue() {
		return min;
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChangeChallengeValueTitle(this, this);
	}

	protected void onValueChange() {
	}

	@Override
	public void loadSettings(@Nonnull Document document) {
		setValue(document.getInt("value", value));
	}

	@Override
	public void writeSettings(@Nonnull Document document) {
		document.set("value", value);
	}

}
