package net.codingarea.challenges.plugin.spigot.command;

import net.anweisen.utilities.bukkit.utils.animation.AnimatedInventory;
import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.menu.MenuPosition;
import net.anweisen.utilities.bukkit.utils.menu.positions.SlottedMenuPosition;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.cloud.CloudSupportManager;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.management.stats.PlayerStats;
import net.codingarea.challenges.plugin.management.stats.Statistic;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder.SkullBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.codingarea.challenges.plugin.utils.misc.StatsHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public class LeaderboardCommand implements PlayerCommand {

	protected static final int[] slots = StatsHelper.getSlots(1);
	protected static final int[] navigationSlots = {45, 53};
	protected static final AnimatedInventory loadingInventory;

	static {
		loadingInventory = new AnimatedInventory(InventoryTitleManager.getLeaderboardTitle(), 6 * 9, MenuPosition.HOLDER).setEndSound(null).setFrameSound(null);
		loadingInventory.createAndAdd().fill(ItemBuilder.FILL_ITEM).setItem(31, new ItemBuilder(Material.BARRIER, "§8» §cLoading.."));
	}

	@Override
	public void onCommand(@Nonnull Player player, @Nonnull String[] args) throws Exception {
		if (!Challenges.getInstance().getStatsManager().isEnabled()) {
			Message.forName("feature-disabled").send(player, Prefix.CHALLENGES);
			SoundSample.BASS_OFF.play(player);
			return;
		} else if (!Challenges.getInstance().getStatsManager().hasDatabaseConnection()) {
			Message.forName("no-database-connection").send(player, Prefix.CHALLENGES);
			SoundSample.BASS_OFF.play(player);
			return;
		}

		createInventory(player).open(player, Challenges.getInstance());
	}

	@Nonnull
	@CheckReturnValue
	public AnimatedInventory createInventory(@Nonnull Player player) {
		AnimatedInventory inventory = new AnimatedInventory(InventoryTitleManager.getLeaderboardTitle(), 4 * 9, MenuPosition.HOLDER);
		StatsHelper.setAccent(inventory, 2);
		SlottedMenuPosition position = new SlottedMenuPosition();
		for (int i = 0; i < Statistic.values().length; i++) {
			Statistic statistic = Statistic.values()[i];
			ItemBuilder item = new ItemBuilder(StatsHelper.getMaterial(statistic), "§8» " + StatsHelper.getNameMessage(statistic).asString());
			inventory.cloneLastAndAdd().setItem(slots[i], item.hideAttributes());
			position.setAction(slots[i], () -> openMenu(player, statistic, 0, false));
		}

		MenuPosition.set(player, position);
		return inventory;
	}

	private void openMenu(@Nonnull Player player, @Nonnull Statistic statistic, int page, boolean openInstant) {
		loadingInventory.open(player, Challenges.getInstance());
		MenuPosition.setEmpty(player);
		Challenges.getInstance().runAsync(() -> openMenu0(player, statistic, page, openInstant));
	}

	private void openMenu0(@Nonnull Player player, @Nonnull Statistic statistic, int page, boolean openInstant) {

		int[] slots = {
				10, 11, 12, 13, 14, 15, 16,
				19, 20, 21, 22, 23, 24, 25,
				28, 29, 30, 31, 32, 33, 34,
				37, 38, 39, 40, 41, 42, 43
		};

		String statisticName = StatsHelper.getNameMessage(statistic).asString();
		AnimatedInventory inventory = new AnimatedInventory(InventoryTitleManager.getLeaderboardTitle(ChatColor.stripColor(statisticName), page + 1), 6 * 9, MenuPosition.HOLDER);
		inventory.createAndAdd().fill(ItemBuilder.FILL_ITEM);

		List<PlayerStats> leaderboard = Challenges.getInstance().getStatsManager().getLeaderboard(statistic);
		int pages = leaderboard.size() / slots.length;
		if (leaderboard.size() % slots.length > 0) pages++;
		int offset = page * slots.length;

		InventoryUtils.setNavigationItemsToFrame(inventory.cloneLastAndAdd(), navigationSlots, true, page, pages);
		SlottedMenuPosition position = new SlottedMenuPosition();
		CloudSupportManager cloudSupport = Challenges.getInstance().getCloudSupportManager();

		for (int i = offset; i < leaderboard.size() && i < offset + slots.length; i++) {
			int slot = slots[i - offset];
			PlayerStats stats = leaderboard.get(i);
			String coloredName = cloudSupport.isNameSupport() && cloudSupport.hasNameFor(stats.getPlayerUUID()) ? cloudSupport.getColoredName(stats.getPlayerUUID()) : stats.getPlayerName();
			ItemBuilder item = new SkullBuilder().setOwner(stats.getPlayerUUID(), stats.getPlayerName())
				.setName(Message.forName("stats-leaderboard-display")
					.asArray(coloredName, statistic.formatChat(stats.getStatisticValue(statistic)), statisticName, i + 1));
			inventory.cloneLastAndAdd().setItem(slot, item.hideAttributes());

			position.setAction(slot, () -> {
				MenuPosition.setEmpty(player);
				loadingInventory.open(player, Challenges.getInstance());
				player.performCommand("stats " + stats.getPlayerName());
			});
		}

		position.setAction(navigationSlots[0], info -> {
			if (page == 0 || info.isShiftClick()) {
				createInventory(player).openNotAnimated(player, true, Challenges.getInstance());
			} else {
				openMenu(player, statistic, page - 1, true);
			}
		});
		if (inventory.getLastFrame().getItemType(navigationSlots[1]) == Material.PLAYER_HEAD)
			position.setAction(navigationSlots[1], () -> openMenu(player, statistic, page + 1, true));

		if (openInstant) inventory.openNotAnimated(player, true, Challenges.getInstance());
		else inventory.open(player, Challenges.getInstance());
		MenuPosition.set(player, position);
	}

}
