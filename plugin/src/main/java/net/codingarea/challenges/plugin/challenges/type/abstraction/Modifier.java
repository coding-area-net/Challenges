package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public abstract class Modifier extends AbstractChallenge implements IModifier {

	private final int max, min;
	private final int defaultValue;
	private int value;

	public Modifier(@Nonnull MenuType menu) {
		this(menu, 64);
	}

	public Modifier(@Nonnull MenuType menu, int max) {
		this(menu, 1, max);
	}

	public Modifier(@Nonnull MenuType menu, int min, int max) {
		this(menu, min, max, min);
	}

	public Modifier(@Nonnull MenuType menu, int min, int max, int defaultValue) {
		super(menu);
		if (max < min) throw new IllegalArgumentException("max < min");
		if (min < 0) throw new IllegalArgumentException("min < 0");
		if (defaultValue > max) throw new IllegalArgumentException("defaultValue > max");
		if (defaultValue < min) throw new IllegalArgumentException("defaultValue < min");
		this.max = max;
		this.min = min;
		this.value = defaultValue;
		this.defaultValue = defaultValue;
	}

	@Nonnull
	@Override
	public ItemBuilder createSettingsItem() {
		return DefaultItem.value(value);
	}

	@Override
	public void restoreDefaults() {
		setValue(defaultValue);
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

		try {
			if (isEnabled()) onValueChange();
		} catch (Exception exception) {
			Challenges.getInstance().getLogger().error("Error while modifying value of Setting {}", getClass().getSimpleName(), exception);
		}

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
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void handleClick(@Nonnull ChallengeMenuClickInfo info) {
		ChallengeHelper.handleModifierClick(info, this);
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChangeChallengeValueTitle(this);
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
