package net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.anweisen.utilities.common.misc.StringUtils;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.ValueSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.impl.BooleanSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.impl.ModifierSetting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.impl.MessageManager;
import net.codingarea.challenges.plugin.management.menu.generator.MenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.IParentCustomGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.SubSettingValueMenuGenerator;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.entity.Player;

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
  public MenuGenerator getGenerator(Player player, IParentCustomGenerator parentGenerator, String title) {
    return new SubSettingValueMenuGenerator(parentGenerator, new LinkedHashMap<>(getDefaultSettings()), title);
  }

  @Override
  public List<String> getDisplay(Map<String, String[]> activated) {
    List<String> display = Lists.newLinkedList();

    for (Entry<String, String[]> entry : activated.entrySet()) {

      for (ValueSetting setting : defaultSettings.keySet()) {

        if (entry.getKey().equals(setting.getKey())) {

          ItemBuilder builder = setting.getSettingsItem(entry.getValue()[0]);
          if (builder != null) {
            display.add("§7" + getKeyTranslation(entry.getKey()) + " " + builder.getName());

          }

        }

      }

    }

    return display;
  }

  public String getKeyTranslation(String key) {
    String messageName = "custom-subsetting-" + key;
    return MessageManager.hasMessageInCache(messageName)
      ? Message.forName(messageName).asString() : StringUtils.getEnumName(key);
  }

  public ValueSubSettingsBuilder addBooleanSetting(String key, ItemBuilder displayItem,
                                                   boolean defaultValue) {
    defaultSettings.put(new BooleanSetting(key, displayItem),
      defaultValue ? "enabled" : "disabled");
    return this;
  }

  public ValueSubSettingsBuilder addModifierSetting(String key, ItemBuilder displayItem,
                                                    int defaultValue, int min, int max) {
    defaultSettings.put(new ModifierSetting(key, min, max, displayItem),
      String.valueOf(defaultValue));
    return this;
  }

  public ValueSubSettingsBuilder addModifierSetting(String key, ItemBuilder displayItem,
                                                    int defaultValue, int min, int max, Function<Integer, String> prefixGetter, Function<Integer, String> suffixGetter) {
    defaultSettings.put(new ModifierSetting(key, min, max, displayItem, prefixGetter, suffixGetter),
      String.valueOf(defaultValue));
    return this;
  }

  public ValueSubSettingsBuilder addModifierSetting(String key, ItemBuilder displayItem,
                                                    int defaultValue, int min, int max, Function<Integer, ItemBuilder> settingsItemGetter) {
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
