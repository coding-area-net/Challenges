package net.codingarea.challenges.plugin.utils.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.base.Preconditions;
import lombok.Getter;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.commons.bukkit.utils.item.BannerPattern;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
    Locale locale = Challenges.getInstance().getTranslationManager().getLanguageProvider().getPlayerLanguage(playerLocale);
    this(locale, material, nameAndLoreKey, args);
  }

  public ItemBuilder(@NotNull Locale locale, @NotNull ItemStack item) {
    Preconditions.checkNotNull(locale, "Cannot create ItemBuilder from null Locale");
    super(item);
    this.locale = locale;
  }

  protected void resetToNameAndLore(@NotNull MessageKey nameAndLoreKey, @NotNull Object... args) {
    hideAttributes();

    ItemMeta meta = getItemMeta();
    List<Component> components = nameAndLoreKey.asComponents(locale, args);
    if (components.isEmpty()) return;
    meta.displayName(components.getFirst());

    if (components.size() == 1) return;
    meta.lore(components.subList(1, components.size()));
  }

  @NotNull
  public ItemBuilder setName(@NotNull MessageKey key, @NotNull Object... args) {
    getItemMeta().displayName(key.asComponent(locale, args));
    return this;
  }

  @NotNull
  public ItemBuilder appendNameWithSpace(@NotNull MessageKey key, @NotNull Object... args) {
    ItemMeta meta = getItemMeta();
    Component existingName = meta.displayName();
    if (existingName == null) return setName(key, args);

    meta.displayName(existingName.append(Component.space()).append(key.asComponent(locale, args)));
    return this;
  }

  @NotNull
  public ItemBuilder appendName(@NotNull MessageKey key, @NotNull Object... args) {
    ItemMeta meta = getItemMeta();
    Component existingName = meta.displayName();
    if (existingName == null) return setName(key, args);

    meta.displayName(existingName.append(key.asComponent(locale, args)));
    return this;
  }

  @NotNull
  public ItemBuilder setLore(@NotNull MessageKey key, @NotNull Object... args) {
    getItemMeta().lore(key.asComponents(locale, args));
    return this;
  }

  @NotNull
  public ItemBuilder appendLore(@NotNull MessageKey key, @NotNull Object... args) {
    addToLore(key.asComponents(locale, args));
    return this;
  }

  @NotNull
  public ItemBuilder appendLore(@NotNull LocalizableMessage localizable) {
    return this.appendLore(localizable.getLocalizableKey(), localizable.getLocalizableArgs());
  }

  @NotNull
  public ItemBuilder appendLoreList(@NotNull Collection<? extends LocalizableMessage> lines) {
    for (LocalizableMessage line : lines) {
      appendLore(line);
    }
    return this;
  }

  @NotNull
  public ItemBuilder appendBlankLoreLine() {
    addToLore(List.of(Component.space()));
    return this;
  }

  protected void addToLore(@NotNull Collection<Component> components) {
    ItemMeta meta = getItemMeta();
    List<Component> existingLore = meta.lore();
    if (existingLore != null) {
      existingLore.addAll(components);
      meta.lore(existingLore);
    } else {
      meta.lore(List.copyOf(components));
    }
  }


  // Overrides

  @NotNull
  @Override
  public ItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
    return (ItemBuilder) super.addEnchantment(enchantment, level);
  }

  @NotNull
  @Override
  public ItemBuilder addFlag(@NotNull ItemFlag... flags) {
    return (ItemBuilder) super.addFlag(flags);
  }

  @NotNull
  @Override
  public ItemBuilder removeFlag(@NotNull ItemFlag... flags) {
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
  public StandardItemBuilder setLore(@NotNull String... lore) {
    return super.setLore(lore);
  }

  @NotNull
  @Override
  @Deprecated
  public StandardItemBuilder appendLore(@NotNull String... lore) {
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
  public StandardItemBuilder setName(@NotNull String... content) {
    return super.setName(content);
  }

  @NotNull
  @Override
  @Deprecated
  public StandardItemBuilder appendName(@Nullable String sequence) {
    return super.appendName(sequence);
  }

  public static class BannerBuilder extends ItemBuilder {

    public BannerBuilder(@NotNull Locale locale, @NotNull Material material, @NotNull MessageKey nameAndLoreKey, @NotNull Object... args) {
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

    public SkullBuilder(@NotNull Locale locale, @NotNull MessageKey nameAndLoreKey, @NotNull Object... args) {
      super(locale, Material.PLAYER_HEAD, nameAndLoreKey, args);
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
      StandardItemBuilder.SkullBuilder.setTexture(getItemMeta(), textureUrl);
      return this;
    }

    public SkullBuilder setBase64Texture(@NotNull String base64Texture) {
      StandardItemBuilder.SkullBuilder.setBase64Texture(getItemMeta(), base64Texture);
      return this;
    }

    @NotNull
    @Override
    public SkullMeta getItemMeta() {
      return getItemMetaAs();
    }

  }

  public static class PotionBuilder extends ItemBuilder {

    public PotionBuilder(@NotNull Locale locale, @NotNull Material material, @NotNull MessageKey titleAndLoreKey, @NotNull Object... args) {
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
