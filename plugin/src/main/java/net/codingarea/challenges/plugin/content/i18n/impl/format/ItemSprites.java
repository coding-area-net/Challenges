package net.codingarea.challenges.plugin.content.i18n.impl.format;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Resolves a {@link Material} / {@link ItemStack} to an Adventure sprite {@link Component}
 * for boss bars, action bars, tab lists, etc. (MC 1.21.9+ object components).
 *
 * <p>Fully self-contained: no config files, no asset extraction. The override tables below
 * were machine-validated against the complete vanilla item registry and texture lists of
 * both 26.1.2 and the 26.3 snapshots (0 unresolved sprites on either). Materials that
 * don't exist on the running server version are skipped automatically, so this compiles
 * and runs across API versions.</p>
 */
final class ItemSprites {

  private ItemSprites() {
  }

  // ------------------------------------------------------------------ atlases

  private static final Key BLOCKS_ATLAS = Key.key("minecraft", "blocks");

  /**
   * Items lived in the blocks atlas only on 1.21.9/1.21.10; minecraft:items ever since.
   */
  private static final Key ITEMS_ATLAS = switch (Bukkit.getMinecraftVersion()) {
    case "1.21.9", "1.21.10" -> BLOCKS_ATLAS;
    default -> Key.key("minecraft", "items");
  };

  private static final Map<Material, Component> CACHE = new ConcurrentHashMap<>();

  // ------------------------------------------------------- irregular item sprites
  // Items whose sprite name differs from their registry key (animated / layered / absent).

  private static final Map<Material, String> ITEM_OVERRIDES = materialMap(Map.of(
    "clock", "clock_00",        // per-frame since the item-model split
    "compass", "compass_16",      // frame 16 = the classic "north" look
    "recovery_compass", "recovery_compass_16",
    "crossbow", "crossbow_standby",
    "debug_stick", "stick",           // has no texture of its own
    "tipped_arrow", "tipped_arrow_base", // tint layer can't render; base only
    "enchanted_golden_apple", "golden_apple",    // glint-only variant, no own texture
    "light", "light_15"         // plain "light" removed in 26.3+
  ));

  // ------------------------------------------------- blocks with flat ITEM sprites
  // Their block form has no matching texture, but item/<key> exists.

  private static final Set<Material> ITEM_SPRITE_BLOCKS = materialSet(
    "wheat", "nether_wart", "sugar_cane", "bamboo", "cake", "cauldron", "bell",
    "hopper", "repeater", "comparator", "brewing_stand", "flower_pot",
    "campfire", "soul_campfire", "lantern", "soul_lantern", "kelp", "seagrass",
    "sea_pickle", "pointed_dripstone", "pink_petals", "wildflowers", "leaf_litter",
    "firefly_bush", "mangrove_propagule", "pitcher_plant", "nether_sprouts",
    "barrier", "structure_void", "sniffer_egg", "sulfur_spike");

  /**
   * Tag/suffix driven so future colors, wood types & oxidation states just work.
   */
  private static boolean usesItemSprite(Material m, String key) {
    return ITEM_SPRITE_BLOCKS.contains(m)
      || Tag.DOORS.isTagged(m)
      || Tag.ALL_SIGNS.isTagged(m)       // signs + hanging signs
      || Tag.CANDLES.isTagged(m)
      || key.endsWith("_chain") || key.equals("iron_chain")
      || key.endsWith("copper_lantern"); // (waxed_ already stripped by caller)
  }

  // ------------------------------------------------------------ sprite-less items
  // Rendered as 3D block-entity models in inventory; their textures exist only as
  // unwrapped UV sheets in dedicated atlases (minecraft:chest, shulker_boxes, ...)
  // which look wrong drawn flat, so we substitute (beds, player heads) or stay empty.

  private static boolean hasNoSprite(Material m, String key) {
    return key.endsWith("chest")           // chest, trapped/ender, copper chests
      || key.endsWith("_banner")
      || key.contains("golem_statue")    // 26.x copper golem statues
      || m == Material.CONDUIT || m == Material.DECORATED_POT || m == Material.SHIELD;
  }

