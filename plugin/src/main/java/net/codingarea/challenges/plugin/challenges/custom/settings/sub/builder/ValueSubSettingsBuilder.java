package net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.ValueSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.impl.BooleanSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.impl.ModifierSetting;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.generator.AbstractMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.custom.IParentCustomGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.custom.choose.SubSettingValueMenuGenerator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class ValueSubSettingsBuilder extends GeneratorSubSettingsBuilder {

  private final LinkedHashMap<ValueSetting, String> defaultSettings = new LinkedHashMap<>();

  public ValueSubSettingsBuilder() {
    super("value");
  }

  public ValueSubSettingsBuilder(SubSettingsBuilder parent) {
    super("value", parent);
  }

  @Override
  public AbstractMenuGenerator getGenerator(Player player, IParentCustomGenerator parentGenerator, LocalizableMessage title) {
    return new SubSettingValueMenuGenerator(parentGenerator, new LinkedHashMap<>(getDefaultSettings()), title);
  }

  @NotNull
  @Override
  public Collection<SubSettingDisplay> getCurrentDisplayFor(@NotNull Map<String, String[]> activated) {
    List<SubSettingDisplay> display = Lists.newLinkedList();
    System.out.println(this.getClass() + " " + activated);

    // TODO logic ported from legacy code; overhaul system
    for (ValueSetting setting : defaultSettings.keySet()) {
      String[] values = activated.get(setting.getKey());
      if (values == null || values.length == 0) continue;

      String value = values[0];
      display.add(new SubSettingDisplay(setting.getSubName(), setting.getSettingsName(value)));
    }

    return display;
  }

  public ValueSubSettingsBuilder addBooleanSetting(@NotNull String key, @NotNull ItemStack displayItemPreset,
                                                   @NotNull MessageKey messageKeyNamespace, boolean defaultValue) {
    defaultSettings.put(new BooleanSetting(key, displayItemPreset, messageKeyNamespace),
      defaultValue ? "enabled" : "disabled");
    return this;
  }

  public ValueSubSettingsBuilder addModifierSetting(@NotNull String key, @NotNull ItemStack displayItemPreset,
                                                    @NotNull MessageKey messageKeyNamespace, int defaultValue, int min, int max) {
    defaultSettings.put(new ModifierSetting(key, displayItemPreset, messageKeyNamespace, min, max),
      String.valueOf(defaultValue));
    return this;
  }

  public ValueSubSettingsBuilder addModifierSetting(@NotNull String key, @NotNull ItemStack displayItemPreset,
                                                    @NotNull MessageKey messageKeyNamespace, int defaultValue, int min, int max,
                                                    @NotNull Function<? super Integer, LocalizableMessage> settingsFormatGetter) {
    defaultSettings.put(new ModifierSetting(key, displayItemPreset, messageKeyNamespace, min, max, settingsFormatGetter),
      String.valueOf(defaultValue));
    return this;
  }

  public ValueSubSettingsBuilder fill(Consumer<ValueSubSettingsBuilder> actions) {
    actions.accept(this);
    return this;
  }

  public boolean hasSettings() {
    return !defaultSettings.isEmpty();
  }

}
