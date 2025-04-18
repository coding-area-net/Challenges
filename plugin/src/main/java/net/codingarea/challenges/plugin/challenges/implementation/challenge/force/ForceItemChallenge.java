package net.codingarea.challenges.plugin.challenges.implementation.challenge.force;

import net.codingarea.challenges.plugin.utils.item.ItemUtils;
import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.CompletableForceChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.challenges.annotations.ExcludeFromRandomChallenges;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeBossBar.BossBarInstance;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

@Since("2.0")
@ExcludeFromRandomChallenges
public class ForceItemChallenge extends CompletableForceChallenge {

	private Material item;

	public ForceItemChallenge() {
		super(MenuType.CHALLENGES, 2, 15);
		setCategory(SettingCategory.FORCE);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.LEATHER_BOOTS, Message.forName("item-force-item-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return ChallengeHelper.getTimeRangeSettingsDescription(this, 60, 30);
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeSecondsRangeValueChangeTitle(this, getValue() * 60 - 30, getValue() * 60 + 30);
	}

	@Nonnull
	@Override
	protected BiConsumer<BossBarInstance, Player> setupBossbar() {
		return (bossbar, player) -> {
			if (getState() == WAITING) {
				bossbar.setTitle(Message.forName("bossbar-force-item-waiting").asString());
				return;
			}

			bossbar.setColor(BarColor.GREEN);
			bossbar.setProgress(getProgress());
			bossbar.setTitle(Message.forName("bossbar-force-item-instruction").asComponent(item, ChallengeAPI.formatTime(getSecondsLeftUntilNextActivation())));
		};
	}

	@Override
	protected void broadcastFailedMessage() {
		Message.forName("force-item-fail").broadcast(Prefix.CHALLENGES, item);
	}

	@Override
	protected void broadcastSuccessMessage(@Nonnull Player player) {
		Message.forName("force-item-success").broadcast(Prefix.CHALLENGES, NameHelper.getName(player), item);
	}

	@Override
	protected void chooseForcing() {
		List<Material> items = new ArrayList<>(Arrays.asList(ExperimentalUtils.getMaterials()));
		items.removeIf(material -> !ItemUtils.isObtainableInSurvival(material));
		items.removeIf(material -> !material.isItem());
		items.removeIf(BlockUtils::isTooHardToGet);

		item = globalRandom.choose(items);
	}

	@Override
	protected int getForcingTime() {
		return globalRandom.range(5 * 60, 8 * 60);
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return globalRandom.around(getValue() * 60, 30);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onClick(@Nonnull PlayerInventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if (item == null) return;
		if (item.getType() != this.item) return;
		completeForcing(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPickUp(@Nonnull PlayerPickupItemEvent event) {
		Material material = event.getItem().getItemStack().getType();
		if (material != item) return;
		completeForcing(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInteract(@Nonnull PlayerInteractEvent event) {
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
			Material material = item.getType();
			if (material != this.item) return;
			completeForcing(event.getPlayer());
		}, 1);
	}

	@Override
	public void loadGameState(@NotNull Document document) {
		super.loadGameState(document);
		if (document.contains("target")) {
			item = document.getEnum("target", Material.class);
			setState(item == null ? WAITING : COUNTDOWN);
		}
	}

	@Override
	public void writeGameState(@NotNull Document document) {
		super.writeGameState(document);
		document.set("target", item);
	}

}
