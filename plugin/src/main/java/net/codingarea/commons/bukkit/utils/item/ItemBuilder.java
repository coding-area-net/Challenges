package net.codingarea.commons.bukkit.utils.item;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ItemBuilder {

  public static final ItemStack FILL_ITEM = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("§0").build(),
    FILL_ITEM_2 = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("§0").build(),
    BLOCKED_ITEM = new ItemBuilder(Material.BARRIER, "§cBlocked").build(),
    AIR = new ItemStack(Material.AIR);

  protected ItemStack item;
  protected ItemMeta meta;

  public ItemBuilder(@NotNull ItemStack item) {
    this(item, item.getItemMeta());
  }

  public ItemBuilder(@NotNull ItemStack item, @Nullable ItemMeta meta) {
    this.item = item;
    this.meta = meta;
  }

  public ItemBuilder(@NotNull Material material) {
    this(new ItemStack(material));
  }

  public ItemBuilder(@NotNull Material material, @NotNull String name) {
    this(material);
    setName(name);
  }

  public ItemBuilder(@NotNull Material material, @NotNull String name, @NotNull String... lore) {
    this(material);
    setName(name);
    setLore(lore);
  }

  public ItemBuilder(@NotNull Material material, @NotNull String name, int amount) {
    this(material);
    setName(name);
    setAmount(amount);
  }

  @NotNull
  public ItemMeta getMeta() {
    return getCastedMeta();
  }

  @NotNull
  @SuppressWarnings("unchecked")
  public final <M> M getCastedMeta() {
    return (M) (meta == null ? meta = item.getItemMeta() : meta);
  }

  @NotNull
  public ItemBuilder setLore(@NotNull List<String> lore) {
    getMeta().setLore(lore);
    return this;
  }

  @NotNull
  public ItemBuilder setLore(@NotNull String... lore) {
    return setLore(Arrays.asList(lore));
  }

  @NotNull
  public ItemBuilder appendLore(@NotNull String... lore) {
    return appendLore(Arrays.asList(lore));
  }

  @NotNull
  public ItemBuilder appendLore(@NotNull Collection<String> lore) {
    List<String> newLore = getMeta().getLore();
    if (newLore == null) newLore = new ArrayList<>();
    newLore.addAll(lore);
    setLore(newLore);
    return this;
  }

  @NotNull
  public ItemBuilder lore(@NotNull String... lore) {
    return setLore(lore);
  }

  @NotNull
  public ItemBuilder setName(@Nullable String name) {
    getMeta().setDisplayName(name);
    return this;
  }

  @NotNull
  public ItemBuilder setName(@Nullable Object name) {
    return setName(name == null ? null : name.toString());
  }

  @NotNull
  public ItemBuilder setName(@NotNull String... content) {
    if (content.length > 0) setName(content[0]);
    if (content.length > 1) setLore(Arrays.copyOfRange(content, 1, content.length));
    return this;
  }

  @NotNull
  public ItemBuilder appendName(@Nullable Object sequence) {
    String name = getMeta().getDisplayName();
    return setName(name + sequence);
  }

  @NotNull
  public ItemBuilder name(@Nullable Object name) {
    return setName(name);
  }

  @NotNull
  public ItemBuilder name(@NotNull String... content) {
    return setName(content);
  }

  @NotNull
  public ItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
    getMeta().addEnchant(enchantment, level, true);
    return this;
  }

  @NotNull
  public ItemBuilder enchant(@NotNull Enchantment enchantment, int level) {
    return addEnchantment(enchantment, level);
  }

  @NotNull
  public ItemBuilder addFlag(@NotNull ItemFlag... flags) {
    getMeta().addItemFlags(flags);
    return this;
  }

  @NotNull
  public ItemBuilder flag(@NotNull ItemFlag... flags) {
    return addFlag(flags);
  }

  @NotNull
  public ItemBuilder removeFlag(@NotNull ItemFlag... flags) {
    getMeta().removeItemFlags(flags);
    return this;
  }

  @NotNull
  public ItemBuilder hideAttributes() {
    return addFlag(ItemFlag.values());
  }

  @NotNull
  public ItemBuilder showAttributes() {
    return removeFlag(ItemFlag.values());
  }

  @NotNull
  public ItemBuilder setUnbreakable(boolean unbreakable) {
    getMeta().setUnbreakable(unbreakable);
    return this;
  }

  @NotNull
  public ItemBuilder unbreakable() {
    return setUnbreakable(true);
  }

  @NotNull
  public ItemBuilder breakable() {
    return setUnbreakable(false);
  }

  @NotNull
  public ItemBuilder setAmount(int amount) {
    item.setAmount(Math.min(Math.max(amount, 0), 64));
    return this;
  }

  @NotNull
  public ItemBuilder amount(int amount) {
    return setAmount(amount);
  }

  @NotNull
  public ItemBuilder setDamage(int damage) {
    this.<Damageable>getCastedMeta().setDamage(damage);
    return this;
  }

  @NotNull
  public ItemBuilder damage(int damage) {
    return setDamage(damage);
  }

  @NotNull
  public ItemBuilder setType(@NotNull Material material) {
    item.setType(material);
    meta = item.getItemMeta();
    return this;
  }

  @NotNull
  public String getName() {
    return getMeta().getDisplayName();
  }

  @NotNull
  public List<String> getLore() {
    List<String> lore = getMeta().getLore();
    return lore == null ? new ArrayList<>() : lore;
  }

  @NotNull
  public Material getType() {
    return item.getType();
  }

  public int getAmount() {
    return item.getAmount();
  }

  public int getDamage() {
    return this.<Damageable>getCastedMeta().getDamage();
  }

  @NotNull
  public ItemStack build() {
    item.setItemMeta(getMeta()); // Call to getter to prevent null value
    return item;
  }

  @NotNull
  public ItemStack toItem() {
    return build();
  }

  @Override
  public ItemBuilder clone() {
    return new ItemBuilder(item.clone(), getMeta().clone());
  }

  public static class BannerBuilder extends ItemBuilder {

    public BannerBuilder(@NotNull Material material) {
      super(material);
    }

    public BannerBuilder(@NotNull Material material, @NotNull String name) {
      super(material, name);
    }

    public BannerBuilder(@NotNull Material material, @NotNull String name, @NotNull String... lore) {
      super(material, name, lore);
    }

    public BannerBuilder(@NotNull Material material, @NotNull String name, int amount) {
      super(material, name, amount);
    }

    public BannerBuilder(@NotNull ItemStack item) {
      super(item);
    }

    @NotNull
    public BannerBuilder addPattern(@NotNull BannerPattern pattern, @NotNull DyeColor color) {
      return addPattern(pattern.getPatternType(), color);
    }

    @NotNull
    public BannerBuilder addPattern(@NotNull PatternType pattern, @NotNull DyeColor color) {
      getMeta().addPattern(new Pattern(color, pattern));
      return this;
    }

    @NotNull
    @Override
    public BannerMeta getMeta() {
      return getCastedMeta();
    }

  }

  public static class SkullBuilder extends ItemBuilder {

    public SkullBuilder() {
      super(Material.PLAYER_HEAD);
    }

    public SkullBuilder(@NotNull String owner) {
      super(Material.PLAYER_HEAD);
      setOwner(owner);
    }

    public SkullBuilder(@NotNull String owner, @NotNull String name, @NotNull String... lore) {
      super(Material.PLAYER_HEAD, name, lore);
      setOwner(owner);
    }

    public SkullBuilder setOwner(@NotNull String owner) {
      getMeta().setOwner(owner);
      return this;
    }

    @NotNull
    @Override
    public SkullMeta getMeta() {
      return getCastedMeta();
    }

  }

  public static class PotionBuilder extends ItemBuilder {

    @NotNull
    public static ItemBuilder createWaterBottle() {
      return new PotionBuilder(Material.POTION).setColor(Color.BLUE).hideAttributes();
    }

    public PotionBuilder(@NotNull Material material) {
      super(material);
    }

    public PotionBuilder(@NotNull Material material, @NotNull String name) {
      super(material, name);
    }

    public PotionBuilder(@NotNull Material material, @NotNull String name, @NotNull String... lore) {
      super(material, name, lore);
    }

    public PotionBuilder(@NotNull Material material, @NotNull String name, int amount) {
      super(material, name, amount);
    }

    public PotionBuilder(@NotNull ItemStack item) {
      super(item);
    }

    @NotNull
    public PotionBuilder addEffect(@NotNull PotionEffect effect) {
      getMeta().addCustomEffect(effect, true);
      return this;
    }

    @NotNull
    public PotionBuilder setColor(@NotNull Color color) {
      getMeta().setColor(color);
      return this;
    }

    @NotNull
    public PotionBuilder color(@NotNull Color color) {
      return setColor(color);
    }

    @NotNull
    @Override
    public PotionMeta getMeta() {
      return getCastedMeta();
    }

  }

  public static class LeatherArmorBuilder extends ItemBuilder {

    public LeatherArmorBuilder(@NotNull Material material) {
      super(material);
    }

    public LeatherArmorBuilder(@NotNull Material material, @NotNull String name) {
      super(material, name);
    }

    public LeatherArmorBuilder(@NotNull Material material, @NotNull String name, @NotNull String... lore) {
      super(material, name, lore);
    }

    public LeatherArmorBuilder(@NotNull Material material, @NotNull String name, int amount) {
      super(material, name, amount);
    }

    public LeatherArmorBuilder(@NotNull ItemStack item) {
      super(item);
    }

    @NotNull
    public LeatherArmorBuilder setColor(@NotNull Color color) {
      getMeta().setColor(color);
      return this;
    }

    @NotNull
    public LeatherArmorBuilder color(@NotNull Color color) {
      return setColor(color);
    }

    @NotNull
    @Override
    public LeatherArmorMeta getMeta() {
      return getCastedMeta();
    }

  }

}
