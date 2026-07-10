package net.codingarea.challenges.plugin.utils.misc;

import org.bukkit.Material;

public final class ArmorUtils {

  private ArmorUtils() {
  }

  public static Material[] getArmor() {
    return new Material[]{
      Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.IRON_HELMET,
      Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.DIAMOND_HELMET,
      Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.GOLDEN_HELMET,
      Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.LEATHER_HELMET,
      Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_HELMET,
      Utils.getMaterial("NETHERITE_CHESTPLATE"), Utils.getMaterial("NETHERITE_LEGGINGS"),
      Utils.getMaterial("NETHERITE_BOOTS"), Utils.getMaterial("NETHERITE_HELMET"),
      Material.TURTLE_HELMET
    };
  }

  // TODO wrong class...
  public static Material[] getBuckets() {
    return new Material[]{
      Material.BUCKET, Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.MILK_BUCKET,
      Material.FISHING_ROD, Material.SALMON_BUCKET, Material.COD_BUCKET, Material.PUFFERFISH_BUCKET,
      Material.SALMON_BUCKET, Material.TROPICAL_FISH_BUCKET, Utils.getMaterial("AXOLOTL_BUCKET"),
      Utils.getMaterial("POWDER_SNOW_BUCKET")
    };
  }

  // TODO wrong class...
  public static Material[] getSwords() {
    return new Material[]{
      Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD,
      Material.GOLDEN_SWORD, Material.NETHERITE_SWORD
    };
  }

  // TODO wrong class...
  public static Material[] getPickaxes() {
    return new Material[]{
      Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE,
      Material.GOLDEN_PICKAXE, Material.NETHERITE_PICKAXE
    };
  }

}
