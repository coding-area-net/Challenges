package net.codingarea.challenges.plugin.utils.item;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.commons.bukkit.utils.item.BannerPattern;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import net.codingarea.commons.common.config.Document;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ItemBuilder extends StandardItemBuilder {

  @Getter
  private final Locale locale;

  public ItemBuilder(@NotNull Locale locale, @NotNull Material material, @NotNull MessageKey nameAndLoreKey, @NotNull Object... args) {
    Preconditions.checkNotNull(locale, "Cannot create ItemBuilder from null Locale");
    super(material);
    this.locale = locale;
    resetToNameAndLore(nameAndLoreKey, args);
  }

  public ItemBuilder(@NotNull Locale locale, @NotNull ItemStack item, @NotNull MessageKey nameAndLoreKey, @NotNull Object... args) {
    Preconditions.checkNotNull(locale, "Cannot create ItemBuilder from null Locale");
    super(item);
    this.locale = locale;
    resetToNameAndLore(nameAndLoreKey, args);
  }

  public ItemBuilder(@NotNull Locale locale, @NotNull StandardItemBuilder item, @NotNull MessageKey nameAndLoreKey, @NotNull Object... args) {
    this(locale, item.build(), nameAndLoreKey, args);
  }

  public ItemBuilder(@NotNull Player playerLocale, @NotNull Material material, @NotNull MessageKey nameAndLoreKey, @NotNull Object... args) {
    this(Challenges.getInstance().getTranslationManager().getLanguageProvider().getPlayerLanguage(playerLocale), material, nameAndLoreKey, args);
  }

  public ItemBuilder(@NotNull Locale locale, @NotNull ItemStack item) {
    Preconditions.checkNotNull(locale, "Cannot create ItemBuilder from null Locale");
    super(item);
    this.locale = locale;
  }

  protected void resetToNameAndLore(@NotNull MessageKey nameAndLoreKey, @NotNull Object... args) {
    nameAndLoreKey.applyAsItemNameAndLore(locale, getItemMeta(), args);
    hideAttributes();
  }

  @NotNull
  public ItemBuilder appendNameWithSpace(@NotNull MessageKey key, @NotNull Object... args) {
    key.appendToItemName(locale, getItemMeta(), true, args);
    return this;
  }

  @NotNull
  public ItemBuilder appendName(@NotNull MessageKey key, @NotNull Object... args) {
    key.appendToItemName(locale, getItemMeta(), false, args);
    return this;
  }

  @NotNull
  public ItemBuilder appendLore(@NotNull MessageKey key, @NotNull Object... args) {
    key.appendToItemLore(locale, getItemMeta(), args);
    return this;
  }

  @NotNull
  public ItemBuilder appendLore(@NotNull LocalizableMessage localizable) {
    return this.appendLore(localizable.getLocalizableKey(), localizable.getLocalizableArgs());
  }

  @NotNull
  public ItemBuilder appendLoreWithEmptyLine(@NotNull MessageKey key, @NotNull Object... args) {
    super.appendLore(" ");
    return this.appendLore(key, args);
  }

  @NotNull
  public ItemBuilder appendLoreWithEmptyLine(@NotNull LocalizableMessage localizable) {
    return this.appendLoreWithEmptyLine(localizable.getLocalizableKey(), localizable.getLocalizableArgs());
  }


  // Overrides

  @NotNull
  @Override
  public ItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
    return (ItemBuilder) super.addEnchantment(enchantment, level);
  }

  @NotNull
  @Override
  public ItemBuilder addFlag(@NonNull @NotNull ItemFlag... flags) {
    return (ItemBuilder) super.addFlag(flags);
  }

  @NotNull
  @Override
  public ItemBuilder removeFlag(@NonNull @NotNull ItemFlag... flags) {
    return (ItemBuilder) super.removeFlag(flags);
  }

  @NotNull
  @Override
  public ItemBuilder hideAttributes() {
    return (ItemBuilder) super.hideAttributes();
  }

  @NotNull
  @Override
  public ItemBuilder showAttributes() {
    return (ItemBuilder) super.showAttributes();
  }

  @NotNull
  @Override
  public ItemBuilder setUnbreakable(boolean unbreakable) {
    return (ItemBuilder) super.setUnbreakable(unbreakable);
  }

  @NotNull
  @Override
  public ItemBuilder setAmount(int amount) {
    return (ItemBuilder) super.setAmount(amount);
  }

  @NotNull
  @Override
  public ItemBuilder setDamage(int damage) {
    return (ItemBuilder) super.setDamage(damage);
  }

  @NotNull
  @Override
  public ItemBuilder setMaterial(@NotNull Material material) {
    return (ItemBuilder) super.setMaterial(material);
  }


  // Unstable

  @NotNull
  @Override
  @Deprecated
  public StandardItemBuilder setLore(@NotNull List<String> lore) {
    return super.setLore(lore);
  }

  @NotNull
  @Override
  @Deprecated
  public StandardItemBuilder setLore(@NonNull @NotNull String... lore) {
    return super.setLore(lore);
  }

  @NotNull
  @Override
  @Deprecated
  public StandardItemBuilder appendLore(@NonNull @NotNull String... lore) {
    return super.appendLore(lore);
  }

  @NotNull
  @Override
  @Deprecated
  public StandardItemBuilder appendLore(@NotNull Collection<String> lore) {
    return super.appendLore(lore);
  }

  @NotNull
  @Override
  @Deprecated
  public StandardItemBuilder setName(@Nullable String name) {
    return super.setName(name);
  }

  @NotNull
  @Override
  @Deprecated
  public StandardItemBuilder setName(@Nullable Object name) {
    return super.setName(name);
  }

  @NotNull
  @Override
  @Deprecated
  public StandardItemBuilder setName(@NonNull @NotNull String... content) {
    return super.setName(content);
  }

  @NotNull
  @Override
  @Deprecated
  public StandardItemBuilder appendName(@Nullable String sequence) {
    return super.appendName(sequence);
  }

  public static class BannerBuilder extends ItemBuilder {

    public BannerBuilder(@NotNull Locale locale, @NotNull Material material, @NotNull MessageKey nameAndLoreKey, @NonNull Object... args) {
      super(locale, material, nameAndLoreKey, args);
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

  public static class SkullBuilder extends ItemBuilder {

    public SkullBuilder(@NotNull Locale locale, @NotNull MessageKey nameAndLoreKey, @NonNull Object... args) {
      super(locale, Material.PLAYER_HEAD, nameAndLoreKey, args);
    }

    @NotNull
    public SkullBuilder setOwner(@NotNull OfflinePlayer owner) {
      getItemMeta().setOwningPlayer(owner);
      return this;
    }

    @NotNull
    public SkullBuilder setOwner(@NotNull UUID uuid, @NotNull String name) {
      PlayerProfile profile = Bukkit.createPlayerProfile(uuid, name); // TODO compatibility 1.17
      getItemMeta().setOwnerProfile(profile);
      return this;
    }

    @NotNull
    public SkullBuilder setTexture(@NotNull String textureUrl) {
      UUID uuid = UUID.nameUUIDFromBytes(textureUrl.getBytes());

      PlayerProfile profile = Bukkit.createPlayerProfile(uuid); // TODO does not exist 1.17.1
      PlayerTextures texture = profile.getTextures();

      try {
        texture.setSkin(new URL(textureUrl)); // URI.create(textureUrl).toURL()
      } catch (MalformedURLException e) {
        throw new IllegalArgumentException("Invalid texture url", e);
      }

      profile.setTextures(texture);
      getItemMeta().setOwnerProfile(profile);
      return this;
    }

    public SkullBuilder setBase64Texture(@NotNull String base64Texture) {
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

  public static class PotionBuilder extends ItemBuilder {

    public PotionBuilder(@NotNull Locale locale, @NotNull Material material, @NotNull MessageKey titleAndLoreKey, @NonNull Object... args) {
      super(locale, material, titleAndLoreKey, args);
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
    @Override
    public PotionMeta getItemMeta() {
      return getItemMetaAs();
    }

  }

  public static class LeatherArmorBuilder extends ItemBuilder {

    public LeatherArmorBuilder(@NotNull Locale locale, @NotNull Material material, @NotNull MessageKey titleAndLoreKey, @NotNull Object... args) {
      super(locale, material, titleAndLoreKey, args);
    }

    @NotNull
    public LeatherArmorBuilder setColor(@NotNull Color color) {
      getItemMeta().setColor(color);
      return this;
    }

    @NotNull
    @Override
    public LeatherArmorMeta getItemMeta() {
      return getItemMetaAs();
    }

  }
}
