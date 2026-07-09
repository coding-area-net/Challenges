package net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.ValueSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.impl.BooleanSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.impl.ModifierSetting;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.content.legacy.MessageManager;
import net.codingarea.challenges.plugin.management.menu.generator.AbstractMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.custom.IParentCustomGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.custom.choose.SubSettingValueMenuGenerator;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.commons.common.misc.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

    // TODO logic ported from legacy code; overhaul system
    for (ValueSetting setting : defaultSettings.keySet()) {
      String[] values = activated.get(setting.getKey());
      if (values == null || values.length == 0) continue;

      String value = values[0];

      // TODO get setting value formatted name
      display.add(new SubSettingDisplay(getKeyTranslation(setting.getKey()), LocalizableMessage.wrap(setting.getSettingsItem(value).getName())));
    }

    return display;
  }

  public ValueSubSettingsBuilder addBooleanSetting(String key, LegacyItemBuilder displayItem,
                                                   boolean defaultValue) {
    defaultSettings.put(new BooleanSetting(key, displayItem),
      defaultValue ? "enabled" : "disabled");
    return this;
  }

  public ValueSubSettingsBuilder addModifierSetting(String key, LegacyItemBuilder displayItem,
                                                    int defaultValue, int min, int max) {
    defaultSettings.put(new ModifierSetting(key, min, max, displayItem),
      String.valueOf(defaultValue));
    return this;
  }

  public ValueSubSettingsBuilder addModifierSetting(String key, LegacyItemBuilder displayItem,
                                                    int defaultValue, int min, int max, Function<Integer, String> prefixGetter, Function<Integer, String> suffixGetter) {
    defaultSettings.put(new ModifierSetting(key, min, max, displayItem, prefixGetter, suffixGetter),
      String.valueOf(defaultValue));
    return this;
  }

  public ValueSubSettingsBuilder addModifierSetting(String key, LegacyItemBuilder displayItem,
                                                    int defaultValue, int min, int max, Function<Integer, LegacyItemBuilder> settingsItemGetter) {
    defaultSettings.put(new ModifierSetting(key, min, max, displayItem, settingsItemGetter),
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
