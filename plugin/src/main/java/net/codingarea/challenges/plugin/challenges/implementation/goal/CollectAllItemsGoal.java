package net.codingarea.challenges.plugin.challenges.implementation.goal;

import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.utils.item.ItemUtils;
import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.collection.SeededRandomWrapper;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Since("2.0")
public class CollectAllItemsGoal extends SettingGoal implements SenderCommand {

	@Getter
    private final int totalItemsCount;
	private SeededRandomWrapper random;
	@Getter
    private List<Material> allItemsToFind;
	@Getter
    private List<Material> itemsToFind;
	@Getter
    private Material currentItem;

	public CollectAllItemsGoal() {
		random = new SeededRandomWrapper();
		reloadItemsToFind();
		totalItemsCount = itemsToFind.size();
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.GRASS_BLOCK, Message.forName("item-all-items-goal"));
	}

	private void reloadItemsToFind() {
		allItemsToFind = new ArrayList<>(Arrays.asList(ExperimentalUtils.getMaterials()));
		allItemsToFind.removeIf(material -> !material.isItem());
		allItemsToFind.removeIf(material -> !ItemUtils.isObtainableInSurvival(material));
		Collections.shuffle(allItemsToFind, random);
		itemsToFind = new ArrayList<>(allItemsToFind);
		nextItem();

		if (isEnabled())
			bossbar.update();
	}

	private void nextItem() {
		if (itemsToFind.isEmpty()) {
			ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED);
			return;
		}
		currentItem = itemsToFind.remove(0);
	}

	@Override
	protected void onEnable() {
	  bossbar.setContent((bossbar, player) -> {
	   if (currentItem == null) {
	    bossbar.setTitle(Message.forName("bossbar-all-items-finished").asString());
	    return;
	   }
	   bossbar.setTitle(Message.forName("bossbar-all-items-current-max").asComponent(
	       getItemDisplayName(currentItem),
	       totalItemsCount - itemsToFind.size() + 1,
	       totalItemsCount));
	  });
	  bossbar.show();
	}

	@Override
	protected void onDisable() {
		bossbar.hide();
	}

	@Override
	public void onCommand(@Nonnull CommandSender sender, @Nonnull String[] args) throws Exception {
		if (!isEnabled()) {
			Message.forName("challenge-disabled").send(sender, Prefix.CHALLENGES);
			SoundSample.BASS_OFF.playIfPlayer(sender);
			return;
		}
		if (currentItem == null) {
			Message.forName("all-items-already-finished").send(sender, Prefix.CHALLENGES);
			SoundSample.BASS_OFF.playIfPlayer(sender);
			return;
		}

		Message.forName("all-items-skipped").broadcast(Prefix.CHALLENGES, getItemDisplayName(currentItem));
		SoundSample.PLING.broadcast();
		nextItem();
		bossbar.update();

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onClick(@Nonnull PlayerInventoryClickEvent event) {
		if (event.isCancelled()) return;
		ItemStack item = event.getCurrentItem();
		if (item == null) return;
		handleNewItem(item.getType(), event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPickUp(@Nonnull PlayerPickupItemEvent event) {
		Material material = event.getItem().getItemStack().getType();
		handleNewItem(material, event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInteract(@Nonnull PlayerInteractEvent event) {
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
			Material material = item.getType();
			handleNewItem(material, event.getPlayer());
		}, 1);
	}

	protected void handleNewItem(@Nullable Material material, @Nonnull Player player) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(player)) return;
		if (currentItem != material) return;
		Message.forName("all-items-found").broadcast(Prefix.CHALLENGES, getItemDisplayName(currentItem) , NameHelper.getName(player));
		SoundSample.PLING.broadcast();
		nextItem();
		bossbar.update();
	}

	@Override
	public void getWinnersOnEnd(@Nonnull List<Player> winners) {
	}

	@Override
	public void loadGameState(@Nonnull Document document) {
		super.loadGameState(document);
		random = new SeededRandomWrapper(document.getLong("seed"));
		reloadItemsToFind();

		for (int i = 0; i < document.getInt("found"); i++)
			nextItem();
	}

	@Override
	public void writeGameState(@Nonnull Document document) {
		super.writeGameState(document);
		document.set("seed", random.getSeed());
		document.set("found", totalItemsCount - itemsToFind.size());
	}

    private String getItemDisplayName(@Nullable Material material) {
		if (material == null) return "Unbekannt";

		String name = material.name();

		if (name.contains("SMITHING_TEMPLATE")) {
			String prefix = name.replace("_SMITHING_TEMPLATE", "");
			String formattedPrefix = Arrays.stream(prefix.split("_"))
					.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
					.collect(Collectors.joining(" "));

			return formattedPrefix + " Smithing template";
		}

		if (name.endsWith("_BANNER_PATTERN")) {
			String prefix = name.replace("_BANNER_PATTERN", "");
			String formattedPrefix = Arrays.stream(prefix.split("_"))
					.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
					.collect(Collectors.joining(" "));

			return formattedPrefix + " Banner Pattern";
		}

		return Arrays.stream(name.split("_"))
						.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
						.collect(Collectors.joining(" "));
	}
}
