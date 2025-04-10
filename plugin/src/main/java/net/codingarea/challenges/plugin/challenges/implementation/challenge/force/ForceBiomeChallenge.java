package net.codingarea.challenges.plugin.challenges.implementation.challenge.force;

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
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
@Since("2.0")
@ExcludeFromRandomChallenges
public class ForceBiomeChallenge extends CompletableForceChallenge {

	private Biome biome;

	public ForceBiomeChallenge() {
		super(MenuType.CHALLENGES, 2, 20, 5);
		setCategory(SettingCategory.FORCE);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.CHAINMAIL_BOOTS, Message.forName("item-force-biome-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return ChallengeHelper.getTimeRangeSettingsDescription(this, 60 * 3, 60);
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeSecondsRangeValueChangeTitle(this, getValue() * 60 * 3 - 60, getValue() * 60 * 3 + 60);
	}

	@Nonnull
	@Override
	protected BiConsumer<BossBarInstance, Player> setupBossbar() {
		return (bossbar, player) -> {
			if (getState() == WAITING) {
				bossbar.setTitle(Message.forName("bossbar-force-biome-waiting").asString());
				return;
			}

			bossbar.setColor(BarColor.GREEN);
			bossbar.setProgress(getProgress());
			bossbar.setTitle(Message.forName("bossbar-force-biome-instruction").asComponent(biome, ChallengeAPI.formatTime(getSecondsLeftUntilNextActivation())));
		};
	}

	@Override
	protected void broadcastFailedMessage() {
		Message.forName("force-biome-fail").broadcast(Prefix.CHALLENGES, BukkitStringUtils.getBiomeName(biome));
	}

	@Override
	protected void broadcastSuccessMessage(@Nonnull Player player) {
		Message.forName("force-biome-success").broadcast(Prefix.CHALLENGES, NameHelper.getName(player), BukkitStringUtils.getBiomeName(biome));
	}

	@Override
	protected void chooseForcing() {
		Biome[] biomes = Arrays.stream(Biome.values())
				.filter(biome -> !biome.name().contains("END"))
				.filter(biome -> !biome.name().contains("MUSHROOM"))
				.filter(biome -> !biome.name().contains("VOID"))
				.filter(biome -> !biome.name().equals("CUSTOM"))
				.toArray(Biome[]::new);

		biome = globalRandom.choose(biomes);
	}

	@Override
	protected int getForcingTime() {
		return globalRandom.around(getRarity(biome) * 60 * 6, 60);
	}

	private int getRarity(@Nonnull Biome biome) {
		Object[][] mapping = {
				{"BADLANDS", 5},
				{"JUNGLE", 4},
				{"BAMBOO", 4},
				{"MODIFIED", 3},
				{"TALL", 3},
				{"SWAMP", 2},
		};

		for (Object[] pair : mapping) {
			String key = (String) pair[0];
			if (biome.name().contains(key))
				return (int) pair[1];
		}
		return 1;
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return globalRandom.around(getValue() * 60 * 3, 60);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(@Nonnull PlayerMoveEvent event) {
		if (!shouldExecuteEffect()) return;
		if (event.getTo() == null) return;
		if (event.getTo().getBlock().getBiome() != biome) return;
		completeForcing(event.getPlayer());
	}

	@Override
	public void loadGameState(@NotNull Document document) {
		super.loadGameState(document);
		if (document.contains("target")) {
			String biomeName = document.getString("target");
			biome = Registry.BIOME.get(NamespacedKey.minecraft(Objects.requireNonNull(biomeName).toLowerCase()));
			setState(biome == null ? WAITING : COUNTDOWN);
		}
	}


	@Override
	public void writeGameState(@NotNull Document document) {
		super.writeGameState(document);
		document.set("target", biome);
	}

}
