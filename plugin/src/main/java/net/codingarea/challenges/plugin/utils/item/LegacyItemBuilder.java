package net.codingarea.challenges.plugin.utils.item;

import net.codingarea.challenges.plugin.content.legacy.ItemDescription;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.commons.bukkit.utils.item.BannerPattern;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import net.codingarea.commons.common.config.Document;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Deprecated
public class LegacyItemBuilder extends StandardItemBuilder {

  public static final ItemStack BLOCKED_ITEM = new LegacyItemBuilder(Material.BARRIER, "§cBlocked").build();

  protected ItemDescription builtByItemDescription;

  public LegacyItemBuilder(@NotNull ItemStack item) {
    super(item);
  }

  public LegacyItemBuilder(@NotNull ItemStack item, @Nullable ItemMeta meta) {
    super(item, meta);
  }

  public LegacyItemBuilder() {
    this(Material.BARRIER, ItemDescription.empty());
  }

  public LegacyItemBuilder(@NotNull Material material) {
    super(material);
  }

  public LegacyItemBuilder(@NotNull Material material, @NotNull Message message) {
    this(material, message.asItemDescription());
  }

  public LegacyItemBuilder(@NotNull Material material, @NotNull Message message, Object... args) {
    this(material, message.asItemDescription(args));
  }

  public LegacyItemBuilder(@NotNull Material material, @NotNull ItemDescription description) {
    this(material);
    applyFormat(description);
  }

  public LegacyItemBuilder(@NotNull Material material, @NotNull String name) {
    super(material, name);
  }

  public LegacyItemBuilder(@NotNull Material material, @NotNull String name, @NotNull String... lore) {
    super(material, name, lore);
  }

  public LegacyItemBuilder(@NotNull Material material, @NotNull String name, int amount) {
    super(material, name, amount);
  }

  @NotNull
  public LegacyItemBuilder setLore(@NotNull Message message) {
    return setLore(message.asArray());
  }

  @NotNull
  public LegacyItemBuilder setLore(@NotNull List<String> lore) {
    return (LegacyItemBuilder) super.setLore(lore);
  }

  @NotNull
  public LegacyItemBuilder setLore(@NotNull String... lore) {
    return (LegacyItemBuilder) super.setLore(lore);
  }

  @NotNull
  public LegacyItemBuilder appendLore(@NotNull String... lore) {
    return (LegacyItemBuilder) super.appendLore(lore);
  }

  @NotNull
  public LegacyItemBuilder appendLore(@NotNull Collection<String> lore) {
    return (LegacyItemBuilder) super.appendLore(lore);
  }

  @NotNull
  public LegacyItemBuilder setName(@Nullable String name) {
    return (LegacyItemBuilder) super.setName(name);
  }

  @NotNull
  public LegacyItemBuilder setName(@Nullable Object name) {
    return (LegacyItemBuilder) super.setName(name);
  }

  @NotNull
  public LegacyItemBuilder setName(@NotNull String... content) {
    return (LegacyItemBuilder) super.setName(content);
  }

  @NotNull
  public LegacyItemBuilder appendName(@Nullable String sequence) {
    return (LegacyItemBuilder) super.appendName(sequence);
  }

