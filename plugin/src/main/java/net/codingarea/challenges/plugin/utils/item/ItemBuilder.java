package net.codingarea.challenges.plugin.utils.item;

import com.google.gson.JsonParser;
import net.codingarea.challenges.plugin.content.ItemDescription;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.commons.bukkit.utils.item.BannerPattern;
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

public class ItemBuilder extends net.codingarea.commons.bukkit.utils.item.ItemBuilder {

  public static final ItemStack BLOCKED_ITEM = new ItemBuilder(Material.BARRIER, "§cBlocked").build();

  protected ItemDescription builtByItemDescription;

  public ItemBuilder(@NotNull ItemStack item) {
    super(item);
  }

  public ItemBuilder(@NotNull ItemStack item, @Nullable ItemMeta meta) {
    super(item, meta);
  }

  public ItemBuilder() {
    this(Material.BARRIER, ItemDescription.empty());
  }

  public ItemBuilder(@NotNull Material material) {
    super(material);
  }

  public ItemBuilder(@NotNull Material material, @NotNull Message message) {
    this(material, message.asItemDescription());
  }

  public ItemBuilder(@NotNull Material material, @NotNull Message message, Object... args) {
    this(material, message.asItemDescription(args));
  }

  public ItemBuilder(@NotNull Material material, @NotNull ItemDescription description) {
    this(material);
    applyFormat(description);
  }

  public ItemBuilder(@NotNull Material material, @NotNull String name) {
    super(material, name);
  }

  public ItemBuilder(@NotNull Material material, @NotNull String name, @NotNull String... lore) {
    super(material, name, lore);
  }

  public ItemBuilder(@NotNull Material material, @NotNull String name, int amount) {
    super(material, name, amount);
  }

  @NotNull
  public ItemBuilder setLore(@NotNull Message message) {
    return setLore(message.asArray());
  }

  @NotNull
  public ItemBuilder setLore(@NotNull List<String> lore) {
    return (ItemBuilder) super.setLore(lore);
  }

  @NotNull
  public ItemBuilder setLore(@NotNull String... lore) {
    return (ItemBuilder) super.setLore(lore);
  }

  @NotNull
  public ItemBuilder appendLore(@NotNull String... lore) {
    return (ItemBuilder) super.appendLore(lore);
  }

  @NotNull
  public ItemBuilder appendLore(@NotNull Collection<String> lore) {
    return (ItemBuilder) super.appendLore(lore);
  }

  @NotNull
  public ItemBuilder setName(@Nullable String name) {
    return (ItemBuilder) super.setName(name);
  }

  @NotNull
  public ItemBuilder setName(@Nullable Object name) {
    return (ItemBuilder) super.setName(name);
  }

  @NotNull
  public ItemBuilder setName(@NotNull String... content) {
    return (ItemBuilder) super.setName(content);
  }

  @NotNull
  public ItemBuilder appendName(@Nullable Object sequence) {
    return (ItemBuilder) super.appendName(sequence);
  }

  @NotNull
  public ItemBuilder name(@Nullable Object name) {
    return (ItemBuilder) super.name(name);
  }

  @NotNull
  public ItemBuilder name(@NotNull String... content) {
    return (ItemBuilder) super.name(content);
  }

