package net.codingarea.challenges.plugin.challenges.type.abstraction.menu;

import lombok.Getter;
import lombok.Setter;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public abstract class MenuSetting extends Setting {

  private final Map<String, SubSetting> settings = new LinkedHashMap<>();
  private final SubSettingsMenuGenerator menuGenerator;

  public MenuSetting(@NotNull MenuType menu, @Nullable SettingCategory category,
                     @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(menu, category, displayItemPreset, nameMessageKey);
    this.menuGenerator = new SubSettingsMenuGenerator(this);
  }

  protected final void registerSetting(@NotNull String name, @NotNull SubSetting setting) {
    if (name.equals("enabled")) throw new IllegalArgumentException("SubSetting name 'enabled' is reserved");
    settings.put(name, setting);
    menuGenerator.addToCache(setting);
    Challenges.getInstance().registerListener(setting);
  }

  public final SubSetting getSetting(@NotNull String name) {
    return settings.get(name);
  }

  @Override
  public void handleClick(@NotNull ChallengeMenuClickInfo info) {
    if (isEnabled() && !info.isRightClick() && info.isLowerItemClick()) {
      openMenu(info);
    } else {
      super.handleClick(info);
    }
  }

  private void openMenu(@NotNull ChallengeMenuClickInfo event) {
    menuGenerator.openMenu(event.getPlayer());
  }

  @NotNull
  @Override
  public ItemStack getSettingsItemPreset() {
    return DefaultItems.createCustomizePreset();
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingsName() {
    return MessageKey.of("generic.customize");
  }

  @Override
  public void writeSettings(@NotNull Document document) {
    document.set("enabled", isEnabled());
    for (Entry<String, SubSetting> entry : settings.entrySet()) {
      Document subDocument = document.getDocument(entry.getKey());
      entry.getValue().writeSettings(subDocument);
    }
  }

  @Override
  public void loadSettings(@NotNull Document document) {
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

  // TODO extract duplicate logic with AbstractChallenge
  public abstract class SubSetting implements Listener {

    @Setter
    @Getter
    private int page = -1, slot = -1;

    protected final ItemStack displayItemPreset;

    public SubSetting(@NotNull ItemStack displayItemPreset) {
      this.displayItemPreset = displayItemPreset;
    }

    public final void updateItems() {
      menuGenerator.updateElementDisplay(this);
    }

    @NotNull
    public ItemBuilder getDisplayItem(@NotNull Locale locale) {
      return DefaultItems.createChallengeDisplayFormat(displayItemPreset, getDisplayName(), getDisplayDescription(), locale);
    }

    @NotNull
    public ItemBuilder getSettingsItem(@NotNull Locale locale) {
      ItemStack preset = isEnabled() ? getSettingsItemPreset() : DefaultItems.createDisabledPreset();
      // apply formatting dynamically, to prevent duplicate format references
      ItemBuilder item = new ItemBuilder(locale, preset, MessageKey.of("challenge.subsettings-format"),
        isEnabled() ? getSettingsName() : MessageKey.of("disabled")); // no need to override disabled name/item

      LocalizableMessage description = getSettingsDescription();
      if (description != null && isEnabled()) {
        item.appendLore(MessageKey.of("challenge.subsettings-format-lore"), description);
      }

      return item;
    }

    @NotNull
    public abstract ItemStack getSettingsItemPreset();

    @NotNull
    protected abstract LocalizableMessage getDisplayName();

    @NotNull
    protected abstract LocalizableMessage getDisplayDescription();

    @NotNull
    protected LocalizableMessage getSettingsName() {
      return MessageKey.of("generic.enabled");
    }

    @Nullable
    protected LocalizableMessage getSettingsDescription() {
      return null;
    }

    public boolean isEnabled() {
      return MenuSetting.this.isEnabled() && getAsBoolean();
    }

    public abstract int getAsInt();

    public abstract boolean getAsBoolean();

    public abstract void restoreDefaults();

    public abstract void loadSettings(@NotNull Document document);

    public abstract void writeSettings(@NotNull Document document);

    public abstract void handleClick(@NotNull ChallengeMenuClickInfo info);

  }

  public class BooleanSubSetting extends SubSetting {

    private final boolean enabledByDefault;
    private boolean enabled;

    public BooleanSubSetting(@NotNull ItemStack displayItemPreset) {
      this(displayItemPreset, false);
    }

    public BooleanSubSetting(@NotNull ItemStack displayItemPreset, boolean enabledByDefault) {
      super(displayItemPreset);
      this.enabledByDefault = enabledByDefault;
      this.setEnabled(enabledByDefault);
    }

    @NotNull
    @Override
    public ItemStack getSettingsItemPreset() {
      return DefaultItems.createEnabledPreset();
    }

    @NotNull
    @Override
    protected LocalizableMessage getDisplayName() {
      return null; // TODO
    }

    @NotNull
    @Override
    protected LocalizableMessage getDisplayDescription() {
      return null; // TODO
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

    @NotNull
    public BooleanSubSetting setEnabled(boolean enabled) {
      if (this.enabled == enabled) return this;
      this.enabled = enabled;

      if (enabled) this.onEnable();
      else this.onDisable();

      this.updateItems();
      return this;
    }

    @Override
    public final void handleClick(@NotNull ChallengeMenuClickInfo info) {
      this.setEnabled(!enabled);
      SoundSample.playStatusSound(info.getPlayer(), enabled);
    }

    @Override
    public void loadSettings(@NotNull Document document) {
      this.setEnabled(document.getBoolean("enabled"));
    }

    @Override
    public void writeSettings(@NotNull Document document) {
      document.set("enabled", enabled);
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

  }

  public class NumberSubSetting extends SubSetting {

    // TODO name, desc
    private final int max, min;
    private final int defaultValue;
    @Getter
    private int value;

    public NumberSubSetting(@NotNull ItemStack displayItemPreset) {
      this(displayItemPreset, 64);
    }

    public NumberSubSetting(@NotNull ItemStack displayItemPreset, int max) {
      this(displayItemPreset, max, 1);
    }

    public NumberSubSetting(@NotNull ItemStack displayItemPreset, int min, int max) {
      this(displayItemPreset, min, max, min);
    }

    public NumberSubSetting(@NotNull ItemStack displayItemPreset, int min, int max, int defaultValue) {
      super(displayItemPreset);
      if (max <= min) throw new IllegalArgumentException("max <= min");
      if (min < 0) throw new IllegalArgumentException("min < 0");
      if (defaultValue > max) throw new IllegalArgumentException("defaultValue > max");
      if (defaultValue < min) throw new IllegalArgumentException("defaultValue < min");
      this.value = defaultValue;
      this.defaultValue = defaultValue;
      this.max = max;
      this.min = min;
    }

    @Override
    public @NotNull ItemStack getSettingsItemPreset() {
      return DefaultItems.createValuePreset(value);
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

    @NotNull
    @Override
    protected LocalizableMessage getDisplayName() {
      return null; // TODO
    }

    @NotNull
    @Override
    protected LocalizableMessage getDisplayDescription() {
      return null; // TODO
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
    public void handleClick(@NotNull ChallengeMenuClickInfo info) {
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
    public void loadSettings(@NotNull Document document) {
      this.setValue(document.getInt("value"));
    }

    @Override
    public void writeSettings(@NotNull Document document) {
      document.set("value", value);
    }

    protected void onValueChange() {
    }

  }

  public class NumberAndBooleanSubSetting extends NumberSubSetting {

    private final boolean enabledByDefault = false; // Implement in future
    private boolean enabled;

    public NumberAndBooleanSubSetting(@NotNull ItemStack displayItemPreset) {
      super(displayItemPreset);
    }

    public NumberAndBooleanSubSetting(@NotNull ItemStack displayItemPreset, int max) {
      super(displayItemPreset, max);
    }

    public NumberAndBooleanSubSetting(@NotNull ItemStack displayItemPreset, int min, int max) {
      super(displayItemPreset, min, max);
    }

    public NumberAndBooleanSubSetting(@NotNull ItemStack displayItemPreset, int min, int max, int defaultValue) {
      super(displayItemPreset, min, max, defaultValue);
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
    public void handleClick(@NotNull ChallengeMenuClickInfo info) {
      if (info.isUpperItemClick() || !enabled) {
        this.setEnabled(!enabled);
        SoundSample.playStatusSound(info.getPlayer(), enabled);
      } else {
        super.handleClick(info);
      }
    }

    @Override
    public void loadSettings(@NotNull Document document) {
      super.loadSettings(document);
      this.setEnabled(document.getBoolean("enabled"));
    }

    @Override
    public void writeSettings(@NotNull Document document) {
      super.writeSettings(document);
      document.set("enabled", enabled);
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

  }

}
