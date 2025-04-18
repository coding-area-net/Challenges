package net.codingarea.challenges.plugin.challenges.type.abstraction;

import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.menu.MenuClickInfo;
import net.anweisen.utilities.bukkit.utils.menu.MenuPosition;
import net.anweisen.utilities.bukkit.utils.menu.positions.EmptyMenuPosition;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.SettingsMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class MenuSetting extends Setting {

	private final Map<String, SubSetting> settings = new LinkedHashMap<>();
	private final List<Inventory> inventories = new ArrayList<>();
	private final Message title;

	public MenuSetting(@Nonnull MenuType menu, @Nonnull Message title) {
		super(menu);
		this.title = title;
	}

	@Nonnull
	public static int[] getSlots(int amount) {
		switch (amount) {
			default:
				return new int[0];
			case 1:
				return new int[]{13};
			case 2:
				return new int[]{12, 14};
			case 3:
				return new int[]{11, 13, 15};
			case 4:
				return new int[]{10, 12, 14, 16};
		}
	}

	protected final void generateInventories() {

		inventories.clear();

		int maxRowLength = 4;
		int page = 0;
		int index = 0;

		Collection<SubSetting> settings = this.settings.values();

		int pagesTotal = settings.size() / maxRowLength;
		if (settings.size() % maxRowLength != 0) pagesTotal++;

		for (SubSetting setting : settings) {

			if (index >= maxRowLength) {
				index = 0;
				createNewInventory(++page, pagesTotal);
			} else if (inventories.isEmpty()) {
				createNewInventory(page, pagesTotal);
			}

			int added = page * maxRowLength + index;
			int left = settings.size() - added;
			int fitOnThisPage = index + left;
			if (fitOnThisPage > 4) fitOnThisPage = 4;

			int[] slots = getSlots(fitOnThisPage);

			int displaySlot = slots[index];

			setting.setPage(page);
			setting.setSlot(displaySlot);
			setting.updateItems();

			index++;

		}

		InventoryUtils.setNavigationItemsToInventory(inventories, SettingsMenuGenerator.NAVIGATION_SLOTS, true);

	}

	@Nonnull
	private Inventory createNewInventory(int page, int pagesAmount) {
		Inventory inventory = Bukkit.createInventory(MenuPosition.HOLDER, SettingsMenuGenerator.SIZE, InventoryTitleManager.getMenuSettingTitle(getType(), title.asString(), page, pagesAmount > 1));
		InventoryUtils.fillInventory(inventory, ItemBuilder.FILL_ITEM);
		inventories.add(inventory);
		return inventory;
	}

	protected final void registerSetting(@Nonnull String name, @Nonnull SubSetting setting) {
		if (name.equals("enabled")) throw new IllegalArgumentException();
		settings.put(name, setting);
		Challenges.getInstance().registerListener(setting);
	}

	public final SubSetting getSetting(@Nonnull String name) {
		return settings.get(name);
	}

	@Override
	public void handleClick(@Nonnull ChallengeMenuClickInfo info) {
		if (info.isUpperItemClick()) {
			super.handleClick(info);
		} else if (isEnabled() && !info.isRightClick()) {
			openMenu(info);
		} else {
			super.handleClick(info);
		}
	}

	private void openMenu(@Nonnull ChallengeMenuClickInfo event) {
		MenuPosition position = MenuPosition.get(event.getPlayer());
		if (position == null) position = new EmptyMenuPosition();
		Inventory inventory = event.getInventory();
		SoundSample.CLICK.play(event.getPlayer());
		open(event.getPlayer(), inventory, position, 0);
	}

	private void open(@Nonnull Player player, @Nonnull Inventory inventory, @Nonnull MenuPosition position, int page) {
		if (inventories.isEmpty()) generateInventories();
		if (inventories.isEmpty()) {
			SoundSample.BASS_OFF.play(player);
			return;
		}
		if (page >= inventories.size()) page = inventories.size() - 1;
		Inventory menu = inventories.get(page);
		MenuPosition.set(player, new SettingMenuPosition(position, inventory, page));
		player.openInventory(menu);
	}

	@Nonnull
	@Override
	public final ItemBuilder createSettingsItem() {
		return isEnabled() ? DefaultItem.customize() : DefaultItem.disabled();
	}

	@Override
	public void writeSettings(@Nonnull Document document) {
		document.set("enabled", isEnabled());
		for (Entry<String, SubSetting> entry : settings.entrySet()) {
			Document subDocument = document.getDocument(entry.getKey());
			entry.getValue().writeSettings(subDocument);
		}
	}

	@Override
	public void loadSettings(@Nonnull Document document) {
		setEnabled(document.getBoolean("enabled"));
		for (Entry<String, SubSetting> entry : settings.entrySet()) {
			if (!document.contains(entry.getKey())) continue;
			Document subDocument = document.getDocument(entry.getKey());
			entry.getValue().loadSettings(subDocument);
		}
	}

	@Override
	public void restoreDefaults() {
		super.restoreDefaults();
		settings.values().forEach(SubSetting::restoreDefaults);
	}

	public abstract class SubSetting implements Listener {

		private int page = -1, slot = -1;

		private void setPage(int page) {
			this.page = page;
		}

		private void setSlot(int slot) {
			this.slot = slot;
		}

		public final void updateItems() {
			if (inventories.isEmpty()) return; // Menu not generated yet, nothing to update here
			if (page == -1 || slot == -1) return; // Invalid slots, menu not generated yet

			Inventory inventory = inventories.get(page);

			inventory.setItem(slot, getDisplayItem().hideAttributes().build());
			inventory.setItem(slot + 9, buildSettingsItem());
		}

		@Nonnull
		private ItemStack buildSettingsItem() {
			ItemBuilder item = getSettingsItem();
			String[] description = getSettingsDescription();
			if (description != null && isEnabled()) {
				item.appendLore(" ");
				item.appendLore(description);
			}

			return item.build();
		}

		@Nonnull
		public abstract ItemBuilder getDisplayItem();

		@Nonnull
		public abstract ItemBuilder getSettingsItem();

		@Nullable
		protected abstract String[] getSettingsDescription();

		public boolean isEnabled() {
			return MenuSetting.this.isEnabled() && getAsBoolean();
		}

		public abstract int getAsInt();

		public abstract boolean getAsBoolean();

		public abstract void restoreDefaults();

		public abstract void loadSettings(@Nonnull Document document);

		public abstract void writeSettings(@Nonnull Document document);

		public abstract void handleClick(@Nonnull ChallengeMenuClickInfo info);

	}

	public class BooleanSubSetting extends SubSetting {

		private final Supplier<ItemBuilder> item;
		private final Supplier<String[]> description;
		private final boolean enabledByDefault;
		private boolean enabled;

		public BooleanSubSetting(@Nonnull Supplier<ItemBuilder> item) {
			this(item, () -> null);
		}

		public BooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, boolean enabledByDefault) {
			this(item, () -> null, enabledByDefault);
		}

		public BooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Supplier<String[]> description) {
			this(item, description, false);
		}

		public BooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Supplier<String[]> description, boolean enabledByDefault) {
			this.item = item;
			this.description = description;
			this.enabledByDefault = enabledByDefault;
			this.setEnabled(enabledByDefault);
		}

		@Nonnull
		@Override
		public ItemBuilder getDisplayItem() {
			return item.get();
		}

		@Nonnull
		@Override
		public ItemBuilder getSettingsItem() {
			return DefaultItem.status(enabled);
		}

		@Nullable
		@Override
		protected String[] getSettingsDescription() {
			return description.get();
		}

		@Override
		public int getAsInt() {
			return enabled ? 1 : 0;
		}

		@Override
		public boolean getAsBoolean() {
			return enabled;
		}

		@Override
		public void restoreDefaults() {
			this.setEnabled(enabledByDefault);
		}

		@Nonnull
		public BooleanSubSetting setEnabled(boolean enabled) {
			if (this.enabled == enabled) return this;
			this.enabled = enabled;

			if (enabled) this.onEnable();
			else this.onDisable();

			this.updateItems();
			return this;
		}

		@Override
		public final void handleClick(@Nonnull ChallengeMenuClickInfo info) {
			this.setEnabled(!enabled);
			SoundSample.playStatusSound(info.getPlayer(), enabled);
		}

		@Override
		public void loadSettings(@Nonnull Document document) {
			this.setEnabled(document.getBoolean("enabled"));
		}

		@Override
		public void writeSettings(@Nonnull Document document) {
			document.set("enabled", enabled);
		}

		protected void onEnable() {
		}

		protected void onDisable() {
		}

	}

	public class NumberSubSetting extends SubSetting {

		private final Supplier<ItemBuilder> item;
		private final Function<Integer, String[]> description;
		private final Function<Integer, String> name;
		private final int max, min;
		private final int defaultValue;
		@Getter
    private int value;

		public NumberSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, @Nullable Function<Integer, String> name) {
			this(item, description, name, 64);
		}

		public NumberSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, @Nullable Function<Integer, String> name, int max) {
			this(item, description, name, max, 1);
		}

		public NumberSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, @Nullable Function<Integer, String> name, int min, int max) {
			this(item, description, name, min, max, min);
		}

		public NumberSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, @Nullable Function<Integer, String> name, int min, int max, int defaultValue) {
			if (max <= min) throw new IllegalArgumentException("max <= min");
			if (min < 0) throw new IllegalArgumentException("min < 0");
			if (defaultValue > max) throw new IllegalArgumentException("defaultValue > max");
			if (defaultValue < min) throw new IllegalArgumentException("defaultValue < min");
			this.value = defaultValue;
			this.defaultValue = defaultValue;
			this.max = max;
			this.min = min;
			this.item = item;
			this.description = description;
			this.name = name;
		}

		public NumberSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description) {
			this(item, description, null, 64, 1);
		}

		public NumberSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, int max) {
			this(item, description, null, max, 1);
		}

		public NumberSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, int min, int max) {
			this(item, description, null, min, max, min);
		}

		public NumberSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, int min, int max, int defaultValue) {
			this(item, description, null, min, max, defaultValue);
		}

		public NumberSubSetting(@Nonnull Supplier<ItemBuilder> item) {
			this(item, value -> null);
		}

		public NumberSubSetting(@Nonnull Supplier<ItemBuilder> item, int max) {
			this(item, value -> null, max);
		}

		public NumberSubSetting(@Nonnull Supplier<ItemBuilder> item, int min, int max) {
			this(item, value -> null, min, max);
		}

		public NumberSubSetting(@Nonnull Supplier<ItemBuilder> item, int min, int max, int defaultValue) {
			this(item, value -> null, min, max, defaultValue);
		}

		@Nonnull
		@Override
		public ItemBuilder getDisplayItem() {
			return item.get();
		}

		@Nonnull
		@Override
		public ItemBuilder getSettingsItem() {
			if (name != null)
				return DefaultItem.create(Material.STONE_BUTTON, name.apply(getValue())).amount(getValue());

			return DefaultItem.value(value);
		}

		@Nullable
		@Override
		protected String[] getSettingsDescription() {
			return description.apply(getValue());
		}

		@Override
		public void restoreDefaults() {
			this.setValue(defaultValue);
		}

    public void setValue(int value) {
			if (this.value == value) return;
			this.value = value;

			updateItems();
			onValueChange();
		}

		@Override
		public int getAsInt() {
			return value;
		}

		@Override
		public boolean getAsBoolean() {
			return value > 0;
		}

		@Override
		public void handleClick(@Nonnull ChallengeMenuClickInfo info) {
			int amount = info.isShiftClick() ? 10 : 1;
			int newValue = value;
			if (info.isRightClick()) {
				newValue -= amount;
			} else {
				newValue += amount;
			}

			if (newValue > max)
				newValue = min;
			if (newValue < min)
				newValue = max;

			this.setValue(newValue);
			SoundSample.CLICK.play(info.getPlayer());
		}

		@Override
		public void loadSettings(@Nonnull Document document) {
			this.setValue(document.getInt("value"));
		}

		@Override
		public void writeSettings(@Nonnull Document document) {
			document.set("value", value);
		}

		protected void onValueChange() {
		}

	}

	public class NumberAndBooleanSubSetting extends NumberSubSetting {

		private final boolean enabledByDefault = false; // Implement in future
		private boolean enabled;

		public NumberAndBooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, @Nullable Function<Integer, String> name) {
			super(item, description, name);
		}

		public NumberAndBooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, @Nullable Function<Integer, String> name, int max) {
			super(item, description, name, max);
		}

		public NumberAndBooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, @Nullable Function<Integer, String> name, int min, int max) {
			super(item, description, name, min, max);
		}

		public NumberAndBooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, @Nullable Function<Integer, String> name, int min, int max, int defaultValue) {
			super(item, description, name, min, max, defaultValue);
		}

		public NumberAndBooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description) {
			super(item, description);
		}

		public NumberAndBooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, int max) {
			super(item, description, max);
		}

		public NumberAndBooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, int min, int max) {
			super(item, description, min, max);
		}

		public NumberAndBooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, @Nonnull Function<Integer, String[]> description, int min, int max, int defaultValue) {
			super(item, description, min, max, defaultValue);
		}

		public NumberAndBooleanSubSetting(@Nonnull Supplier<ItemBuilder> item) {
			super(item);
		}

		public NumberAndBooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, int max) {
			super(item, max);
		}

		public NumberAndBooleanSubSetting(@Nonnull Supplier<ItemBuilder> item, int min, int max) {
			super(item, min, max);
		}

		@Override
		public void restoreDefaults() {
			super.restoreDefaults();
			this.setEnabled(enabledByDefault);
		}

		public void setEnabled(boolean enabled) {
			if (this.enabled == enabled) return;
			this.enabled = enabled;

			if (enabled) this.onEnable();
			else this.onDisable();

			this.updateItems();
		}

		@Override
		public boolean getAsBoolean() {
			return enabled;
		}

		@Override
		public void handleClick(@Nonnull ChallengeMenuClickInfo info) {
			if (info.isUpperItemClick() || !enabled) {
				this.setEnabled(!enabled);
				SoundSample.playStatusSound(info.getPlayer(), enabled);
			} else {
				super.handleClick(info);
			}
		}

		@Override
		public void loadSettings(@Nonnull Document document) {
			super.loadSettings(document);
			this.setEnabled(document.getBoolean("enabled"));
		}

		@Override
		public void writeSettings(@Nonnull Document document) {
			super.writeSettings(document);
			document.set("enabled", enabled);
		}

		protected void onEnable() {
		}

		protected void onDisable() {
		}

	}

	private final class SettingMenuPosition implements MenuPosition {

		private final MenuPosition before;
		private final Inventory inventoryBefore;
		private final int page;

		public SettingMenuPosition(@Nonnull MenuPosition before, @Nonnull Inventory inventoryBefore, int page) {
			this.before = before;
			this.inventoryBefore = inventoryBefore;
			this.page = page;
		}

		@Override
		public void handleClick(@Nonnull MenuClickInfo info) {

			if (info.getSlot() == SettingsMenuGenerator.NAVIGATION_SLOTS[0]) {
				if (page == 0) {
					MenuPosition.set(info.getPlayer(), before);
					info.getPlayer().openInventory(inventoryBefore);
				} else {
					open(info.getPlayer(), inventoryBefore, before, page - 1);
				}
				SoundSample.CLICK.play(info.getPlayer());
				return;
			} else if (info.getSlot() == SettingsMenuGenerator.NAVIGATION_SLOTS[1]) {
				open(info.getPlayer(), inventoryBefore, before, page + 1);
				SoundSample.CLICK.play(info.getPlayer());
				return;
			}

			for (SubSetting setting : settings.values()) {
				if (setting.page != page) continue;
				if (info.getSlot() != setting.slot && info.getSlot() != setting.slot + 9) continue;

				setting.handleClick(new ChallengeMenuClickInfo(info, info.getSlot() == setting.slot));
				return;
			}

			SoundSample.CLICK.play(info.getPlayer());

		}

	}

}
