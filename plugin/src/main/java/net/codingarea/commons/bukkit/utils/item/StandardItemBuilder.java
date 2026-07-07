package net.codingarea.commons.bukkit.utils.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.commons.bukkit.core.BukkitModule;
import net.codingarea.commons.common.config.Document;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StandardItemBuilder {

  public static final ItemStack
    FILL_ITEM = new StandardItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("§0").build(),
    FILL_ITEM_CONTRAST = new StandardItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("§0").build(),
    AIR = new ItemStack(Material.AIR);

  protected ItemStack item;
  protected ItemMeta meta;

  public StandardItemBuilder(@NotNull ItemStack item) {
    this(item, item.getItemMeta());
  }

  public StandardItemBuilder(@NotNull ItemStack item, @Nullable ItemMeta meta) {
    this.item = item;
    this.meta = meta;
  }

  public StandardItemBuilder(@NotNull Material material) {
    this(new ItemStack(material));
  }

  public StandardItemBuilder(@NotNull Material material, @NotNull String name) {
    this(material);
    setName(name);
  }

  public StandardItemBuilder(@NotNull Material material, @NotNull String name, @NotNull String... lore) {
    this(material);
    setName(name);
    setLore(lore);
  }

  public StandardItemBuilder(@NotNull Material material, @NotNull String name, int amount) {
    this(material);
    setName(name);
    setAmount(amount);
  }

  @NotNull
  public ItemMeta getItemMeta() {
    return getItemMetaAs();
  }

  @NotNull
  @SuppressWarnings("unchecked")
  public final <M> M getItemMetaAs() {
    return (M) (meta == null ? meta = item.getItemMeta() : meta);
  }

  @NotNull
  public StandardItemBuilder setLore(@NotNull List<String> lore) {
    getItemMeta().setLore(lore);
    return this;
  }

  @NotNull
  public StandardItemBuilder setLore(@NotNull String... lore) {
    return setLore(Arrays.asList(lore));
  }

  @NotNull
  public StandardItemBuilder appendLore(@NotNull String... lore) {
    return appendLore(Arrays.asList(lore));
  }

  @NotNull
  public StandardItemBuilder appendLore(@NotNull Collection<String> lore) {
    List<String> newLore = getItemMeta().getLore();
    if (newLore == null) newLore = new ArrayList<>();
    newLore.addAll(lore);
    setLore(newLore);
    return this;
  }

  @NotNull
  public StandardItemBuilder setName(@Nullable String name) {
    getItemMeta().setDisplayName(name);
    return this;
  }

  @NotNull
  public StandardItemBuilder setName(@Nullable Object name) {
    return setName(name == null ? null : name.toString());
  }

  @NotNull
  public StandardItemBuilder setName(@NotNull String... content) {
    if (content.length > 0) setName(content[0]);
    if (content.length > 1) setLore(Arrays.copyOfRange(content, 1, content.length));
    return this;
  }

  @NotNull
  public StandardItemBuilder appendName(@Nullable String sequence) {
    String name = getItemMeta().getDisplayName();
    return setName(name + sequence);
  }

  @NotNull
  public StandardItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
    getItemMeta().addEnchant(enchantment, level, true);
    return this;
  }

  @NotNull
  public StandardItemBuilder addFlag(@NotNull ItemFlag... flags) {
    getItemMeta().addItemFlags(flags);
    return this;
  }

  @NotNull
  public StandardItemBuilder removeFlag(@NotNull ItemFlag... flags) {
    getItemMeta().removeItemFlags(flags);
    return this;
  }

  @NotNull
  public StandardItemBuilder hideAttributes() {
    applyDummyAttributeModifier();
    return addFlag(ItemFlag.values());
  }

  @SuppressWarnings({"UnstableApiUsage"})
  protected void applyDummyAttributeModifier() {
    try {
      // hacky fix to make paper hide damage attributes (only works with custom modifiers), "Vanilla behavior since 1.20.5"
      // see https://github.com/PaperMC/Paper/issues/11224
      Attribute dummyAttribute = MinecraftNameWrapper.LUCK;
      Collection<AttributeModifier> modifiers = getItemMeta().getAttributeModifiers(dummyAttribute);

      if (modifiers == null || modifiers.isEmpty()) {
        meta.addAttributeModifier(dummyAttribute, new AttributeModifier(
          new NamespacedKey(BukkitModule.getFirstInstance(), "dummy"),
          0,
          AttributeModifier.Operation.ADD_NUMBER,
          org.bukkit.inventory.EquipmentSlotGroup.ANY
        ));
      }
    } catch (Throwable ignored) { // defend against experimental api changes
    }
  }

  @NotNull
  public StandardItemBuilder showAttributes() {
    return removeFlag(ItemFlag.values());
  }

  @NotNull
  public StandardItemBuilder setUnbreakable(boolean unbreakable) {
    getItemMeta().setUnbreakable(unbreakable);
    return this;
  }

  @NotNull
  public StandardItemBuilder setAmount(int amount) {
    item.setAmount(Math.clamp(amount, 0, 64));
    return this;
  }

  @NotNull
  public StandardItemBuilder setDamage(int damage) {
    this.<Damageable>getItemMetaAs().setDamage(damage);
    return this;
  }

  @NotNull
  public StandardItemBuilder setMaterial(@NotNull Material material) {
    item.setType(material);
    meta = item.getItemMeta();
    return this;
  }

  @NotNull
  public String getName() {
    return getItemMeta().getDisplayName();
  }

  @NotNull
  public List<String> getLore() {
    List<String> lore = getItemMeta().getLore();
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
    return this.<Damageable>getItemMetaAs().getDamage();
  }

  @NotNull
  public ItemStack build() {
    item.setItemMeta(getItemMeta()); // Call to getter to prevent null value
    return item;
  }

  @NotNull
  public ItemStack toItem() {
    return build();
  }

  @Override
  public StandardItemBuilder clone() {
    return new StandardItemBuilder(item.clone(), getItemMeta().clone());
  }

  public static class BannerBuilder extends StandardItemBuilder {

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
      getItemMeta().addPattern(new Pattern(color, pattern));
      return this;
    }

    @NotNull
    @Override
    public BannerMeta getItemMeta() {
      return getItemMetaAs();
    }

  }

  public static class SkullBuilder extends StandardItemBuilder {

    public static void setBase64Texture(@NotNull SkullMeta item, @NotNull String base64Texture) {
      String textureUrlJson = new String(Base64.getDecoder().decode(base64Texture), StandardCharsets.UTF_8);
      String textureUrl = Document.parseJson(textureUrlJson)
        .getString("textures.SKIN.url");
      if (textureUrl == null) return; // TODO
      setTexture(item, textureUrl);
    }

    public static void setTexture(@NotNull SkullMeta meta, @NotNull String textureUrl) {
      UUID uuid = UUID.nameUUIDFromBytes(textureUrl.getBytes());

      PlayerProfile profile = Bukkit.createProfile(uuid);
      PlayerTextures texture = profile.getTextures();

      try {
        texture.setSkin(URI.create(textureUrl).toURL());
      } catch (MalformedURLException e) {
        throw new IllegalArgumentException("Invalid texture url", e);
      }

      profile.setTextures(texture);
      meta.setPlayerProfile(profile);
    }

    public SkullBuilder() {
      super(Material.PLAYER_HEAD);
    }

    @NotNull
    public SkullBuilder setOwner(@NotNull OfflinePlayer owner) {
      getItemMeta().setOwningPlayer(owner);
      return this;
    }

    @NotNull
    public SkullBuilder setOwner(@NotNull UUID uuid, @NotNull String name) {
      PlayerProfile profile = Bukkit.createProfile(uuid, name);
      getItemMeta().setPlayerProfile(profile);
      return this;
    }

    @NotNull
    public SkullBuilder setTexture(@NotNull String textureUrl) {
      setTexture(getItemMeta(), textureUrl);
      return this;
    }

    public SkullBuilder setBase64Texture(@NotNull String base64Texture) {
      setBase64Texture(getItemMeta(), base64Texture);
      return this;
    }

    @NotNull
    @Override
    public SkullMeta getItemMeta() {
      return getItemMetaAs();
    }

  }

  public static class PotionBuilder extends StandardItemBuilder {

    @NotNull
    public static StandardItemBuilder createWaterBottle() {
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
      getItemMeta().addCustomEffect(effect, true);
      return this;
    }

    @NotNull
    public PotionBuilder setColor(@NotNull Color color) {
      getItemMeta().setColor(color);
      return this;
    }

    @NotNull
    public PotionBuilder color(@NotNull Color color) {
      return setColor(color);
    }

    @NotNull
    @Override
    public PotionMeta getItemMeta() {
      return getItemMetaAs();
    }

  }

  public static class LeatherArmorBuilder extends StandardItemBuilder {

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
      getItemMeta().setColor(color);
      return this;
    }

    @NotNull
    public LeatherArmorBuilder color(@NotNull Color color) {
      return setColor(color);
    }

    @NotNull
    @Override
    public LeatherArmorMeta getItemMeta() {
      return getItemMetaAs();
    }

  }

}
