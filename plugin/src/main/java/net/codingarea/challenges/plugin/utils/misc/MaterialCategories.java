package net.codingarea.challenges.plugin.utils.misc;

import org.bukkit.Material;

public final class MaterialCategories {

  private MaterialCategories() {
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

  public static Material[] getBuckets() {
    return new Material[]{
      Material.BUCKET, Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.MILK_BUCKET,
      Material.COD_BUCKET, Material.SALMON_BUCKET, Material.PUFFERFISH_BUCKET, Material.TROPICAL_FISH_BUCKET, // 1.13
      Material.AXOLOTL_BUCKET, Material.POWDER_SNOW_BUCKET, // 1.17
      Utils.getMaterial("TADPOLE_BUCKET"), // 1.19
      Utils.getMaterial("SULFUR_CUBE_BUCKET") // 26.2
    };
  }

  public static Material[] getSnowballAndEggs() {
    return new Material[]{
      Material.SNOWBALL, Material.EGG, Utils.getMaterial("BLUE_EGG"), Utils.getMaterial("BROWN_EGG")
    };
  }

  public static Material[] getSwords() {
    return new Material[]{
      Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD,
      Material.GOLDEN_SWORD, Material.NETHERITE_SWORD
    };
  }

  public static Material[] getPickaxes() {
    return new Material[]{
      Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE,
      Material.GOLDEN_PICKAXE, Material.NETHERITE_PICKAXE
    };
  }

}
