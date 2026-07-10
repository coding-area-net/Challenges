package net.codingarea.challenges.plugin.challenges.type.helper;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SelectableKey;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder.ChooseItemSubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder.ChooseMultipleItemSubSettingBuilder;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.StructureUtils;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import net.codingarea.commons.bukkit.utils.misc.BukkitReflectionUtils;
import net.codingarea.commons.common.misc.StringUtils;
import org.bukkit.Material;
import org.bukkit.StructureType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public final class SubSettingsHelper {

  private SubSettingsHelper() {
  }

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
        builder.addSetting(SelectableKey.of(ANY, Material.NETHER_STAR, "item-custom-setting-entity_type-any"));
      }
      if (player) {
        builder.addSetting(SelectableKey.of("PLAYER", Material.PLAYER_HEAD, "item-custom-setting-entity_type-player"));
      }
      for (EntityType type : EntityType.values()) {
        if (!type.isSpawnable() || !type.isAlive()) continue;
        Material displayMaterial;
        try {
          displayMaterial = Material.valueOf(type.name() + "_SPAWN_EGG");
        } catch (Exception ex) {
          displayMaterial = Material.STRUCTURE_VOID;
        }

        builder.addSetting(SelectableKey.of(type.name(), displayMaterial, type));
      }
    });
  }

  public static ChooseMultipleItemSubSettingBuilder createBlockSettingsBuilder() {
    return SubSettingsBuilder.createChooseMultipleItem(BLOCK).fill(builder -> {
      builder.addSetting(SelectableKey.of(ANY, Material.NETHER_STAR, "item-custom-setting-block-any"));
      for (Material material : ExperimentalUtils.getMaterials()) {
        if (material.isBlock() && material.isItem() && !BukkitReflectionUtils.isAir(material)) {
          builder.addSetting(SelectableKey.of(material.name(), material, material));
        }
      }
    });
  }

  public static ChooseMultipleItemSubSettingBuilder createItemSettingsBuilder() {
    return SubSettingsBuilder.createChooseMultipleItem(ITEM).fill(builder -> {
      builder.addSetting(SelectableKey.of(ANY, Material.NETHER_STAR, "item-custom-setting-item-any"));
      for (Material material : ExperimentalUtils.getMaterials()) {
        if (material.isItem() && !BukkitReflectionUtils.isAir(material)) {
          builder.addSetting(SelectableKey.of(material.name(), material, material));
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
      // TODO generic translation; constant keys?
      builder.addSetting(SelectableKey.of("console", Material.COMMAND_BLOCK_MINECART, "item-custom-setting-target-console"));
    }

    if (!onlyPlayer) {
      builder.addSetting(SelectableKey.of("current", Material.DRAGON_HEAD, "item-custom-setting-target-current"));
    }

    builder.addSetting(SelectableKey.of("current_player", Material.PLAYER_HEAD, "item-custom-setting-target-current_player"));
    builder.addSetting(SelectableKey.of("random_player", Material.ZOMBIE_HEAD, "item-custom-setting-target-random_player"));
    builder.addSetting(SelectableKey.of("every_player", Material.PLAYER_HEAD, "item-custom-setting-target-every_player"));

    if (everyMob && !onlyPlayer) {
      builder.addSetting(SelectableKey.of("every_mob", Material.WITHER_SKELETON_SKULL, "item-custom-setting-target-every_mob"));
      builder.addSetting(SelectableKey.of("every_mob_except_current", Material.SKELETON_SKULL, "item-custom-setting-target-every_mob_except_current"));
      builder.addSetting(SelectableKey.of("every_mob_except_players", Material.SKELETON_SKULL, "item-custom-setting-target-every_mob_except_players"));
    }
    return builder;
  }

  public static SubSettingsBuilder createPotionSettingsBuilder(boolean potionType, boolean potionTime) {
    SubSettingsBuilder potionSettings = SubSettingsBuilder.createValueItem().fill(builder -> {

      // TODO also port
      if (potionTime) {
        builder.addModifierSetting("length", new LegacyItemBuilder(Material.CLOCK,
            Message.forName("item-random-effect-length-challenge")),
          30, 1, 60,
          value -> "",
          value -> Message.forName(value == 1 ? "second" : "seconds").asString());
      }
      builder.addModifierSetting("amplifier", new LegacyItemBuilder(Material.STONE_SWORD,
          Message.forName("item-random-effect-amplifier-challenge")),
        3, 1, 8,
        value -> Message.forName("amplifier").asString(),
        integer -> "");
    });

    if (potionType) {
      potionSettings = potionSettings.createChooseItemChild("potion_type").fill(builder -> {
        for (PotionEffectType effectType : PotionEffectType.values()) {
          ItemStack displayItemPreset = new StandardItemBuilder.PotionBuilder(Material.POTION)
            .addEffect(effectType.createEffect(1, 0))
            .color(effectType.getColor()).build();

          // TODO custom centralized translation?
          builder.addSetting(SelectableKey.of(effectType.getName(), displayItemPreset, LocalizableMessage.wrap(StringUtils.getEnumName(effectType.getName()))));
        }
      });
    }


    return potionSettings;
  }

  public static SubSettingsBuilder createStructureSettingsBuilder() {
    return SubSettingsBuilder.createChooseItem(STRUCTURE).fill(builder -> {
      builder.addSetting(SelectableKey.of("random_structure", Material.STRUCTURE_BLOCK, "item-custom-action-place_structure-random"));
      for (StructureType structure : StructureType.getStructureTypes().values()) {
        builder.addSetting(SelectableKey.of(structure.getName(), StructureUtils.getStructureIcon(structure), StringUtils.getEnumName(structure.getName())));
      }
    });
  }

}
