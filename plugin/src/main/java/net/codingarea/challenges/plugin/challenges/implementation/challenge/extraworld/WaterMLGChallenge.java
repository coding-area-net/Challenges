package net.codingarea.challenges.plugin.challenges.implementation.challenge.extraworld;

import net.codingarea.challenges.plugin.challenges.implementation.setting.OneTeamLifeSetting;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.WorldDependentChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WaterMLGChallenge extends WorldDependentChallenge {

	public WaterMLGChallenge() {
		super(MenuType.CHALLENGES, 1, 10, 5, false);
		setCategory(SettingCategory.EXTRA_WORLD);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.WATER_BUCKET, Message.forName("item-water-mlg-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-time-seconds-range-description").asArray(getValue() * 60 - 10, getValue() * 60 + 10);
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeSecondsRangeValueChangeTitle(this, getValue() * 60 - 10, getValue() * 60 + 10);
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return globalRandom.around(getValue() * 60, 10);
	}

	@Override
	public void startWorldChallenge() {
		Location currentLocation = new Location(getExtraWorld(), 0, 150, 0);

		teleportToWorld(false, (player, index) -> {
			currentLocation.add(100, 0, 0);
			player.getInventory().setHeldItemSlot(4);
			player.getInventory().setItem(4, new ItemStack(Material.WATER_BUCKET));
			player.teleport(currentLocation);
		});

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			teleportBack();
			restartTimer();
		}, 10 * 20);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerBucketEmpty(@Nonnull PlayerBucketEmptyEvent event) {
		if (!isInExtraWorld()) return;

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			event.getBlock().setType(Material.AIR);
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				teleportBack(event.getPlayer());
			}, 40);
		}, 10);

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(@Nonnull EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (!isInExtraWorld()) return;

		if (AbstractChallenge.getFirstInstance(OneTeamLifeSetting.class).isEnabled()) {
			teleportBack();
		} else {
			Player player = (Player) event.getEntity();
			teleportBack(player);
		}
	}

}
