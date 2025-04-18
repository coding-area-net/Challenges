package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class PlayerGlowSetting extends Setting {

	public PlayerGlowSetting() {
		super(MenuType.SETTINGS);
	}


	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.GLASS_BOTTLE, Message.forName("item-glow-setting"));
	}

	@Override
	protected void onEnable() {
		updateEffects();
	}

	@Override
	protected void onDisable() {
		updateEffects();
	}

	@TimerTask(status = {TimerStatus.PAUSED, TimerStatus.RUNNING}, async = false)
	@ScheduledTask(ticks = 50, async = false, timerPolicy = TimerPolicy.ALWAYS)
	public void updateEffects() {
		if (!shouldExecuteEffect()) {
			broadcast(player -> player.removePotionEffect(PotionEffectType.GLOWING));
			return;
		}
		broadcast(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, true, false, false)));
	}

	@EventHandler
	public void onPlayerJoin(@Nonnull PlayerJoinEvent event) {
		updateEffects();
	}

}
