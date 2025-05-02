package net.codingarea.challenges.plugin.management.menu.generator.categorised;

import lombok.Getter;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Material;

import java.util.function.Supplier;

@Getter
public class SettingCategory {

  //Challenges
  public static final SettingCategory MISC_CHALLENGE = new SettingCategory(99, Material.MINECART, () -> Message.forName("category-misc_challenge"));
  public static final SettingCategory RANDOMIZER = new SettingCategory(1, Material.COMMAND_BLOCK, () -> Message.forName("category-randomizer"));
  public static final SettingCategory FORCE = new SettingCategory(2, Material.BLUE_BANNER, () -> Message.forName("category-force"));
  public static final SettingCategory ENTITIES = new SettingCategory(3, Material.PIG_SPAWN_EGG, () -> Message.forName("category-entities"));
  public static final SettingCategory DAMAGE = new SettingCategory(4, MinecraftNameWrapper.RED_DYE, () -> Message.forName("category-damage"));
  public static final SettingCategory EFFECT = new SettingCategory(5, Material.FERMENTED_SPIDER_EYE, () -> Message.forName("category-effect"));
  public static final SettingCategory WORLD = new SettingCategory(6, Material.TNT, () -> Message.forName("category-world"));
  public static final SettingCategory INVENTORY = new SettingCategory(7, Material.CHEST, () -> Message.forName("category-inventory"));
  public static final SettingCategory MOVEMENT = new SettingCategory(8, Material.RABBIT_FOOT, () -> Message.forName("category-movement"));
  public static final SettingCategory LIMITED_TIME = new SettingCategory(9, Material.CLOCK, () -> Message.forName("category-limited_time"));
  public static final SettingCategory EXTRA_WORLD = new SettingCategory(10, Material.GRASS_BLOCK, () -> Message.forName("category-extra_world"));

  //Goals
  public static final SettingCategory MISC_GOAL = new SettingCategory(99, Material.MINECART, () -> Message.forName("category-misc_goal"));
  public static final SettingCategory KILL_ENTITY = new SettingCategory(1, Material.BOW, () -> Message.forName("category-kill_entity"));
  public static final SettingCategory SCORE_POINTS = new SettingCategory(2, Material.CHEST, () -> Message.forName("category-score_points"));
  public static final SettingCategory FASTEST_TIME = new SettingCategory(3, Material.CLOCK, () -> Message.forName("category-fastest_time"));
  public static final SettingCategory FORCE_BATTLE = new SettingCategory(4, Material.BLUE_BANNER, () -> Message.forName("category-force_battle"));

  // Lowest priority will be displayed first
  private final int priority;
  private final Material material;
  private final Supplier<Message> messageSupplier;

  public SettingCategory(int priority, Material material, Supplier<Message> messageSupplier) {
    this.priority = priority;
    this.material = material;
    this.messageSupplier = messageSupplier;
  }

  public ItemBuilder getDisplayItem() {
    return new ItemBuilder(material, messageSupplier.get());
  }

}
