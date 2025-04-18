package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class SettingModifier extends Modifier {

	private boolean enabled;

	public SettingModifier(@Nonnull MenuType menu) {
		super(menu);
	}

	public SettingModifier(@Nonnull MenuType menu, int max) {
		super(menu, max);
	}

	public SettingModifier(@Nonnull MenuType menu, int min, int max) {
		super(menu, min, max);
	}

	public SettingModifier(@Nonnull MenuType menu, int min, int max, int defaultValue) {
		super(menu, min, max, defaultValue);
	}

	@Override
	public void handleClick(@Nonnull ChallengeMenuClickInfo info) {
		if (info.isUpperItemClick() || !enabled) {
			setEnabled(!enabled);
			SoundSample.playStatusSound(info.getPlayer(), enabled);
			playStatusUpdateTitle();
		} else {
			super.handleClick(info);
		}
	}

	@Override
	public void restoreDefaults() {
		super.restoreDefaults();
		setEnabled(false);
	}

	@Nonnull
	@Override
	public ItemStack getSettingsItem() {
		return isEnabled() ? super.getSettingsItem() : DefaultItem.disabled().build();
	}

	@Nonnull
	@Override
	public ItemBuilder createSettingsItem() {
		return DefaultItem.enabled().amount(getValue());
	}

	@Override
	public final boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled) return;
		this.enabled = enabled;

		if (enabled) onEnable();
		else onDisable();

		updateItems();
	}

	public void playStatusUpdateTitle() {
		ChallengeHelper.playToggleChallengeTitle(this);
	}

	protected void onEnable() {
	}

	protected void onDisable() {
	}

	@Override
	public void handleShutdown() {
		super.handleShutdown();
		onDisable();
	}

	@Override
	public void writeSettings(@Nonnull Document document) {
		super.writeSettings(document);
		document.set("enabled", enabled);
	}

	@Override
	public void loadSettings(@Nonnull Document document) {
		super.loadSettings(document);
		setEnabled(document.getBoolean("enabled"));
	}

}
