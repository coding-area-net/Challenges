package net.codingarea.challenges.plugin.management.inventory;

import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.collection.pair.Triple;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.config.FileDocument;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder.SkullBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public final class PlayerInventoryManager implements Listener {

	private final List<HotbarItem> hotbarItems;

	public PlayerInventoryManager() {
		Challenges.getInstance().registerListener(this);
		ChallengeAPI.registerScheduler(this);
		ChallengeAPI.subscribeLoader(LanguageLoader.class, () -> Bukkit.getOnlinePlayers().forEach(Challenges.getInstance().getPlayerInventoryManager()::updateInventoryAuto));

		hotbarItems = new LinkedList<>();
		loadItems();
	}

	public void loadItems() {
		FileDocument config = Challenges.getInstance().getConfig("hotbar-items.yml");
		loadItem(config.getDocument("timer"), "item-menu-timer", "challenges.timer", p -> p.performCommand("timer"));
		loadItem(config.getDocument("challenges"), "item-menu-challenges", "challenges.gui", p -> p.performCommand("challenges"));
		loadItem(config.getDocument("start"), "item-menu-start", "challenges.timer", p -> p.performCommand("start"));
		loadItem(config.getDocument("leaderboard"), "item-menu-leaderboard", null, p -> p.performCommand("leaderboard"));
		loadItem(config.getDocument("stats"), "item-menu-stats", null, p -> p.performCommand("stats"));
	}

	public void loadItem(Document config, String message, String permission, Consumer<Player> action) {
		if (!config.getBoolean("enabled", false)) return;
		int slot = config.getInt("slot", 0);
		Material material = config.getEnum("material", Material.BARRIER);
		HotbarItem item = new HotbarItem(slot, message, material, action, permission);
		hotbarItems.add(item);
	}

	public void handleDisable() {
		Bukkit.getOnlinePlayers().forEach(this::removeItems);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCommandsUpdate(@Nonnull PlayerCommandSendEvent event) {
		updateInventoryAuto(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(@Nonnull PlayerJoinEvent event) {
		updateInventoryJoin(event.getPlayer(), true);
	}

	@EventHandler
	public void onQuit(@Nonnull PlayerQuitEvent event) {
		removeItems(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onGameModeChange(@Nonnull PlayerGameModeChangeEvent event) {
		updateInventoryGamemode(event.getPlayer(), event.getNewGameMode());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onDrop(@Nonnull PlayerDropItemEvent event) {
		if (ChallengeAPI.isStarted()) return;
		if (!hasItems(event.getPlayer())) return;
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRespawn(@Nonnull PlayerRespawnEvent event) {
		updateInventoryAlive(event.getPlayer(), true);
	}

	@EventHandler
	public void onInteract(@Nonnull PlayerInteractEvent event) {
		switch (event.getAction()) {
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
			case PHYSICAL:
				return;
		}

		if (ChallengeAPI.isStarted()) return;
		if (!hasItems(event.getPlayer())) return;
		Triple<ItemStack, Consumer<Player>, String>[] pairs = createItemPairs(event.getPlayer());

		int slot = event.getPlayer().getInventory().getHeldItemSlot();
		if (slot >= pairs.length) return;
		Triple<ItemStack, Consumer<Player>, String> pair = pairs[slot];
		if (pair == null) return;
		if (pair.getThird() != null && !event.getPlayer().hasPermission(pair.getThird())) return;

		Consumer<Player> action = pair.getSecond();
		if (action == null) return;

		action.accept(event.getPlayer());
		SoundSample.PLOP.play(event.getPlayer());
	}

	@TimerTask(status = {TimerStatus.PAUSED, TimerStatus.RUNNING})
	public void updateInventories() {
		Bukkit.getOnlinePlayers().forEach(this::updateInventoryAuto);
	}

	public void updateInventoryAuto(@Nonnull Player player) {
		updateInventory(player, player.getGameMode(), false, !player.isDead());
	}

	public void updateInventoryAlive(@Nonnull Player player, boolean alive) {
		updateInventory(player, player.getGameMode(), false, alive);
	}

	public void updateInventoryGamemode(@Nonnull Player player, @Nonnull GameMode gamemode) {
		updateInventory(player, gamemode, false, !player.isDead());
	}

	public void updateInventoryJoin(@Nonnull Player player, boolean join) {
		updateInventory(player, player.getGameMode(), join, !player.isDead());
	}

	public void updateInventory(@Nonnull Player player, @Nonnull GameMode gamemode, boolean join, boolean alive) {
		if (Bukkit.isPrimaryThread()) {
			Challenges.getInstance().runAsync(() -> updateInventory(player, gamemode, join, alive));
			return;
		}

		try {
			if (!LanguageLoader.isLoaded()) return;
			if (ChallengeAPI.isPaused()) {
				updateInventoryPaused(player, gamemode, join, alive);
			} else {
				updateInventoryStarted(player, gamemode, join, alive);
			}
		} catch (Exception ex) {
			Logger.error("Failed to update inventory", ex);
		}
	}

	private void updateInventoryStarted(@Nonnull Player player, @Nonnull GameMode gamemode, boolean join, boolean alive) {
		removeItems(player);
	}

	private void updateInventoryPaused(@Nonnull Player player, @Nonnull GameMode gamemode, boolean join, boolean alive) {
		if (gamemode == GameMode.CREATIVE || gamemode == GameMode.SPECTATOR) {
			removeItems(player);
			return;
		}
		if (canGiveItems(player)) {
			if (!alive) return;
			giveItems(player);
			if (join) player.getInventory().setHeldItemSlot(4);
		}
	}

	private boolean hasItems(@Nonnull Player player) {
		Triple<ItemStack, Consumer<Player>, String>[] pairs = createItemPairs(player);
		for (int i = 0; i < pairs.length; i++) {
			Triple<ItemStack, Consumer<Player>, String> pair = pairs[i];
			ItemStack expected = pair == null ? null : pair.getFirst();
			ItemStack found = player.getInventory().getItem(i);
			if (pair != null && pair.getThird() != null && !player.hasPermission(pair.getThird()))
				continue;
			if (expected != null && found == null) return false;
			if (expected == null) continue;
			if (expected.getType() != found.getType()) return false;
		}
		return true;
	}

	private boolean canGiveItems(@Nonnull Player player) {
		Triple<ItemStack, Consumer<Player>, String>[] pairs = createItemPairs(player);
		for (int i = 0; i < pairs.length; i++) {
			Triple<ItemStack, Consumer<Player>, String> pair = pairs[i];
			ItemStack expected = pair == null ? null : pair.getFirst();
			ItemStack found = player.getInventory().getItem(i);
			if (expected == null && found != null) return false;
			if (expected == null) continue;
			if (found == null) continue;
			if (expected.getType() != found.getType()) return false;
		}
		return true;
	}

	private void removeItems(@Nonnull Player player) {
		Triple<ItemStack, Consumer<Player>, String>[] pairs = createItemPairs(player);
		for (Triple<ItemStack, Consumer<Player>, String> pair : pairs) {
			if (pair == null) continue;
			ItemStack item = pair.getFirst();
			if (item == null) continue;
			if (item.getItemMeta() == null) continue;
			ItemStack[] content = player.getInventory().getContents();
			for (int i = 0; i < content.length; i++) {
				ItemStack current = content[i];
				if (current == null) continue;
				if (current.getType() != item.getType()) continue;
				if (current.getItemMeta() == null) continue;
				if (!current.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()))
					continue;
				player.getInventory().setItem(i, null);
			}
		}
	}

	private void giveItems(@Nonnull Player player) {
		Triple<ItemStack, Consumer<Player>, String>[] pairs = createItemPairs(player);
		for (int i = 0; i < pairs.length; i++) {
			Triple<ItemStack, Consumer<Player>, String> pair = pairs[i];
			if (pair == null) continue;
			if (pair.getThird() != null && !player.hasPermission(pair.getThird())) continue;
			player.getInventory().setItem(i, pair.getFirst());
		}
	}

	@Nonnull
	private Triple<ItemStack, Consumer<Player>, String>[] createItemPairs(@Nonnull Player player) {
		Triple<ItemStack, Consumer<Player>, String>[] pairs = new Triple[9];

		for (HotbarItem item : hotbarItems) {

			ItemStack stack;
			if (item.getMaterial() == Material.PLAYER_HEAD) {
				stack = new SkullBuilder(player.getUniqueId(), player.getName(), Message.forName(item.getMessage()).asString()).build();
			} else {
				stack = new ItemBuilder(item.getMaterial(), Message.forName(item.getMessage()).asString()).build();
			}

			pairs[item.getSlot()] = new Triple<>(
					stack,
					item.getAction(),
					item.getPermission()
			);
		}

		return pairs;
	}

	@Getter
  public static class HotbarItem {

		private final int slot;
		private final String message;
		private final Material material;
		private final Consumer<Player> action;
		private final String permission;

		public HotbarItem(int slot, String message, Material material, Consumer<Player> action, String permission) {
			this.slot = slot;
			this.message = message;
			this.material = material;
			this.action = action;
			this.permission = permission;
		}

  }

}
