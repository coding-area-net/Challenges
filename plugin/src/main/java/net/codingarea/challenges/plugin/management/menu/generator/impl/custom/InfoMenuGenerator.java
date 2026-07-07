package net.codingarea.challenges.plugin.management.menu.generator.impl.custom;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.CustomChallenge;
import net.codingarea.challenges.plugin.challenges.custom.settings.SettingType;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.ChallengeAction;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.SinglePageMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.legacy.custom.IParentCustomGenerator;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.common.collection.IRandom;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

// TODO rename to CustomChallengeMenuGenerator
public class InfoMenuGenerator extends SinglePageMenuGenerator implements IParentCustomGenerator {

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

  public InfoMenuGenerator(@NotNull CustomChallenge customChallenge) {
    this.menuType = MenuType.CUSTOM;
    this.uuid = customChallenge.getUniqueId();
    this.material = customChallenge.getMaterial();
    this.name = customChallenge.getDisplayName();
    this.trigger = customChallenge.getTrigger();
    this.subTriggers = customChallenge.getSubTriggers();
    this.action = customChallenge.getAction();
    this.subActions = customChallenge.getSubActions();
  }

  /**
   * Default Settings for new Custom Challenges
   */
  public InfoMenuGenerator() {
    this.menuType = MenuType.CUSTOM;
    this.trigger = null;
    this.action = null;
    this.subTriggers = new HashMap<>();
    this.subActions = new HashMap<>();
    this.uuid = UUID.randomUUID();
    this.material = IRandom.threadLocal().choose(CustomChooseMaterialMenuGenerator.MATERIALS);
    this.name = "§7Custom §e#" +
      (Challenges.getInstance().getCustomChallengesLoader().getCustomChallenges().size() + 1);
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

    // Display Item
    inventory.setItem(MATERIAL_SLOT, new ItemBuilder(locale, material == null ? Material.BARRIER : material,
      MessageKey.of("menu.custom.info.item-material"), material != null ? material : MessageKey.of("generic.none")).build());

    // Name Item
    // TODO escape, format?
    int maxNameLength = Challenges.getInstance().getCustomChallengesLoader().getMaxNameLength();
    inventory.setItem(NAME_SLOT, new ItemBuilder(locale, Material.NAME_TAG, MessageKey.of("menu.custom.info.item-name"),
      name, maxNameLength).build());

    // TODO TRIGGER; CONDITION; ACTION
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

  public class CustomChallengeMenuPosition extends SinglePageGeneratorMenuPosition {

    @Override
    public boolean handleMenuClick(@NotNull MenuClickInfo info) {
      switch (info.getSlot()) {
        case MATERIAL_SLOT -> {
          chooseMaterialGenerator.openMenu(info.getPlayer());
          SoundSample.CLICK.play(info.getPlayer());
          return true;
        }
      }

      return false;
    }

  }

}