  private static boolean isSkull(String key) {
    return key.endsWith("_head") || key.endsWith("_skull");
  }

  // ------------------------------------------------ irregular block texture names
  // Multi-face / animated / renamed blocks. Every entry verified to exist in both
  // 26.1.2 and 26.3 assets (e.g. purpur_pillar_top, because the plain purpur_pillar
  // texture was renamed to purpur_pillar_side in 26.2+).

  private static final Map<Material, String> BLOCK_OVERRIDES = materialMap(Map.ofEntries(
    Map.entry("grass_block", "grass_block_side"),
    Map.entry("mycelium", "mycelium_side"),
    Map.entry("podzol", "podzol_side"),
    Map.entry("dirt_path", "dirt_path_top"),
    Map.entry("crafting_table", "crafting_table_front"),
    Map.entry("furnace", "furnace_front"),
    Map.entry("blast_furnace", "blast_furnace_front"),
    Map.entry("smoker", "smoker_front"),
    Map.entry("loom", "loom_front"),
    Map.entry("smithing_table", "smithing_table_front"),
    Map.entry("fletching_table", "fletching_table_front"),
    Map.entry("cartography_table", "cartography_table_side3"),
    Map.entry("grindstone", "grindstone_side"),
    Map.entry("stonecutter", "stonecutter_side"),
    Map.entry("lectern", "lectern_front"),
    Map.entry("enchanting_table", "enchanting_table_side"),
    Map.entry("jukebox", "jukebox_side"),
    Map.entry("tnt", "tnt_side"),
    Map.entry("cactus", "cactus_side"),
    Map.entry("melon", "melon_side"),
    Map.entry("pumpkin", "pumpkin_side"),
    Map.entry("hay_block", "hay_block_side"),
    Map.entry("dried_kelp_block", "dried_kelp_side"),
    Map.entry("bone_block", "bone_block_side"),
    Map.entry("snow_block", "snow"),
    Map.entry("barrel", "barrel_side"),
    Map.entry("composter", "composter_side"),
    Map.entry("dispenser", "dispenser_front"),
    Map.entry("dropper", "dropper_front"),
    Map.entry("observer", "observer_front"),
    Map.entry("crafter", "crafter_north"),
    Map.entry("piston", "piston_side"),
    Map.entry("sticky_piston", "piston_side"),
    Map.entry("daylight_detector", "daylight_detector_top"),
    Map.entry("target", "target_side"),
    Map.entry("bee_nest", "bee_nest_front"),
    Map.entry("beehive", "beehive_front"),
    Map.entry("honey_block", "honey_block_side"),
    Map.entry("bookshelf", "bookshelf"),
    Map.entry("chiseled_bookshelf", "chiseled_bookshelf_occupied"),
    Map.entry("magma_block", "magma"),
    Map.entry("basalt", "basalt_side"),
    Map.entry("polished_basalt", "polished_basalt_side"),
    Map.entry("quartz_block", "quartz_block_side"),
    Map.entry("smooth_quartz", "quartz_block_bottom"),
    Map.entry("quartz_pillar", "quartz_pillar_top"),
    Map.entry("purpur_pillar", "purpur_pillar_top"),
    Map.entry("smooth_sandstone", "sandstone_top"),
    Map.entry("smooth_red_sandstone", "red_sandstone_top"),
    Map.entry("lodestone", "lodestone_side"),
    Map.entry("respawn_anchor", "respawn_anchor_side0"),
    Map.entry("ancient_debris", "ancient_debris_side"),
    Map.entry("reinforced_deepslate", "reinforced_deepslate_side"),
    Map.entry("sculk_sensor", "sculk_sensor_top"),
    Map.entry("calibrated_sculk_sensor", "calibrated_sculk_sensor_top"),
    Map.entry("sculk_catalyst", "sculk_catalyst_side"),
    Map.entry("sculk_shrieker", "sculk_shrieker_side"),
    Map.entry("suspicious_sand", "suspicious_sand_0"),
    Map.entry("suspicious_gravel", "suspicious_gravel_0"),
    Map.entry("vault", "vault_front_off"),
    Map.entry("trial_spawner", "trial_spawner_side_inactive"),
    Map.entry("command_block", "command_block_front"),
    Map.entry("chain_command_block", "chain_command_block_front"),
    Map.entry("repeating_command_block", "repeating_command_block_front"),
    Map.entry("jigsaw", "jigsaw_side"),
    Map.entry("test_block", "test_block_start"),
    Map.entry("end_portal_frame", "end_portal_frame_side"),
    Map.entry("scaffolding", "scaffolding_side"),
    Map.entry("heavy_weighted_pressure_plate", "iron_block"),
    Map.entry("light_weighted_pressure_plate", "gold_block"),
    Map.entry("petrified_oak_slab", "oak_planks"),
    Map.entry("bamboo_fence", "bamboo_fence"),
    Map.entry("bamboo_fence_gate", "bamboo_fence_gate"),
    Map.entry("bamboo_stairs", "bamboo_planks"),
    Map.entry("bamboo_slab", "bamboo_planks"),
    Map.entry("bamboo_button", "bamboo_planks"),
    Map.entry("bamboo_pressure_plate", "bamboo_planks"),
    Map.entry("anvil", "anvil"),
    Map.entry("chipped_anvil", "chipped_anvil_top"),
    Map.entry("damaged_anvil", "damaged_anvil_top"),
    Map.entry("azalea", "azalea_top"),
    Map.entry("flowering_azalea", "flowering_azalea_top"),
    Map.entry("big_dripleaf", "big_dripleaf_top"),
    Map.entry("small_dripleaf", "small_dripleaf_top"),
    Map.entry("mangrove_roots", "mangrove_roots_side"),
    Map.entry("muddy_mangrove_roots", "muddy_mangrove_roots_side"),
    Map.entry("sniffer_egg", "sniffer_egg_not_cracked_north"), // fallback; item sprite preferred
    Map.entry("dried_ghast", "dried_ghast_hydration_0_north"),
    Map.entry("shelf_mushroom", "shelf_mushroom_stage1"),
    Map.entry("sunflower", "sunflower_front"),
    Map.entry("lilac", "lilac_top"),
    Map.entry("rose_bush", "rose_bush_top"),
    Map.entry("peony", "peony_top"),
    Map.entry("tall_grass", "tall_grass_top"),
    Map.entry("large_fern", "large_fern_top"),
    Map.entry("straw_bed", "straw_bed") // unlike colored beds, has a flat texture
  ));

