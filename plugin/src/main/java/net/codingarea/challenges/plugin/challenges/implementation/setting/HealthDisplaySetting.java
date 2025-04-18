package net.codingarea.challenges.plugin.challenges.implementation.setting;

import javax.annotation.Nonnull;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

@Since("2.0")
public class HealthDisplaySetting extends Setting {

	public static final String OBJECTIVE_NAME = "health_display";

	public HealthDisplaySetting() {
		super(MenuType.SETTINGS, true);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.RED_STAINED_GLASS, Message.forName("item-health-display-setting"));
	}

	@Override
	protected void onEnable() {
		Bukkit.getOnlinePlayers().forEach(this::show);
	}

	@Override
	protected void onDisable() {
		Bukkit.getOnlinePlayers().forEach(this::hide);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(@Nonnull PlayerJoinEvent event) {
		if (isEnabled()) {
			show(event.getPlayer());
		} else {
			hide(event.getPlayer());
		}
	}

	private void show(@Nonnull Player player) {
		Scoreboard scoreboard = player.getScoreboard();
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		if (manager == null) return;
		if (player.getScoreboard() == manager.getMainScoreboard())
			player.setScoreboard(scoreboard = manager.getNewScoreboard());

		Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
		if (objective == null)
			objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "health", OBJECTIVE_NAME);
		// Criteria interface only available since 1.20

		objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

		try {
			objective.setRenderType(RenderType.HEARTS);
		} catch (Exception ex) {
			Challenges.getInstance().getLogger().severe("Tablist Health could not be updated. You are using an outdated version of spigot.");
			// In some versions of spigot RenderType does not exist
		}

	}

	private void hide(@Nonnull Player player) {

		Scoreboard scoreboard = player.getScoreboard();
		Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
		if (objective == null) return;

		try {
			objective.unregister();
		} catch (Exception ex) {
			Challenges.getInstance().getLogger().severe("Error while unregistering tablist hearts objective");
		}

	}

}
