package net.codingarea.challenges.plugin.management.menu.generator.impl.custom;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.ToString;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.CustomChallenge;
import net.codingarea.challenges.plugin.challenges.custom.settings.SettingType;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.ChallengeAction;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.SinglePageMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.custom.choose.CustomChooseMaterialMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.custom.choose.CustomMainSettingsMenuGenerator;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.chat.ChatInputHandler;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.common.collection.IRandom;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ToString
public class CustomChallengeMenuGenerator extends SinglePageMenuGenerator implements IParentCustomGenerator {

  public static final int SIZE = 5 * 9;

  public static final int DELETE_SLOT = 19 + 9;
  public static final int SAVE_SLOT = 25 + 9;
  public static final int CONDITION_SLOT = 21 + 9;
  public static final int ACTION_SLOT = 23 + 9;
  public static final int MATERIAL_SLOT = 14;
  public static final int NAME_SLOT = 12;

  private static final boolean savePlayerChallenges;

  // TODO
  static {
    savePlayerChallenges = Challenges.getInstance().getConfigDocument().getBoolean("save-player_challenges");
  }

  private final CustomChooseMaterialMenuGenerator chooseMaterialGenerator = new CustomChooseMaterialMenuGenerator(this);

  private final UUID uuid;
  private String name;
  private Material material;
  private ChallengeTrigger trigger;
  private Map<String, String[]> subTriggers;
  private ChallengeAction action;
  private Map<String, String[]> subActions;

  public CustomChallengeMenuGenerator(@NotNull CustomChallenge customChallenge) {
    this.menuType = MenuType.CUSTOM;
    this.uuid = customChallenge.getUniqueId();
    this.material = customChallenge.getMaterial();
    this.name = customChallenge.getDisplayNameValue();
    this.trigger = customChallenge.getTrigger();
    this.subTriggers = customChallenge.getSubTriggers();
    this.action = customChallenge.getAction();
    this.subActions = customChallenge.getSubActions();
  }

  /**
   * Default Settings for new Custom Challenges
   */
  public CustomChallengeMenuGenerator() {
    this.menuType = MenuType.CUSTOM;
    this.trigger = null;
    this.action = null;
    this.subTriggers = new HashMap<>();
    this.subActions = new HashMap<>();
    this.uuid = UUID.randomUUID();
    this.material = IRandom.threadLocal().choose(CustomChooseMaterialMenuGenerator.MATERIALS);
    this.name = "&7Custom &e#" + (Challenges.getInstance().getCustomChallengesLoader().getCustomChallenges().size() + 1);
  }

  @NotNull
  @Override
  public LocalizableMessage getMenuName() {
    return createSubMenuTitle(MessageKey.of("menu.custom.sub-name"), MessageKey.of("menu.custom.info.name"));
  }

  @NotNull
  @Override
  public GeneratorMenuPosition createMenuPosition(@NotNull Player player) {
    return new CustomChallengeMenuPosition();
  }

  @Override
  public void updateInventoryContent(@NotNull Inventory inventory, @NotNull Locale locale) {
    // Save / Delete Item
    inventory.setItem(DELETE_SLOT, new ItemBuilder(locale, Material.BARRIER, MessageKey.of("menu.custom.info.item-delete")).build());
    inventory.setItem(SAVE_SLOT, new ItemBuilder(locale, Material.LIME_DYE, MessageKey.of("menu.custom.info.item-save")).build());

    // Trigger Item
    LocalizableMessage triggerName = trigger == null ? MessageKey.of("generic.none") : trigger.getSettingName();
    ItemBuilder triggerItem = new ItemBuilder(locale, Material.WITHER_SKELETON_SKULL, MessageKey.of("menu.custom.info.item-trigger"),
      triggerName);
    if (trigger != null) {
      appendSubSettingDisplay(triggerItem, trigger.getSubSettingsBuilder().getCurrentDisplayFor(subTriggers));
    }
    inventory.setItem(CONDITION_SLOT, triggerItem.build());

    // Action Item
    LocalizableMessage actionName = action == null ? MessageKey.of("generic.none") : action.getSettingName();
    ItemBuilder actionItem = new ItemBuilder(locale, Material.NETHER_STAR, MessageKey.of("menu.custom.info.item-action"),
      actionName);
    if (action != null) {
      appendSubSettingDisplay(actionItem, action.getSubSettingsBuilder().getCurrentDisplayFor(subActions));
    }
    inventory.setItem(ACTION_SLOT, actionItem.build());

    // Display Item
    inventory.setItem(MATERIAL_SLOT, new ItemBuilder(locale, material == null ? Material.BARRIER : material,
      MessageKey.of("menu.custom.info.item-material"), material != null ? material : MessageKey.of("generic.none")).build());

    // Name Item
    int maxNameLength = Challenges.getInstance().getCustomChallengesLoader().getMaxNameLength();
    inventory.setItem(NAME_SLOT, new ItemBuilder(locale, Material.NAME_TAG, MessageKey.of("menu.custom.info.item-name"),
      CustomChallenge.formatChallengeName(name), maxNameLength).build());
  }

  protected void appendSubSettingDisplay(@NotNull ItemBuilder item, @NotNull Collection<SubSettingsBuilder.SubSettingDisplay> display) {
    for (SubSettingsBuilder.SubSettingDisplay settingDisplay : display) {
      item.appendLore(MessageKey.of("menu.custom.subsetting-format"), settingDisplay.keyName(), settingDisplay.valueFormatted());
    }
  }

  @Override
  public int getInventorySize() {
    return SIZE;
  }

