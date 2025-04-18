package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Since("2.0")
public class RandomItemDroppingChallenge extends TimedChallenge {

	public RandomItemDroppingChallenge() {
		super(MenuType.CHALLENGES, 1, 60, 5);
		setCategory(SettingCategory.RANDOMIZER);
	}

	public static void dropRandomItem(Player player) {
		if (player.getInventory().getContents().length <= 0) return;
		dropRandomItem(player.getLocation(), player.getInventory());
	}

	public static void dropRandomItem(@Nonnull Location location, @Nonnull Inventory inventory) {
		if (location.getWorld() == null) return;
		int slot = InventoryUtils.getRandomFullSlot(inventory);
		if (slot == -1) return;
		ItemStack item = inventory.getItem(slot);
		if (item == null) return;
		inventory.setItem(slot, null);
		InventoryUtils.dropItemByPlayer(location, item);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.DISPENSER, Message.forName("item-random-dropping-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-time-seconds-description").asArray(getValue());
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeSecondsValueChangeTitle(this, getValue());
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return getValue();
	}

	@Override
	protected void onTimeActivation() {
		restartTimer();

		Bukkit.getScheduler().runTask(plugin, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (ignorePlayer(player)) continue;

				dropRandomItem(player);
			}
		});
	}

}
