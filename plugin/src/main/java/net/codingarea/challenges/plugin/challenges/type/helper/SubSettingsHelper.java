package net.codingarea.challenges.plugin.challenges.type.helper;

import net.anweisen.utilities.bukkit.utils.item.ItemBuilder.PotionBuilder;
import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.anweisen.utilities.common.misc.StringUtils;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder.ChooseItemSubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder.ChooseMultipleItemSubSettingBuilder;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.StructureUtils;
import org.bukkit.Material;
import org.bukkit.StructureType;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

public class SubSettingsHelper {

  public static final String
    ENTITY_TYPE = "entity_type",
    BLOCK = "block",
    ANY = "any",
    ITEM = "item",
    LIQUID = "liquid",
    TARGET_ENTITY = "target_entity",
    STRUCTURE = "structure";

  public static ChooseMultipleItemSubSettingBuilder createEntityTypeSettingsBuilder(boolean any, boolean player) {
    return SubSettingsBuilder.createChooseMultipleItem(ENTITY_TYPE).fill(builder -> {

      if (any) {
        builder.addSetting(ANY, new ItemBuilder(
          Material.NETHER_STAR, Message.forName("item-custom-setting-entity_type-any")).build());
      }
      if (player) {
        builder.addSetting("PLAYER", new ItemBuilder(Material.PLAYER_HEAD, Message.forName("item-custom-setting-entity_type-player")).build());
      }
      for (EntityType type : EntityType.values()) {
        if (!type.isSpawnable() || !type.isAlive()) continue;
        try {
          Material spawnEgg = Material.valueOf(type.name() + "_SPAWN_EGG");
          builder.addSetting(type.name(), new ItemBuilder(spawnEgg,
            DefaultItem.getItemPrefix() + BukkitStringUtils.getEntityName(type).toPlainText()).build());
        } catch (Exception ex) {
          builder.addSetting(type.name(), new ItemBuilder(Material.STRUCTURE_VOID,
            DefaultItem.getItemPrefix() + BukkitStringUtils.getEntityName(type).toPlainText()));
        }
      }
    });
  }

  public static ChooseMultipleItemSubSettingBuilder createBlockSettingsBuilder() {
    return SubSettingsBuilder.createChooseMultipleItem(BLOCK).fill(builder -> {
      builder.addSetting(ANY, new ItemBuilder(Material.NETHER_STAR, Message.forName("item-custom-setting-block-any")).build());
      for (Material material : ExperimentalUtils.getMaterials()) {
        if (material.isBlock() && material.isItem() && !BukkitReflectionUtils.isAir(material)) {
          builder.addSetting(material.name(), new ItemBuilder(material, DefaultItem.getItemPrefix() + BukkitStringUtils.getItemName(material).toPlainText()).build());
        }
      }
    });
  }

  public static ChooseMultipleItemSubSettingBuilder createItemSettingsBuilder() {
    return SubSettingsBuilder.createChooseMultipleItem(ITEM).fill(builder -> {
      builder.addSetting(ANY, new ItemBuilder(Material.NETHER_STAR, Message.forName("item-custom-setting-item-any")).build());
      for (Material material : ExperimentalUtils.getMaterials()) {
        if (material.isItem() && !BukkitReflectionUtils.isAir(material)) {
          builder.addSetting(material.name(), new ItemBuilder(material, DefaultItem.getItemPrefix() + BukkitStringUtils.getItemName(material).toPlainText()).build());
        }
      }
    });
  }

  public static SubSettingsBuilder createEntityTargetSettingsBuilder(boolean everyMob) {
    return createEntityTargetSettingsBuilder(everyMob, false);
  }

  public static SubSettingsBuilder createEntityTargetSettingsBuilder(boolean everyMob, boolean onlyPlayer) {
    return createEntityTargetSettingsBuilder(everyMob, onlyPlayer, false);
  }

  public static SubSettingsBuilder createEntityTargetSettingsBuilder(boolean everyMob, boolean onlyPlayer, boolean console) {
    ChooseItemSubSettingsBuilder builder = SubSettingsBuilder.createChooseItem(TARGET_ENTITY);

    if (console) {
      builder.addSetting("console", new ItemBuilder(Material.COMMAND_BLOCK_MINECART, Message.forName("item-custom-setting-target-console")));
    }

    if (!onlyPlayer) {
      builder.addSetting("current", new ItemBuilder(Material.DRAGON_HEAD,
        Message.forName("item-custom-setting-target-current")));
    }

    builder.addSetting("current_player", new ItemBuilder(Material.PLAYER_HEAD,
      Message.forName("item-custom-setting-target-current_player")));
    builder.addSetting("random_player", new ItemBuilder(Material.ZOMBIE_HEAD,
      Message.forName("item-custom-setting-target-random_player")));
    builder.addSetting("every_player", new ItemBuilder(Material.PLAYER_HEAD,
      Message.forName("item-custom-setting-target-every_player")));

    if (everyMob && !onlyPlayer) {
      builder.addSetting("every_mob", new ItemBuilder(Material.WITHER_SKELETON_SKULL,
        Message.forName("item-custom-setting-target-every_mob")));
      builder.addSetting("every_mob_except_current", new ItemBuilder(Material.SKELETON_SKULL,
        Message.forName("item-custom-setting-target-every_mob_except_current")));
      builder.addSetting("every_mob_except_players", new ItemBuilder(Material.SKELETON_SKULL,
        Message.forName("item-custom-setting-target-every_mob_except_players")));
    }
    return builder;
  }

  public static SubSettingsBuilder createPotionSettingsBuilder(boolean potionType,
                                                               boolean potionTime) {

    SubSettingsBuilder potionSettings = SubSettingsBuilder.createValueItem().fill(builder -> {

      if (potionTime) {
        builder.addModifierSetting("length", new ItemBuilder(Material.CLOCK,
            Message.forName("item-random-effect-length-challenge")),
          30, 1, 60,
          value -> "",
          value -> Message.forName(value == 1 ? "second" : "seconds").asString());
      }
      builder.addModifierSetting("amplifier", new ItemBuilder(Material.STONE_SWORD,
          Message.forName("item-random-effect-amplifier-challenge")),
        3, 1, 8,
        value -> Message.forName("amplifier").asString(),
        integer -> "");
    });

    if (potionType) {
      potionSettings = potionSettings.createChooseItemChild("potion_type").fill(builder -> {
        for (PotionEffectType effectType : PotionEffectType.values()) {
          builder.addSetting(effectType.getName(), new PotionBuilder(Material.POTION,
            DefaultItem.getItemPrefix() + StringUtils.getEnumName(effectType.getName()))
            .addEffect(effectType.createEffect(1, 0))
            .color(effectType.getColor()).build());
        }
      });
    }


    return potionSettings;
  }

  public static SubSettingsBuilder createStructureSettingsBuilder() {
    return SubSettingsBuilder.createChooseItem(STRUCTURE).fill(builder -> {
      builder.addSetting("random_structure", new ItemBuilder(Material.STRUCTURE_BLOCK, Message.forName("item-custom-action-place_structure-random")).build());
      for (StructureType structure : StructureType.getStructureTypes().values()) {
        builder.addSetting(structure.getName(), new ItemBuilder(StructureUtils.getStructureIcon(structure), DefaultItem.getItemPrefix() + StringUtils.getEnumName(structure.getName())).build());
      }
    });
  }

}