  // ==================================================================== public API

  /**
   * Sprite for an item stack. Prefer this over {@link #sprite(Material)}: it renders
   * real player heads and skips stacks whose item model was customized by a
   * datapack/plugin (a vanilla-derived sprite would be misleading for those).
   */
  public static @NotNull Component sprite(@Nullable ItemStack stack) {
    if (stack == null || stack.isEmpty()) return Component.empty();

    Key model = stack.getData(DataComponentTypes.ITEM_MODEL);
    if (model != null && !model.equals(stack.getType().getKey())) return Component.empty();

    if (stack.getType() == Material.PLAYER_HEAD
      && stack.getItemMeta() instanceof SkullMeta skull
      && skull.getOwningPlayer() != null) {
      return Component.object(ObjectContents.playerHead(skull.getOwningPlayer().getUniqueId()));
    }
    return sprite(stack.getType());
  }

  /**
   * Sprite for a material, or {@link Component#empty()} if nothing usable exists.
   */
  public static @NotNull Component sprite(@Nullable Material material) {
    if (material == null || material.isAir() || !material.isItem()) return Component.empty();
    return CACHE.computeIfAbsent(material, ItemSprites::resolve);
  }

  // ==================================================================== resolution

  private static Component resolve(Material m) {
    // waxed_/infested_ blocks are pure state variants sharing the base's textures
    // AND item sprites — strip before anything else (fixes waxed copper doors etc.)
    String key = stripAliasPrefix(m.getKey().getKey());

    String itemOverride = ITEM_OVERRIDES.get(m);
    if (itemOverride != null) return item(itemOverride);
    if (isSkull(key)) return Component.empty(); // (player heads via ItemStack overload)
    if (hasNoSprite(m, key)) return Component.empty();
    if (Tag.SHULKER_BOXES.isTagged(m)) return Component.empty();

    String blockOverride = BLOCK_OVERRIDES.get(m);
    if (blockOverride != null) return block(blockOverride);

    // colored beds have no flat sprite anywhere -> wool substitute
    if (Tag.BEDS.isTagged(m) && key.endsWith("_bed")) {
      return block(key.substring(0, key.length() - 4) + "_wool");
    }

    if (!m.isBlock() || usesItemSprite(m, key)) return item(key);
    return block(deriveBlockTexture(key));
  }

