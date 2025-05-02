package net.codingarea.challenges.plugin.management.menu.generator.implementation.custom;

import lombok.Getter;
import lombok.ToString;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;
import net.codingarea.commons.common.collection.IRandom;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.CustomChallenge;
import net.codingarea.challenges.plugin.challenges.custom.settings.SettingType;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.ChallengeAction;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.ChallengeMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.MenuGenerator;
import net.codingarea.challenges.plugin.spigot.listener.ChatInputListener;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils.InventorySetter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.*;

@ToString
public class InfoMenuGenerator extends MenuGenerator implements IParentCustomGenerator {

  public static final int DELETE_SLOT = 19 + 9, SAVE_SLOT = 25 + 9, CONDITION_SLOT = 21 + 9, ACTION_SLOT = 23 + 9, MATERIAL_SLOT = 14, NAME_SLOT = 12;

  private static final Material[] defaultMaterials;
  private static final boolean savePlayerChallenges;

  static {
    savePlayerChallenges = Challenges.getInstance().getConfigDocument().getBoolean("save-player_challenges");
    ArrayList<Material> list = new ArrayList<>(Arrays.asList(ExperimentalUtils.getMaterials()));
    list.removeIf(material1 -> !material1.isItem());
    defaultMaterials = list.toArray(new Material[0]);
  }

  private final UUID uuid;
  private String name;
  private Material material;
  private ChallengeTrigger trigger;
  private Map<String, String[]> subTriggers;
  private ChallengeAction action;
  private Map<String, String[]> subActions;
  private Inventory inventory;

  public InfoMenuGenerator(CustomChallenge customChallenge) {
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
    this.trigger = null;
    this.action = null;
    this.subTriggers = new HashMap<>();
    this.subActions = new HashMap<>();
    this.uuid = UUID.randomUUID();
    this.material = IRandom.threadLocal().choose(defaultMaterials);
    this.name = "§7Custom §e#" +
      (Challenges.getInstance().getCustomChallengesLoader().getCustomChallenges().size() + 1);
  }

  public static List<String> getSubSettingsDisplay(SubSettingsBuilder builder, Map<String, String[]> activated) {
    List<String> display = new LinkedList<>();
    for (SubSettingsBuilder child : builder.getAllChildren()) {
      display.addAll(child.getDisplay(activated));
    }
    return display;
  }

  @Override
  public void generateInventories() {
    inventory = Bukkit.createInventory(MenuPosition.HOLDER, 6 * 9, InventoryTitleManager.getTitle(MenuType.CUSTOM, "Info"));
    InventoryUtils.fillInventory(inventory, ItemBuilder.FILL_ITEM);

    updateItems();

    InventoryUtils.setNavigationItems(inventory, new int[]{36 + 9}, true, InventorySetter.INVENTORY, 0, 1);
  }

  public void updateItems() {
    String currently = Message.forName("custom-info-currently").asString();
    String none = Message.forName("none").asString();

    // Save / Delete Item
    inventory.setItem(DELETE_SLOT, new ItemBuilder(Material.BARRIER, Message.forName("item-custom-info-delete")).build());
    inventory.setItem(SAVE_SLOT, new ItemBuilder(Material.LIME_DYE, Message.forName("item-custom-info-save")).build());

    // Trigger Item
    ItemBuilder triggerItem = new ItemBuilder(Material.WITHER_SKELETON_SKULL,
      Message.forName("item-custom-info-trigger"))
      .appendLore(
        currently + (trigger != null ? Message.forName(trigger.getMessage()) : none));
    if (trigger != null) {
      triggerItem.appendLore(getSubSettingsDisplay(trigger.getSubSettingsBuilder(), subTriggers));
    }
    inventory.setItem(CONDITION_SLOT, triggerItem.build());

    // Action Item
    ItemBuilder actionItem = new ItemBuilder(Material.NETHER_STAR,
      Message.forName("item-custom-info-action"))
      .appendLore(currently + (action != null ? Message.forName(action.getMessage()) : none));
    if (action != null) {
      actionItem.appendLore(getSubSettingsDisplay(action.getSubSettingsBuilder(), subActions));
    }
    inventory.setItem(ACTION_SLOT, actionItem.build());

    // Display Item
    inventory.setItem(MATERIAL_SLOT, new ItemBuilder(material == null ? Material.BARRIER : material, Message.forName("item-custom-info-material"))
      .appendLore(currently + (material != null ? BukkitStringUtils.getItemName(material).toPlainText() : none)).build());

    // Name Item
    inventory.setItem(NAME_SLOT, new ItemBuilder(Material.NAME_TAG, Message.forName("item-custom-info-name"))
      .appendLore(currently + "§7" + name).build());
  }

  @Override
  public List<Inventory> getInventories() {
    return Collections.singletonList(inventory);
  }

  @Override
  public MenuPosition getMenuPosition(int page) {
    return new InfoMenuPosition(page, this);
  }

  @Override
  public void open(@Nonnull Player player, int page) {
    if (inventory == null) generateInventories();
    super.open(player, page);
  }

