package net.codingarea.challenges.plugin.challenges.type.helper;

import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.IModifier;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.content.ItemDescription;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.challenges.annotations.CanInstaKillOnEnable;
import net.codingarea.challenges.plugin.management.challenges.annotations.ExcludeFromRandomChallenges;
import net.codingarea.challenges.plugin.management.menu.generator.ChallengeMenuGenerator;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @autho<a href="r">KxmischesDomi | https://github</a>.com/kxmischesdomi
 * @since 2.0
 */
public final class ChallengeHelper {

	@Getter
  private static boolean inInstantKill = false;

	private ChallengeHelper() {
	}

	public static void kill(@Nonnull Player player) {

		if (!Bukkit.isPrimaryThread()) {
			Bukkit.getScheduler().runTask(Challenges.getInstance(), () -> kill(player));
			return;
		}

		inInstantKill = true;
		player.damage(Integer.MAX_VALUE);
		inInstantKill = false;
	}

	public static void kill(@Nonnull Player player, int delay) {
		Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), () -> kill(player), delay);
	}

  public static void updateItems(@Nonnull IChallenge challenge) {
		challenge.getType().executeWithGenerator(ChallengeMenuGenerator.class, gen -> gen.updateItem(challenge));
	}

	public static boolean canInstaKillOnEnable(@Nonnull IChallenge challenge) {
		return challenge.getClass().isAnnotationPresent(CanInstaKillOnEnable.class);
	}

	public static boolean isExcludedFromRandomChallenges(@Nonnull IChallenge challenge) {
		return challenge.getClass().isAnnotationPresent(ExcludeFromRandomChallenges.class);
	}

	public static void handleModifierClick(@Nonnull MenuClickInfo info, @Nonnull IModifier modifier) {
		int newValue = modifier.getValue();
		int amount = info.isShiftClick()
				? (modifier.getValue() == modifier.getMinValue() || info.isRightClick() && modifier.getValue() == (10 - (modifier.getMinValue() - 1)) ? 9 : 10)
				: 1;
		newValue += info.isRightClick() ? -amount : amount;

		if (newValue > modifier.getMaxValue())
			newValue = modifier.getMinValue();
		if (newValue < modifier.getMinValue())
			newValue = modifier.getMaxValue();

		modifier.setValue(newValue);
		modifier.playValueChangeTitle();
		SoundSample.CLICK.play(info.getPlayer());
	}

	@Nonnull
	public static String getColoredChallengeName(@Nonnull AbstractChallenge challenge) {
		ItemBuilder item = challenge.createDisplayItem();
		ItemDescription description = item.getBuiltByItemDescription();
		if (description == null) return Message.NULL;
		return description.getOriginalName();
	}

	public static void breakBlock(@Nonnull Block block, @Nullable ItemStack tool) {
		breakBlock(block, tool, null);
	}

	public static void breakBlock(@Nonnull Block block, @Nullable ItemStack tool, @Nullable Inventory targetInventory) {

		if (!ChallengeAPI.getDropChance(block.getType())) return;
		boolean putIntoInventory = ChallengeAPI.getItemsDirectIntoInventory() && targetInventory != null;

		List<Material> customDrops = ChallengeAPI.getCustomDrops(block.getType());
		Location location = block.getLocation().clone().add(0.5, 0, 0.5);
		if (!customDrops.isEmpty()) {
			if (putIntoInventory) {
				customDrops.forEach(drop -> InventoryUtils.dropOrGiveItem(targetInventory, location, drop));
			} else {
				customDrops.forEach(drop -> block.getWorld().dropItem(location, new ItemStack(drop)));
			}
			block.setType(Material.AIR);
			return;
		}

		if (putIntoInventory) {
			block.getDrops(tool).forEach(drop -> InventoryUtils.dropOrGiveItem(targetInventory, location, drop));
			block.setType(Material.AIR);
			return;
		}

		block.breakNaturally(tool);

	}

	public static void dropItem(@Nonnull ItemStack itemStack, @Nonnull Location dropLocation, @Nonnull Inventory inventory) {
		boolean directIntoInventory = Challenges.getInstance().getBlockDropManager().isItemsDirectIntoInventory();

		if (directIntoInventory) {
			InventoryUtils.dropOrGiveItem(inventory, dropLocation, itemStack);
			return;
		}

		if (dropLocation.getWorld() == null) return;
		dropLocation.getWorld().dropItemNaturally(dropLocation, itemStack);

	}

	public static boolean ignoreDamager(@Nonnull Entity damager) {
		Player damagerPlayer = getDamagerPlayer(damager);
		if (damagerPlayer == null) return false;
		return AbstractChallenge.ignorePlayer(damagerPlayer);
	}

	public static Player getDamagerPlayer(@Nonnull Entity damager) {
		if (damager instanceof Player) return ((Player) damager);
		if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player)
			return ((Player) ((Projectile) damager).getShooter());
		return null;
	}

	public static List<Player> getIngamePlayers() {
		return Bukkit.getOnlinePlayers().stream().filter(player -> !AbstractChallenge.ignorePlayer(player)).collect(Collectors.toList());
	}

	public static void playToggleChallengeTitle(@Nonnull AbstractChallenge challenge) {
		playToggleChallengeTitle(challenge, challenge.isEnabled());
	}

	public static void playToggleChallengeTitle(@Nonnull AbstractChallenge challenge, boolean enabled) {
		Challenges.getInstance().getTitleManager().sendChallengeStatusTitle(enabled ? Message.forName("title-challenge-enabled") : Message.forName("title-challenge-disabled"), getColoredChallengeName(challenge));
	}

	public static void playChangeChallengeValueTitle(@Nonnull AbstractChallenge challenge, @Nonnull IModifier modifier) {
		playChangeChallengeValueTitle(challenge, modifier.getValue());
	}

	public static void playChangeChallengeValueTitle(@Nonnull Modifier modifier) {
		playChangeChallengeValueTitle(modifier, modifier.getValue());
	}

	public static void playChangeChallengeValueTitle(@Nonnull AbstractChallenge modifier, @Nullable Object value) {
		Challenges.getInstance().getTitleManager().sendChallengeStatusTitle(Message.forName("title-challenge-value-changed"), getColoredChallengeName(modifier), value);
	}

	public static void playChallengeHeartsValueChangeTitle(@Nonnull AbstractChallenge challenge, int health) {
		playChangeChallengeValueTitle(challenge, (health / 2f) + " §c❤");
	}

	public static void playChallengeHeartsValueChangeTitle(@Nonnull Modifier modifier) {
		playChallengeHeartsValueChangeTitle(modifier, modifier.getValue());
	}

	public static void playChallengeSecondsValueChangeTitle(@Nonnull AbstractChallenge challenge, int seconds) {
		playChangeChallengeValueTitle(challenge, Message.forName("subtitle-time-seconds").asString(seconds));
	}

	public static void playChallengeSecondsRangeValueChangeTitle(@Nonnull AbstractChallenge challenge, int min, int max) {
		playChangeChallengeValueTitle(challenge, Message.forName("subtitle-time-seconds-range").asString(min, max));
	}

	public static void playChallengeMinutesValueChangeTitle(@Nonnull AbstractChallenge challenge, int seconds) {
		playChangeChallengeValueTitle(challenge, Message.forName("subtitle-time-minutes").asString(seconds));
	}

	@Nonnull
	public static String[] getTimeRangeSettingsDescription(@Nonnull Modifier modifier, @Nonnegative int multiplier, @Nonnegative int range) {
		return Message.forName("item-time-seconds-range-description").asArray(modifier.getValue() * multiplier - range, modifier.getValue() * multiplier + range);
	}

	@Nonnull
	public static String[] getTimeRangeSettingsDescription(@Nonnull Modifier modifier, @Nonnegative int range) {
		return getTimeRangeSettingsDescription(modifier, 1, range);
	}

	public static boolean finalDamageIsNull(@Nonnull EntityDamageEvent event) {
		return getFinalDamage(event) == 0;
	}

	public static double getFinalDamage(@Nonnull EntityDamageEvent event) {
		return event.getFinalDamage() + event.getDamage(DamageModifier.ABSORPTION);
	}

}
