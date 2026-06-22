package net.codingarea.challenges.plugin.management.menu;

import lombok.Getter;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SettingCategory {

  // Challenges
  public static final SettingCategory MISC_CHALLENGE = new SettingCategory(99, Material.MINECART, "misc_challenge");
  public static final SettingCategory RANDOMIZER = new SettingCategory(1, Material.COMMAND_BLOCK, "randomizer");
  public static final SettingCategory FORCE = new SettingCategory(2, Material.BLUE_BANNER, "force");
  public static final SettingCategory ENTITIES = new SettingCategory(3, Material.PIG_SPAWN_EGG, "entities");
  public static final SettingCategory DAMAGE = new SettingCategory(4, MinecraftNameWrapper.RED_DYE, "damage");
  public static final SettingCategory EFFECT = new SettingCategory(5, Material.FERMENTED_SPIDER_EYE, "effect");
  public static final SettingCategory WORLD = new SettingCategory(6, Material.TNT, "world");
  public static final SettingCategory INVENTORY = new SettingCategory(7, Material.CHEST, "inventory");
  public static final SettingCategory MOVEMENT = new SettingCategory(8, Material.RABBIT_FOOT, "movement");
  public static final SettingCategory LIMITED_TIME = new SettingCategory(9, Material.CLOCK, "limited_time");
  public static final SettingCategory EXTRA_WORLD = new SettingCategory(10, Material.GRASS_BLOCK, "extra_world");

  // Goals
  public static final SettingCategory MISC_GOAL = new SettingCategory(99, Material.MINECART, "misc_goal");
  public static final SettingCategory KILL_ENTITY = new SettingCategory(1, Material.BOW, "kill_entity");
  public static final SettingCategory SCORE_POINTS = new SettingCategory(2, Material.CHEST, "score_points");
  public static final SettingCategory FASTEST_TIME = new SettingCategory(3, Material.CLOCK, "fastest_time");
  public static final SettingCategory FORCE_BATTLE = new SettingCategory(4, Material.BLUE_BANNER, "force_battle");

  @Getter
  private final int priority; // Lowest priority will be displayed first

  private final String messageKey;
  private final Material displayItemMaterial;

  public SettingCategory(int priority, @NotNull Material displayItemMaterial, @NotNull String messageKey) {
    this.priority = priority;
    this.displayItemMaterial = displayItemMaterial;
    this.messageKey = messageKey;
  }

  @NotNull
  public ItemStack getDisplayItemPreset() {
    return new ItemStack(displayItemMaterial);
  }

  @NotNull
  public MessageKey getDisplayName() {
    return MessageKey.of("category." + messageKey + ".name");
  }

  @NotNull
  public MessageKey getDescription() {
    return MessageKey.of("category." + messageKey + ".desc");
  }

  @NotNull
  public MessageKey getMenuName() {
    return MessageKey.of("category." + messageKey + ".menu");
  }

  @NotNull
  public MessageKey getDescriptionInfo() {
    return MessageKey.of("category." + messageKey + ".info");
  }

  @Deprecated
  public LegacyItemBuilder getDisplayItem() {
    return new LegacyItemBuilder(displayItemMaterial);
  }

}
