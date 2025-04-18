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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Since("2.0")
public class RandomItemSwappingChallenge extends TimedChallenge {

	public RandomItemSwappingChallenge() {
		super(MenuType.CHALLENGES, 1, 60, 5);
		setCategory(SettingCategory.RANDOMIZER);
	}

	public static void swapRandomItems(Player player) {
		if (player.getInventory().getContents().length == 0) return;
		int slot = InventoryUtils.getRandomFullSlot(player.getInventory());
		if (slot == -1) return;
		swapItemToRandomSlot(
				player.getInventory(),
				InventoryUtils.getRandomFullSlot(player.getInventory()),
				InventoryUtils.getRandomSlot(player.getInventory())
		);
	}

	private static void swapItemToRandomSlot(@Nonnull Inventory inventory, int slot1, int slot2) {
		if (slot1 == -1 || slot2 == -1) return;
		ItemStack item1 = inventory.getItem(slot1);
		ItemStack item2 = inventory.getItem(slot2);
		inventory.setItem(slot1, item2);
		inventory.setItem(slot2, item1);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.HOPPER, Message.forName("item-random-swapping-challenge"));
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
				swapRandomItems(player);
			}

		});

	}

}
