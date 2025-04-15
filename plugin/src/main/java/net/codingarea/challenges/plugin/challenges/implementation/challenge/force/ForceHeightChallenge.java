package net.codingarea.challenges.plugin.challenges.implementation.challenge.force;

import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
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
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
@ExcludeFromRandomChallenges
public class ForceHeightChallenge extends EndingForceChallenge {

	private int height;

	public ForceHeightChallenge() {
		super(MenuType.CHALLENGES, 2, 15);
		setCategory(SettingCategory.FORCE);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.IRON_BOOTS, Message.forName("item-force-height-challenge"));
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
				bossbar.setTitle(Message.forName("bossbar-force-height-waiting").asString());
				return;
			}

			bossbar.setColor(BarColor.GREEN);
			bossbar.setProgress(getProgress());
			bossbar.setTitle(Message.forName("bossbar-force-height-instruction").asString(height, ChallengeAPI.formatTime(getSecondsLeftUntilNextActivation())));
		};
	}

	@Override
	protected boolean isFailing(@Nonnull Player player) {
		return player.getLocation().getBlockY() != height;
	}

	@Override
	protected void broadcastFailedMessage(@Nonnull Player player) {
		Message.forName("force-height-fail").broadcast(Prefix.CHALLENGES, NameHelper.getName(player), player.getLocation().getBlockY());
	}

	@Override
	protected void broadcastSuccessMessage() {
		Message.forName("force-height-success").broadcast(Prefix.CHALLENGES);
	}

	@Override
	protected void chooseForcing() {
		World world = ChallengeAPI.getGameWorld(Environment.NORMAL);
		height = globalRandom.range(BukkitReflectionUtils.getMinHeight(world), world.getMaxHeight());
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return globalRandom.around(getValue() * 60, 30);
	}

	@Override
	protected int getForcingTime() {
		return globalRandom.range(3 * 60 + 30, 5 * 60);
	}

	@Override
	public void loadGameState(@NotNull Document document) {
		super.loadGameState(document);
		if (document.contains("target")) {
			height = document.getInt("target");
			setState(COUNTDOWN);
		}
	}

	@Override
	public void writeGameState(@NotNull Document document) {
		super.writeGameState(document);
		document.set("target", height);
	}

}