  @NotNull
  public LegacyItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
    return (LegacyItemBuilder) super.addEnchantment(enchantment, level);
  }

  @NotNull
  public LegacyItemBuilder addFlag(@NotNull ItemFlag... flags) {
    return (LegacyItemBuilder) super.addFlag(flags);
  }

  @NotNull
  public LegacyItemBuilder removeFlag(@NotNull ItemFlag... flags) {
    return (LegacyItemBuilder) super.removeFlag(flags);
  }

  @NotNull
  public LegacyItemBuilder hideAttributes() {
    return (LegacyItemBuilder) super.hideAttributes();
  }

  @NotNull
  public LegacyItemBuilder showAttributes() {
    return (LegacyItemBuilder) super.showAttributes();
  }

  @NotNull
  public LegacyItemBuilder setUnbreakable(boolean unbreakable) {
    return (LegacyItemBuilder) super.setUnbreakable(unbreakable);
  }

  @NotNull
  public LegacyItemBuilder setAmount(int amount) {
    return (LegacyItemBuilder) super.setAmount(amount);
  }

  @NotNull
  public LegacyItemBuilder setDamage(int damage) {
    return (LegacyItemBuilder) super.setDamage(damage);
  }

  @NotNull
  public LegacyItemBuilder setMaterial(@NotNull Material material) {
    return (LegacyItemBuilder) super.setMaterial(material);
  }

  @NotNull
  public LegacyItemBuilder applyFormat(@NotNull ItemDescription description) {
    builtByItemDescription = description;
    hideAttributes();
    setName(description.getName());
    setLore(description.getLore());
    return this;
  }

  @Nullable
  public ItemDescription getBuiltByItemDescription() {
    return builtByItemDescription;
  }

  @Override
  public LegacyItemBuilder clone() {
    LegacyItemBuilder builder = new LegacyItemBuilder(item.clone(), getItemMeta().clone());
    builder.builtByItemDescription = builtByItemDescription;
    return builder;
  }

  public static class BannerBuilder extends LegacyItemBuilder {

    public BannerBuilder(@NotNull Material material) {
      super(material);
    }

    public BannerBuilder(@NotNull Material material, @NotNull Message message) {
      super(material, message);
    }

    public BannerBuilder(@NotNull Material material, @NotNull ItemDescription description) {
      super(material, description);
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
    public LegacyItemBuilder.BannerBuilder addPattern(@NotNull BannerPattern pattern, @NotNull DyeColor color) {
      return addPattern(pattern.getPatternType(), color);
    }

    @NotNull
    public LegacyItemBuilder.BannerBuilder addPattern(@NotNull PatternType pattern, @NotNull DyeColor color) {
      getItemMeta().addPattern(new Pattern(color, pattern));
      return this;
    }

    @NotNull
    @Override
    public BannerMeta getItemMeta() {
      return getItemMetaAs();
    }

  }

  public static class SkullBuilder extends LegacyItemBuilder {

    public SkullBuilder() {
      super(Material.PLAYER_HEAD);
    }

    public SkullBuilder(Message message) {
      super(Material.PLAYER_HEAD, message);
    }

    public SkullBuilder(String name, String... lore) {
      super(Material.PLAYER_HEAD, name, lore);
    }

    @NotNull
    public LegacyItemBuilder.SkullBuilder setOwner(@NotNull OfflinePlayer owner) {
      getItemMeta().setOwningPlayer(owner);
      return this;
    }

    @NotNull
    public LegacyItemBuilder.SkullBuilder setOwner(@NotNull UUID uuid, @NotNull String name) {
//      PlayerProfile profile = Bukkit.createPlayerProfile(uuid, name);
      PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
      getItemMeta().setOwnerProfile(profile);
      return this;
    }

    public LegacyItemBuilder.SkullBuilder setTexture(@NotNull String textureUrl) {
      UUID uuid = UUID.nameUUIDFromBytes(textureUrl.getBytes());

      if (true) return this; // TODO QUICKFIX 1.17.1

      PlayerProfile profile = Bukkit.createPlayerProfile(uuid); // does not exist 1.17.1
      PlayerTextures texture = profile.getTextures();

      try {
        texture.setSkin(new URL(textureUrl));
      } catch (MalformedURLException e) {
        throw new IllegalArgumentException("Invalid texture url", e);
      }

      profile.setTextures(texture);
      getItemMeta().setOwnerProfile(profile);
      return this;
    }

    public LegacyItemBuilder.SkullBuilder setBase64Texture(@NotNull String base64Texture) {
      String textureUrlJson = new String(Base64.getDecoder().decode(base64Texture), StandardCharsets.UTF_8);
      String textureUrl = Document.parseJson(textureUrlJson)
        .getString("textures.SKIN.url");
      if (textureUrl == null) return this; // TODO
      return setTexture(textureUrl);
    }

    @NotNull
    @Override
    public SkullMeta getItemMeta() {
      return getItemMetaAs();
    }

  }

  public static class PotionBuilder extends LegacyItemBuilder {

    public PotionBuilder(@NotNull Material material) {
      super(material);
    }

    public PotionBuilder(@NotNull Material material, @NotNull Message message) {
      super(material, message);
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
    public static LegacyItemBuilder createWaterBottle() {
      return new LegacyItemBuilder.PotionBuilder(Material.POTION).setColor(Color.BLUE).hideAttributes();
    }

    @NotNull
    public LegacyItemBuilder.PotionBuilder addEffect(@NotNull PotionEffect effect) {
      getItemMeta().addCustomEffect(effect, true);
      return this;
    }

    @NotNull
    public LegacyItemBuilder.PotionBuilder setColor(@NotNull Color color) {
      getItemMeta().setColor(color);
      return this;
    }

    @NotNull
    public LegacyItemBuilder.PotionBuilder color(@NotNull Color color) {
      return setColor(color);
    }

    @NotNull
    @Override
    public PotionMeta getItemMeta() {
      return getItemMetaAs();
    }

  }

  public static class LeatherArmorBuilder extends LegacyItemBuilder {

    public LeatherArmorBuilder(@NotNull Material material) {
      super(material);
    }

    public LeatherArmorBuilder(@NotNull Material material, @NotNull Message message) {
      super(material, message);
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
    public LegacyItemBuilder.LeatherArmorBuilder setColor(@NotNull Color color) {
      getItemMeta().setColor(color);
      return this;
    }

    @NotNull
    public LegacyItemBuilder.LeatherArmorBuilder color(@NotNull Color color) {
      return setColor(color);
    }

    @NotNull
    @Override
    public LeatherArmorMeta getItemMeta() {
      return getItemMetaAs();
    }

  }

}
