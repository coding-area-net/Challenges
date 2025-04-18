package net.codingarea.challenges.plugin.utils.item;

import com.google.gson.JsonParser;
import lombok.NonNull;
import net.anweisen.utilities.bukkit.utils.item.BannerPattern;
import net.anweisen.utilities.bukkit.utils.misc.GameProfileUtils;
import net.anweisen.utilities.common.annotations.DeprecatedSince;
import net.anweisen.utilities.common.annotations.ReplaceWith;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.ItemDescription;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.misc.DatabaseHelper;
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

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ItemBuilder extends net.anweisen.utilities.bukkit.utils.item.ItemBuilder {

	public static final ItemStack BLOCKED_ITEM = new ItemBuilder(Material.BARRIER, "§cBlocked").build();

	protected ItemDescription builtByItemDescription;

	public ItemBuilder(@Nonnull ItemStack item) {
		super(item);
	}

	public ItemBuilder(@Nonnull ItemStack item, @Nullable ItemMeta meta) {
		super(item, meta);
	}

	public ItemBuilder() {
		this(Material.BARRIER, ItemDescription.empty());
	}

	public ItemBuilder(@Nonnull Material material) {
		super(material);
	}

	public ItemBuilder(@Nonnull Material material, @Nonnull Message message) {
		this(material, message.asItemDescription());
	}

	public ItemBuilder(@Nonnull Material material, @Nonnull Message message, Object... args) {
		this(material, message.asItemDescription(args));
	}

	public ItemBuilder(@Nonnull Material material, @Nonnull ItemDescription description) {
		this(material);
		applyFormat(description);
	}

	public ItemBuilder(@Nonnull Material material, @Nonnull String name) {
		super(material, name);
	}

	public ItemBuilder(@Nonnull Material material, @Nonnull String name, @Nonnull String... lore) {
		super(material, name, lore);
	}

	public ItemBuilder(@Nonnull Material material, @Nonnull String name, int amount) {
		super(material, name, amount);
	}

	@Nonnull
	public ItemBuilder setLore(@Nonnull Message message) {
		return setLore(message.asArray());
	}

	@Nonnull
	public ItemBuilder setLore(@Nonnull List<String> lore) {
		return (ItemBuilder) super.setLore(lore);
	}

	@Nonnull
	public ItemBuilder setLore(@Nonnull String... lore) {
		return (ItemBuilder) super.setLore(lore);
	}

	@Nonnull
	public ItemBuilder appendLore(@Nonnull String... lore) {
		return (ItemBuilder) super.appendLore(lore);
	}

	@Nonnull
	public ItemBuilder appendLore(@Nonnull Collection<String> lore) {
		return (ItemBuilder) super.appendLore(lore);
	}

	@Nonnull
	public ItemBuilder setName(@Nullable String name) {
		return (ItemBuilder) super.setName(name);
	}

	@Nonnull
	public ItemBuilder setName(@Nullable Object name) {
		return (ItemBuilder) super.setName(name);
	}

	@Nonnull
	public ItemBuilder setName(@Nonnull String... content) {
		return (ItemBuilder) super.setName(content);
	}

	@Nonnull
	public ItemBuilder appendName(@Nullable Object sequence) {
		return (ItemBuilder) super.appendName(sequence);
	}

	@Nonnull
	public ItemBuilder name(@Nullable Object name) {
		return (ItemBuilder) super.name(name);
	}

	@Nonnull
	public ItemBuilder name(@Nonnull String... content) {
		return (ItemBuilder) super.name(content);
	}

	@Nonnull
	public ItemBuilder addEnchantment(@Nonnull Enchantment enchantment, int level) {
		return (ItemBuilder) super.addEnchantment(enchantment, level);
	}

	@Nonnull
	public ItemBuilder enchant(@Nonnull Enchantment enchantment, int level) {
		return (ItemBuilder) super.enchant(enchantment, level);
	}

	@Nonnull
	public ItemBuilder addFlag(@Nonnull ItemFlag... flags) {
		return (ItemBuilder) super.addFlag(flags);
	}

	@Nonnull
	public ItemBuilder removeFlag(@Nonnull ItemFlag... flags) {
		return (ItemBuilder) super.removeFlag(flags);
	}

	@Nonnull
	public ItemBuilder hideAttributes() {
		return (ItemBuilder) super.hideAttributes();
	}

	@Nonnull
	public ItemBuilder showAttributes() {
		return (ItemBuilder) super.showAttributes();
	}

	@Nonnull
	public ItemBuilder setUnbreakable(boolean unbreakable) {
		return (ItemBuilder) super.setUnbreakable(unbreakable);
	}

	@Nonnull
	public ItemBuilder unbreakable() {
		return (ItemBuilder) super.unbreakable();
	}

	@Nonnull
	public ItemBuilder breakable() {
		return (ItemBuilder) super.breakable();
	}

	@Nonnull
	public ItemBuilder setAmount(int amount) {
		return (ItemBuilder) super.setAmount(amount);
	}

	@Nonnull
	public ItemBuilder amount(int amount) {
		return (ItemBuilder) super.amount(amount);
	}

	@Nonnull
	public ItemBuilder setDamage(int damage) {
		return (ItemBuilder) super.setDamage(damage);
	}

	@Nonnull
	public ItemBuilder damage(int damage) {
		return (ItemBuilder) super.damage(damage);
	}

	@Nonnull
	public ItemBuilder setType(@Nonnull Material material) {
		return (ItemBuilder) super.setType(material);
	}

	@Nonnull
	public ItemBuilder applyFormat(@Nonnull ItemDescription description) {
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

		public BannerBuilder(@Nonnull Material material) {
			super(material);
		}

		public BannerBuilder(@Nonnull Material material, @Nonnull Message message) {
			super(material, message);
		}

		public BannerBuilder(@Nonnull Material material, @Nonnull ItemDescription description) {
			super(material, description);
		}

		public BannerBuilder(@Nonnull Material material, @Nonnull String name) {
			super(material, name);
		}

		public BannerBuilder(@Nonnull Material material, @Nonnull String name, @Nonnull String... lore) {
			super(material, name, lore);
		}

		public BannerBuilder(@Nonnull Material material, @Nonnull String name, int amount) {
			super(material, name, amount);
		}

		public BannerBuilder(@Nonnull ItemStack item) {
			super(item);
		}

		@Nonnull
		public ItemBuilder.BannerBuilder addPattern(@Nonnull BannerPattern pattern, @Nonnull DyeColor color) {
			return addPattern(pattern.getPatternType(), color);
		}

		@Nonnull
		public ItemBuilder.BannerBuilder addPattern(@Nonnull PatternType pattern, @Nonnull DyeColor color) {
			getMeta().addPattern(new Pattern(color, pattern));
			return this;
		}

		@Nonnull
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

		@NonNull
		public ItemBuilder.SkullBuilder setOwner(@NonNull OfflinePlayer owner) {
			getMeta().setOwningPlayer(owner);
			return this;
		}

		@NonNull
		public ItemBuilder.SkullBuilder setOwner(@NonNull UUID uuid, @NonNull String name) {
			PlayerProfile profile = Bukkit.createPlayerProfile(uuid, name);
			getMeta().setOwnerProfile(profile);
			return this;
		}

		public ItemBuilder.SkullBuilder setTexture(@NonNull String textureUrl) {
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

		public ItemBuilder.SkullBuilder setBase64Texture(@NonNull String base64Texture) {
			String textureUrlJson = new String(Base64.getDecoder().decode(base64Texture),
				StandardCharsets.UTF_8);

			String textureUrl = JsonParser.parseString(textureUrlJson)
				.getAsJsonObject()
				.get("textures").getAsJsonObject()
				.get("SKIN").getAsJsonObject()
				.get("url").getAsString();

			return setTexture(textureUrl);
		}

		@Nonnull
		@Override
		public SkullMeta getMeta() {
			return getCastedMeta();
		}

	}

	public static class PotionBuilder extends ItemBuilder {

		public PotionBuilder(@Nonnull Material material) {
			super(material);
		}

		public PotionBuilder(@Nonnull Material material, @Nonnull Message message) {
			super(material, message);
		}

		public PotionBuilder(@Nonnull Material material, @Nonnull String name) {
			super(material, name);
		}

		public PotionBuilder(@Nonnull Material material, @Nonnull String name, @Nonnull String... lore) {
			super(material, name, lore);
		}

		public PotionBuilder(@Nonnull Material material, @Nonnull String name, int amount) {
			super(material, name, amount);
		}

		public PotionBuilder(@Nonnull ItemStack item) {
			super(item);
		}

		@Nonnull
		@CheckReturnValue
		public static ItemBuilder createWaterBottle() {
			return new ItemBuilder.PotionBuilder(Material.POTION).setColor(Color.BLUE).hideAttributes();
		}

		@Nonnull
		public ItemBuilder.PotionBuilder addEffect(@Nonnull PotionEffect effect) {
			getMeta().addCustomEffect(effect, true);
			return this;
		}

		@Nonnull
		public ItemBuilder.PotionBuilder setColor(@Nonnull Color color) {
			getMeta().setColor(color);
			return this;
		}

		@Nonnull
		public ItemBuilder.PotionBuilder color(@Nonnull Color color) {
			return setColor(color);
		}

		@Nonnull
		@Override
		public PotionMeta getMeta() {
			return getCastedMeta();
		}

	}

	public static class LeatherArmorBuilder extends ItemBuilder {

		public LeatherArmorBuilder(@Nonnull Material material) {
			super(material);
		}

		public LeatherArmorBuilder(@Nonnull Material material, @Nonnull Message message) {
			super(material, message);
		}

		public LeatherArmorBuilder(@Nonnull Material material, @Nonnull String name) {
			super(material, name);
		}

		public LeatherArmorBuilder(@Nonnull Material material, @Nonnull String name, @Nonnull String... lore) {
			super(material, name, lore);
		}

		public LeatherArmorBuilder(@Nonnull Material material, @Nonnull String name, int amount) {
			super(material, name, amount);
		}

		public LeatherArmorBuilder(@Nonnull ItemStack item) {
			super(item);
		}

		@Nonnull
		public ItemBuilder.LeatherArmorBuilder setColor(@Nonnull Color color) {
			getMeta().setColor(color);
			return this;
		}

		@Nonnull
		public ItemBuilder.LeatherArmorBuilder color(@Nonnull Color color) {
			return setColor(color);
		}

		@Nonnull
		@Override
		public LeatherArmorMeta getMeta() {
			return getCastedMeta();
		}

	}

}