  @Override
  public void accept(Player player, SettingType type, Map<String, String[]> data) {
    open(player, 0);

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
        updateItems();
        break;
    }

    updateItems();
  }

  public void setName(String name) {
    this.name = name;
    updateItems();
  }

  @Override
  public void decline(Player player) {
    open(player, 0);
  }

  public CustomChallenge save() {
    return Challenges.getInstance().getCustomChallengesLoader().registerCustomChallenge(uuid, material, name,
      trigger, subTriggers, action, subActions, true);
  }

  public void openChallengeMenu(Player player) {
    CustomChallenge challenge = Challenges.getInstance().getCustomChallengesLoader()
      .getCustomChallenges().get(uuid);
    if (challenge == null) {
      Challenges.getInstance().getMenuManager().openMenu(player, MenuType.CUSTOM, 0);
    } else {
      ChallengeMenuGenerator menuGenerator = (ChallengeMenuGenerator) MenuType.CUSTOM.getMenuGenerator();
      int page = menuGenerator.getPageOfChallenge(challenge) + 1; // +1 because the main and challenge menu are in the same generator
      Challenges.getInstance().getMenuManager().openMenu(player, MenuType.CUSTOM, page);
    }
  }

  public class InfoMenuPosition implements MenuPosition {

    private final int page;
    @Getter
    private final InfoMenuGenerator generator;

    public InfoMenuPosition(int page, InfoMenuGenerator generator) {
      this.page = page;
      this.generator = generator;
    }

    @Override
    public void handleClick(@Nonnull MenuClickInfo info) {
      if (InventoryUtils.handleNavigationClicking(generator, new int[]{36 + 9}, page, info, () -> Challenges.getInstance().getMenuManager().openMenu(info.getPlayer(), MenuType.CUSTOM, 0))) {
        return;
      }

      if (ChallengeMenuGenerator.playNoPermissionsEffect(info.getPlayer())) {
        info.getPlayer().closeInventory();
        return;
      }

      Player player = info.getPlayer();

      switch (info.getSlot()) {
        case DELETE_SLOT:
          if (!Challenges.getInstance().getCustomChallengesLoader().getCustomChallenges().containsKey(uuid)) {
            Message.forName("custom-not-deleted").send(player, Prefix.CUSTOM);
            SoundSample.BASS_OFF.play(player);
            break;
          }
          openChallengeMenu(player);
          Challenges.getInstance().getCustomChallengesLoader().unregisterCustomChallenge(uuid);
          new SoundSample().addSound(Sound.ENTITY_WITHER_BREAK_BLOCK, 0.4f).play(player);
          break;
        case SAVE_SLOT:

          String defaults = new InfoMenuGenerator().toString();
          String current = InfoMenuGenerator.this.toString();
          if (defaults.equals(current)) {
            Message.forName("custom-no-changes").send(player, Prefix.CUSTOM);
            SoundSample.BASS_OFF.play(player);
            return;
          }

          save();
          openChallengeMenu(player);
          Message.forName("custom-saved").send(player, Prefix.CUSTOM);
          if (savePlayerChallenges) {
            Message.forName("custom-saved-db").send(player, Prefix.CUSTOM);
          }
          SoundSample.LEVEL_UP.play(player);
          break;
        case CONDITION_SLOT:
          new CustomMainSettingsMenuGenerator(generator, SettingType.CONDITION,
            "trigger", Message.forName("custom-title-trigger").asString(),
            ChallengeTrigger.getMenuItems(),
            s -> Challenges.getInstance().getCustomSettingsLoader().getTriggerByName(s))
            .open(player, 0);
          SoundSample.CLICK.play(player);
          break;
        case ACTION_SLOT:
          new CustomMainSettingsMenuGenerator(generator, SettingType.ACTION,
            "action", Message.forName("custom-title-action").asString(),
            ChallengeAction.getMenuItems(),
            s -> Challenges.getInstance().getCustomSettingsLoader().getActionByName(s))
            .open(player, 0);
          SoundSample.CLICK.play(player);
          break;
        case NAME_SLOT:

          Message.forName("custom-name-info").send(player, Prefix.CUSTOM);
          player.closeInventory();

          ChatInputListener.setInputAction(player, event -> {
            int maxNameLength = Challenges.getInstance().getCustomChallengesLoader()
              .getMaxNameLength();
            if (event.getMessage().length() > maxNameLength) {
              Message.forName("custom-chars-max_length").send(event.getPlayer(), Prefix.CUSTOM, maxNameLength);
              return;
            }

            Bukkit.getScheduler().runTask(Challenges.getInstance(), () -> {
              setName(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
              open(event.getPlayer(), 0);
            });

          });

          break;
        case MATERIAL_SLOT:
          MaterialMenuGenerator materialMenuGenerator = new MaterialMenuGenerator(generator);
          materialMenuGenerator.open(player, 0);
          SoundSample.CLICK.play(player);
          break;
        default:
          SoundSample.CLICK.play(player);
          break;
      }
    }

  }

}
