package net.codingarea.challenges.plugin.challenges.custom;

import lombok.Getter;
import lombok.ToString;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeExecutionData;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.ChallengeAction;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.impl.format.ComponentFormatter;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.common.config.Document;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;

@Getter
@ToString
public class CustomChallenge extends Setting {

  private final UUID uuid;
  private Material material;
  private String name;
  private ChallengeTrigger trigger;
  private Map<String, String[]> subTriggers;
  private ChallengeAction action;
  private Map<String, String[]> subActions;

  private Component cachedNameFormatted;

  @SuppressWarnings("ConstantConditions") // passing null as "displayItemPreset" intentionally
  public CustomChallenge(MenuType menuType, UUID uuid, Material displayItem, String displayName, ChallengeTrigger trigger,
                         Map<String, String[]> subTriggers, ChallengeAction action, Map<String, String[]> subActions) {
    super(menuType, null, null, "custom-challenge");
    this.uuid = uuid;
    this.material = displayItem;
    this.name = displayName;
    this.trigger = trigger;
    this.subTriggers = subTriggers;
    this.action = action;
    this.subActions = subActions;
  }

  @Override
  public ItemStack getDisplayItemPreset() {
    if (displayItemPreset != null) return displayItemPreset;
    Material material = this.material;
    if (material == null) material = Material.BARRIER;
    return this.displayItemPreset = new ItemStack(material);
  }

  @NotNull
  @Override
  public ItemBuilder getDisplayItem(@NotNull Locale locale) {
    ItemBuilder item = new ItemBuilder(locale, getDisplayItemPreset(), MessageKey.of("custom.display-format"), getDisplayName());

    // ADDING CONDITION INFO
    if (getTrigger() != null) {
      item.appendBlankLoreLine()
        .appendLore(MessageKey.of("menu.custom.trigger-format"), trigger.getSettingName());

      Collection<SubSettingsBuilder.SubSettingDisplay> display = trigger.getSubSettingsBuilder().collectCurrentDisplayFor(subTriggers);
      for (SubSettingsBuilder.SubSettingDisplay subSettingDisplay : display) {
        item.appendLore(MessageKey.of("menu.custom.subsetting-format"), subSettingDisplay.keyName(), subSettingDisplay.valueFormatted());
      }
    }

    // ADDING ACTION INFO
    if (getAction() != null) {
      item.appendBlankLoreLine()
        .appendLore(MessageKey.of("menu.custom.action-format"), action.getSettingName());

      Collection<SubSettingsBuilder.SubSettingDisplay> display = action.getSubSettingsBuilder().collectCurrentDisplayFor(subActions);
      for (SubSettingsBuilder.SubSettingDisplay subSettingDisplay : display) {
        item.appendLore(MessageKey.of("menu.custom.subsetting-format"), subSettingDisplay.keyName(), subSettingDisplay.valueFormatted());
      }
    }

    return item;
  }

  @NotNull
  @Override
  public LocalizableMessage getChallengeName() {
    return LocalizableMessage.wrap(getDisplayName());
  }

  @Override
  public void writeSettings(@NotNull Document document) {
    super.writeSettings(document);

    document.set("material", material == null ? null : material.name());
    document.set("name", name);
    document.set("trigger", trigger == null ? null : trigger.getUniqueName());
    document.set("subTrigger", subTriggers);
    document.set("action", action == null ? null : action.getUniqueName());
    document.set("subActions", subActions);
  }

  public final void onTriggerFulfilled(ChallengeExecutionData challengeExecutionData) {
    if (isEnabled()) {

      boolean triggerMet = isTriggerMet(challengeExecutionData.getTriggerData());
      if (triggerMet) {
        executeAction(challengeExecutionData);
      }

    }
  }

  /**
   * @return if the trigger is met.
   * That is when every key in the subTriggers is contained by the data map and one or more value
   * of the lists are equal to one another.
   */
  public boolean isTriggerMet(Map<String, List<String>> data) {
    if (!subTriggers.isEmpty()) {
      for (Entry<String, String[]> entry : subTriggers.entrySet()) {
        if (!data.containsKey(entry.getKey())) {
          return false;
        }
        List<String> list = data.get(entry.getKey());

        boolean match = false;
        for (String value : entry.getValue()) {
          if (list.contains(value)) {
            match = true;
            break;
          }
        }

        if (!match) {
          return false;
        }
      }
    }
    return true;
  }

  public void executeAction(ChallengeExecutionData challengeExecutionData) {
    if (!Bukkit.isPrimaryThread()) {
      Bukkit.getScheduler().runTask(Challenges.getInstance(), () -> {
        action.execute(challengeExecutionData, subActions);
      });
      return;
    }
    action.execute(challengeExecutionData, subActions);
  }

  public void applySettings(@NotNull Material material, @NotNull String name, @NotNull ChallengeTrigger trigger,
                            Map<String, String[]> subTriggers, ChallengeAction action, Map<String, String[]> subActions) {
    this.material = material;
    this.name = name;
    this.trigger = trigger;
    this.subTriggers = subTriggers;
    this.action = action;
    this.subActions = subActions;
    this.cachedNameFormatted = null;
    this.displayItemPreset = null;
  }

  public UUID getUniqueId() {
    return uuid;
  }

  @NotNull
  public String getDisplayNameValue() {
    return name;
  }

  @NotNull
  public Component getDisplayName() {
    if (cachedNameFormatted != null) return cachedNameFormatted;
    return cachedNameFormatted = formatChallengeName(name);
  }

  @NotNull
  @Override
  public String getUniqueName() {
    return uuid.toString();
  }

  @NotNull
  public static Component formatChallengeName(@NotNull String rawName) {
    try {
      return ComponentFormatter.LEGACY_AMPERSAND.deserialize(rawName).colorIfAbsent(NamedTextColor.WHITE);
    } catch (Exception _) {
      // malformatted
      return Component.text(rawName).colorIfAbsent(NamedTextColor.WHITE);
    }
  }

}
