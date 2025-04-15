package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.anweisen.utilities.bukkit.utils.misc.CompatibilityUtils;
import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.2
 */
@Since("2.1.2")
public class HotBarRandomizerChallenge extends TimedChallenge {

	public HotBarRandomizerChallenge() {
		super(MenuType.CHALLENGES, 1, 10, 5);
		setCategory(SettingCategory.RANDOMIZER);
	}

	/**
	 * @param force if true only sets items if inventory is empty
	 */
	public static void addItems(Player player, boolean force) {

		if (!force && !player.getInventory().isEmpty()) {
			return;
		}

		player.getInventory().clear();
		for (int i = 0; i < 9; i++) {
			player.getInventory().setItem(i, InventoryUtils.getRandomItem(false, true));
		}
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.HOPPER_MINECART, Message.forName("item-hotbar-randomizer-challenge"));
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return getValue() * 60;
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-time-minutes-description").asArray(getValue());
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeMinutesValueChangeTitle(this, getValue());
	}

	@Override
	protected void onTimeActivation() {

		broadcastFiltered(player -> {
			addItems(player, true);
		});
		restartTimer();
	}

	@TimerTask(status = TimerStatus.RUNNING)
	public void onStart() {
		// Execute after hotbar items are removed
		Bukkit.getScheduler().runTask(plugin, () -> {
			broadcastFiltered(player -> {
				addItems(player, false);
			});

		});
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		addItems(event.getPlayer(), false);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		event.setDropItems(false);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClick(@Nonnull PlayerInventoryClickEvent event) {
		Player player = event.getPlayer();
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(player)) return;
		Inventory clickedInventory = event.getClickedInventory();
		if (event.getCursor() == null) return;
		if (clickedInventory == null) return;
		InventoryType type = CompatibilityUtils.getTopInventory(player).getType();
		if (type == InventoryType.WORKBENCH || type == InventoryType.CRAFTING) return;
		if (clickedInventory.getType() == InventoryType.CRAFTING) return;
		if (clickedInventory.getType() == InventoryType.PLAYER) {
			if (event.getInventory().getType() != InventoryType.PLAYER) {
				event.setCancelled(true);
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerDropItem(@Nonnull PlayerDropItemEvent event) {
		if (!shouldExecuteEffect()) return;
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplosion(EntityExplodeEvent event) {
		if (!shouldExecuteEffect()) return;
		for (Block block : event.blockList()) {
			block.setType(Material.AIR);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockExplosion(BlockExplodeEvent event) {
		if (!shouldExecuteEffect()) return;
		for (Block block : event.blockList()) {
			block.setType(Material.AIR);
		}
	}

}