  @Override
  public void accept(@NotNull Player player, @NotNull SettingType type, @NotNull Map<String, String[]> data) {
    openMenu(player);

    switch (type) {
      case CONDITION:
        trigger = Challenges.getInstance().getCustomSettingsLoader().getTriggerByName(data.remove("trigger")[0]);
        this.subTriggers = data;
        break;
      case ACTION:
        action = Challenges.getInstance().getCustomSettingsLoader().getActionByName(data.remove("action")[0]);
        this.subActions = data;
        break;
      case MATERIAL:
        material = Material.valueOf(data.remove("material")[0]);
        break;
    }

    updatePages();
  }

  @Override
  public void decline(@NotNull Player player) {
    openMenu(player);
  }

  public void setName(@NotNull String name) {
    this.name = name;
    updatePages();
  }

  @NotNull
  public CustomChallenge save() {
    return Challenges.getInstance().getCustomChallengesLoader()
      .registerCustomChallenge(uuid, material, name, trigger, subTriggers, action, subActions, true);
  }

  private void navigateToChallengeList(@NotNull Player player) {
    CustomHomeMenuGenerator home = (CustomHomeMenuGenerator) MenuType.CUSTOM.getMenuGenerator();
    CustomChallenge challenge = Challenges.getInstance().getCustomChallengesLoader().getCustomChallenges().get(uuid);
    if (challenge == null) {
      home.openMenu(player);
      return;
    }
    CustomListMenuGenerator list = home.getListGenerator();
    int page = Math.max(0, Math.min(list.getPageOfChallenge(challenge), list.getPageCount() - 1));
    list.openMenu(player, page);
  }

  public class CustomChallengeMenuPosition extends SinglePageGeneratorMenuPosition {

    @Override
    public boolean handleMenuClick(@NotNull MenuClickInfo info) {
      Player player = info.getPlayer();
      switch (info.getSlot()) {
        case MATERIAL_SLOT -> {
          chooseMaterialGenerator.openMenu(player);
          SoundSample.CLICK.play(player);
          return true;
        }
        case CONDITION_SLOT -> {
          new CustomMainSettingsMenuGenerator(CustomChallengeMenuGenerator.this, SettingType.CONDITION, "trigger",
            MessageKey.of("menu.custom.trigger.name"), ChallengeTrigger.getMenuItems(),
            key -> Challenges.getInstance().getCustomSettingsLoader().getTriggerByName(key))
            .openMenu(player);
          SoundSample.CLICK.play(player);
          return true;
        }
        case ACTION_SLOT -> {
          new CustomMainSettingsMenuGenerator(CustomChallengeMenuGenerator.this, SettingType.ACTION, "action",
            MessageKey.of("menu.custom.action.name"), ChallengeAction.getMenuItems(),
            key -> Challenges.getInstance().getCustomSettingsLoader().getActionByName(key))
            .openMenu(player);
          SoundSample.CLICK.play(player);
          return true;
        }
        case NAME_SLOT -> {
          MessageKey.of("menu.custom.info.name-info").send(player, Prefix.CUSTOM);
          player.closeInventory();
          ChatInputHandler.set(player, new NameChatInputHandler());
          return true;
        }
        case DELETE_SLOT -> {
          if (!Challenges.getInstance().getCustomChallengesLoader().getCustomChallenges().containsKey(uuid)) {
            MessageKey.of("menu.custom.delete.new").send(player, Prefix.CUSTOM);
            SoundSample.BASS_OFF.play(player);
            return true;
          }
          MessageKey.of("menu.custom.delete.done").send(player, Prefix.CUSTOM, CustomChallenge.formatChallengeName(name));
          navigateToChallengeList(player);
          Challenges.getInstance().getCustomChallengesLoader().unregisterCustomChallenge(uuid);
          new SoundSample().addSound(Sound.ENTITY_WITHER_BREAK_BLOCK, 0.4f).play(player);
          return true;
        }
        case SAVE_SLOT -> {
          String defaults = new CustomChallengeMenuGenerator().toString();
          String current = CustomChallengeMenuGenerator.this.toString();
          if (defaults.equals(current)) {
            MessageKey.of("menu.custom.save.no-changes").send(player, Prefix.CUSTOM);
            SoundSample.BASS_OFF.play(player);
            return true;
          }
          if (trigger == null || action == null) {
            MessageKey.of("menu.custom.save.missing-settings").send(player, Prefix.CUSTOM);
            SoundSample.BASS_OFF.play(player);
            return true;
          }
          save();
          navigateToChallengeList(player);
          MessageKey.of("menu.custom.save.done").send(player, Prefix.CUSTOM);
          if (savePlayerChallenges) {
            MessageKey.of("menu.custom.save.done-db").send(player, Prefix.CUSTOM);
          }
          SoundSample.LEVEL_UP.play(player);
          return true;
        }
      }

      return false;
    }

  }

  public class NameChatInputHandler implements ChatInputHandler {

    @Override
    public void handleChatInput(@NotNull AsyncChatEvent event, @NotNull String input) {
      int maxNameLength = Challenges.getInstance().getCustomChallengesLoader().getMaxNameLength();
      if (input.length() > maxNameLength) {
        MessageKey.of("custom-chars-max_length").send(event.getPlayer(), Prefix.CUSTOM, maxNameLength);
        return;
      }
      Bukkit.getScheduler().runTask(Challenges.getInstance(), () -> {
        setName(input);
        openMenu(event.getPlayer());
      });
    }

    @Override
    public void handleCancel(@NotNull Player player) {
      openMenu(player);
    }
  }

}