  @NotNull
  public ItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
    return (ItemBuilder) super.addEnchantment(enchantment, level);
  }

  @NotNull
  public ItemBuilder enchant(@NotNull Enchantment enchantment, int level) {
    return (ItemBuilder) super.enchant(enchantment, level);
  }

  @NotNull
  public ItemBuilder addFlag(@NotNull ItemFlag... flags) {
    return (ItemBuilder) super.addFlag(flags);
  }

  @NotNull
  public ItemBuilder removeFlag(@NotNull ItemFlag... flags) {
    return (ItemBuilder) super.removeFlag(flags);
  }

  @NotNull
  public ItemBuilder hideAttributes() {
    return (ItemBuilder) super.hideAttributes();
  }

  @NotNull
  public ItemBuilder showAttributes() {
    return (ItemBuilder) super.showAttributes();
  }

  @NotNull
  public ItemBuilder setUnbreakable(boolean unbreakable) {
    return (ItemBuilder) super.setUnbreakable(unbreakable);
  }

  @NotNull
  public ItemBuilder unbreakable() {
    return (ItemBuilder) super.unbreakable();
  }

  @NotNull
  public ItemBuilder breakable() {
    return (ItemBuilder) super.breakable();
  }

  @NotNull
  public ItemBuilder setAmount(int amount) {
    return (ItemBuilder) super.setAmount(amount);
  }

  @NotNull
  public ItemBuilder amount(int amount) {
    return (ItemBuilder) super.amount(amount);
  }

  @NotNull
  public ItemBuilder setDamage(int damage) {
    return (ItemBuilder) super.setDamage(damage);
  }

  @NotNull
  public ItemBuilder damage(int damage) {
    return (ItemBuilder) super.damage(damage);
  }

  @NotNull
  public ItemBuilder setType(@NotNull Material material) {
    return (ItemBuilder) super.setType(material);
  }

  @NotNull
  public ItemBuilder applyFormat(@NotNull ItemDescription description) {
    builtByItemDescription = description;
    setName(description.getName());
    setLore(description.getLore());
    return this;
  }

  @Nullable
  public ItemDescription getBuiltByItemDescription() {
    return builtByItemDescription;
  }

  @Override
  public ItemBuilder clone() {
    ItemBuilder builder = new ItemBuilder(item.clone(), getMeta().clone());
    builder.builtByItemDescription = builtByItemDescription;
    return builder;
  }

  public static class BannerBuilder extends ItemBuilder {

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
    public ItemBuilder.BannerBuilder addPattern(@NotNull BannerPattern pattern, @NotNull DyeColor color) {
      return addPattern(pattern.getPatternType(), color);
    }

    @NotNull
    public ItemBuilder.BannerBuilder addPattern(@NotNull PatternType pattern, @NotNull DyeColor color) {
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

    public SkullBuilder(Message message) {
      super(Material.PLAYER_HEAD, message);
    }

    public SkullBuilder(String name, String... lore) {
      super(Material.PLAYER_HEAD, name, lore);
    }

    @NotNull
    public ItemBuilder.SkullBuilder setOwner(@NotNull OfflinePlayer owner) {
      getMeta().setOwningPlayer(owner);
      return this;
    }

    @NotNull
    public ItemBuilder.SkullBuilder setOwner(@NotNull UUID uuid, @NotNull String name) {
      PlayerProfile profile = Bukkit.createPlayerProfile(uuid, name);
      getMeta().setOwnerProfile(profile);
      return this;
    }

    public ItemBuilder.SkullBuilder setTexture(@NotNull String textureUrl) {
      UUID uuid = UUID.nameUUIDFromBytes(textureUrl.getBytes());

      PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
      PlayerTextures texture = profile.getTextures();

      try {
        texture.setSkin(new URL(textureUrl));
      } catch (MalformedURLException e) {
        throw new IllegalArgumentException("Invalid texture url", e);
      }

      profile.setTextures(texture);
      getMeta().setOwnerProfile(profile);
      return this;
    }

    public ItemBuilder.SkullBuilder setBase64Texture(@NotNull String base64Texture) {
      String textureUrlJson = new String(Base64.getDecoder().decode(base64Texture),
        StandardCharsets.UTF_8);

      String textureUrl = JsonParser.parseString(textureUrlJson) // TODO fix(deps) version ambiguity
        .getAsJsonObject()
        .get("textures").getAsJsonObject()
        .get("SKIN").getAsJsonObject()
        .get("url").getAsString();

      return setTexture(textureUrl);
    }

    @NotNull
    @Override
    public SkullMeta getMeta() {
      return getCastedMeta();
    }

  }

  public static class PotionBuilder extends ItemBuilder {

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
    public static ItemBuilder createWaterBottle() {
      return new ItemBuilder.PotionBuilder(Material.POTION).setColor(Color.BLUE).hideAttributes();
    }

    @NotNull
    public ItemBuilder.PotionBuilder addEffect(@NotNull PotionEffect effect) {
      getMeta().addCustomEffect(effect, true);
      return this;
    }

    @NotNull
    public ItemBuilder.PotionBuilder setColor(@NotNull Color color) {
      getMeta().setColor(color);
      return this;
    }

    @NotNull
    public ItemBuilder.PotionBuilder color(@NotNull Color color) {
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
    public ItemBuilder.LeatherArmorBuilder setColor(@NotNull Color color) {
      getMeta().setColor(color);
      return this;
    }

    @NotNull
    public ItemBuilder.LeatherArmorBuilder color(@NotNull Color color) {
      return setColor(color);
    }

    @NotNull
    @Override
    public LeatherArmorMeta getMeta() {
      return getCastedMeta();
    }

  }

}