  /**
   * Shaped blocks (stairs, slabs, walls, fences, panes, buttons, plates, carpets),
   * wood/hyphae and froglights have no textures of their own — derive the block
   * they borrow from, then apply that base's override if it has one.
   */
  private static String deriveBlockTexture(String key) {
    if (key.endsWith("froglight")) return key + "_side";
    if (key.endsWith("_wood")) return key.substring(0, key.length() - 5) + "_log";
    if (key.endsWith("_hyphae")) return key.substring(0, key.length() - 7) + "_stem";

    String base = null;
    for (String suffix : new String[]{"_stairs", "_slab", "_wall", "_fence_gate",
      "_fence", "_button", "_pressure_plate", "_pane", "_carpet"}) {
      if (key.endsWith(suffix)) {
        base = key.substring(0, key.length() - suffix.length());
        break;
      }
    }
    if (base == null) return key; // full block with its own texture

    // resolve the borrowed base: oak_stairs -> oak_planks, nether_brick_fence ->
    // nether_bricks, purpur_stairs -> purpur_block, moss_carpet -> moss_block,
    // white_carpet -> white_wool, smooth_sandstone_slab -> (override) sandstone_top
    for (String candidate : List.of(base, base + "_planks", base + "s",
      base + "_block", base + "_wool")) {
      Material bm = Material.matchMaterial(candidate);
      if (bm != null && bm.isBlock()) {
        String o = BLOCK_OVERRIDES.get(bm);
        return o != null ? o : candidate;
      }
    }
    return key; // graceful worst case for future unknown blocks
  }

  private static String stripAliasPrefix(String key) {
    for (String prefix : new String[]{"waxed_", "infested_"}) {
      if (key.startsWith(prefix)) {
        String stripped = key.substring(prefix.length());
        if (Material.matchMaterial(stripped) != null) return stripped;
      }
    }
    return key;
  }

  // ==================================================================== components

  private static Component item(String name) {
    return Component.object(ObjectContents.sprite(ITEMS_ATLAS, Key.key("minecraft", "item/" + name)));
  }

  private static Component block(String name) {
    return Component.object(ObjectContents.sprite(BLOCKS_ATLAS, Key.key("minecraft", "block/" + name)));
  }

  // -------------------------------------------------- version-safe table builders
  // Built from key strings via matchMaterial so entries for materials that don't
  // exist on this server/API version are silently skipped (compiles everywhere,
  // and 26.2+/26.3 entries like sulfur_spike are inert on 26.1.x).

  private static Set<Material> materialSet(String... keys) {
    return Arrays.stream(keys)
      .map(Material::matchMaterial)
      .filter(Objects::nonNull)
      .collect(Collectors.toCollection(() -> EnumSet.noneOf(Material.class)));
  }

  private static Map<Material, String> materialMap(Map<String, String> byKey) {
    Map<Material, String> out = new EnumMap<>(Material.class);
    byKey.forEach((k, v) -> {
      Material m = Material.matchMaterial(k);
      if (m != null) out.put(m, v);
    });
    return out;
  }
}
