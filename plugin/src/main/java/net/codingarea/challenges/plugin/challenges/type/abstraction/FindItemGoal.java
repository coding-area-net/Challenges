package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public abstract class FindItemGoal extends SettingGoal {

	private final Material searchedItem;

	public FindItemGoal(Material searchedItem) {
		this.searchedItem = searchedItem;
	}

	@Override
	public void getWinnersOnEnd(@NotNull List<Player> winners) {

	}

	private void checkItem(ItemStack itemStack, @Nonnull Player player) {
		if (itemStack == null) return;
		if (itemStack.getType() != searchedItem) return;
		ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED, () -> Collections.singletonList(player));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPickup(PlayerPickupItemEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		checkItem(event.getItem().getItemStack(), event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(PlayerInventoryClickEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (event.getClickedInventory() == null) return;
		if (event.getClickedInventory().getHolder() != event.getPlayer()) return;
		checkItem(event.getCurrentItem(), event.getPlayer());
	}

}
