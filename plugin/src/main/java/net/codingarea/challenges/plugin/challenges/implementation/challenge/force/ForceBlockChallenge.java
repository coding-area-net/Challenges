package net.codingarea.challenges.plugin.challenges.implementation.challenge.force;

import net.codingarea.challenges.plugin.utils.item.ItemUtils;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.EndingForceChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.challenges.annotations.ExcludeFromRandomChallenges;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeBossBar.BossBarInstance;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.BiConsumer;

@ExcludeFromRandomChallenges
public class ForceBlockChallenge extends EndingForceChallenge {

	private Material block;

	public ForceBlockChallenge() {
		super(MenuType.CHALLENGES, 2, 15);
		setCategory(SettingCategory.FORCE);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.GOLDEN_BOOTS, Message.forName("item-force-block-challenge"));
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
				bossbar.setTitle(Message.forName("bossbar-force-block-waiting").asString());
				return;
			}

			bossbar.setColor(BarColor.GREEN);
			bossbar.setProgress(getProgress());
			bossbar.setTitle(Message.forName("bossbar-force-block-instruction").asComponent(block, ChallengeAPI.formatTime(getSecondsLeftUntilNextActivation())));
		};
	}

	@Override
	protected boolean isFailing(@Nonnull Player player) {
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				for (int y = -1; y <= 1; y++) {
					Material type = player.getLocation().add(x, y, z).getBlock().getType();
					if (type == block) return false;
				}
			}
		}
		return true;
	}

	@Override
	protected void broadcastFailedMessage(@Nonnull Player player) {
		Message.forName("force-block-fail").broadcast(Prefix.CHALLENGES, NameHelper.getName(player), player.getLocation().subtract(0, 1, 0).getBlock().getType());
	}

	@Override
	protected void broadcastSuccessMessage() {
		Message.forName("force-block-success").broadcast(Prefix.CHALLENGES);
	}

	@Override
	protected void chooseForcing() {
		Material[] materials = Arrays.stream(ExperimentalUtils.getMaterials())
				.filter(Material::isBlock)
				.filter(ItemUtils::isObtainableInSurvival)
				.filter(material -> !BlockUtils.isTooHardToGet(material))
				.filter(material -> !material.name().contains("WALL"))
				.toArray(length -> new Material[length]);
		block = globalRandom.choose(materials);
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return globalRandom.around(getValue() * 60, 30);
	}

	@Override
	protected int getForcingTime() {
		return globalRandom.range(4 * 60, 6 * 60);
	}

	@Override
	public void loadGameState(@NotNull Document document) {
		super.loadGameState(document);
		if (document.contains("target")) {
			block = document.getEnum("target", Material.class);
			setState(block == null ? WAITING : COUNTDOWN);
		}
	}

	@Override
	public void writeGameState(@NotNull Document document) {
		super.writeGameState(document);
		document.set("target", block);
	}

}
