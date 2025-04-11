package net.codingarea.challenges.plugin.challenges.implementation.challenge;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.implementation.setting.MaxHealthSetting;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.bukkit.misc.Version.MinecraftVersion;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.0
 */
@Since("2.0")
public class ZeroHeartsChallenge extends SettingModifier {

	public ZeroHeartsChallenge() {
		super(MenuType.CHALLENGES, 5, 30, 10);
	}

	@Override
	protected void onEnable() {
		bossbar.setContent((bossbar, player) -> {
			int currentTime = getCurrentTime();
			int maxTime = getValue() * 60;
			bossbar.setTitle(Message.forName("bossbar-zero-hearts").asString(maxTime - currentTime));
			bossbar.setColor(BarColor.GREEN);
			bossbar.setProgress(1 - ((float) currentTime / maxTime));
		});
		bossbar.show();
		Bukkit.getOnlinePlayers().forEach(player -> {
			AttributeInstance attribute;
			if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_21_2)) {
				attribute = player.getAttribute(Attribute.valueOf("MAX_HEALTH"));
			} else {
				attribute = player.getAttribute(Attribute.valueOf("GENERIC_MAX_HEALTH"));
			}
			if (attribute == null) return;
			attribute.setBaseValue(0);
		});
	}

	@Override
	protected void onDisable() {
		bossbar.hide();
		AbstractChallenge.getFirstInstance(MaxHealthSetting.class).onValueChange();
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE, Message.forName("item-zero-hearts-challenge"));
	}

	@Override
	protected void onValueChange() {
		bossbar.update();
	}

	@ScheduledTask(ticks = 20, async = false)
	public void onSecond() {
		broadcast(player -> {
			AttributeInstance attribute;
			if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_21_2)) {
				attribute = player.getAttribute(Attribute.valueOf("MAX_HEALTH"));
			} else {
				attribute = player.getAttribute(Attribute.valueOf("GENERIC_MAX_HEALTH"));
			}
			if (attribute == null) return;
			attribute.setBaseValue(0);
		});
		int absorptionTime = getCurrentTime();
		if (absorptionTime >= getValue() * 60) {
			bossbar.hide();
			if (absorptionTime == getValue() * 60) {
				removeAbsorptionEffect();
			}
			return;
		}
		getGameStateData().set("absorption-time", absorptionTime + 1);
		applyAbsorptionEffect();
		bossbar.update();
	}

	private int getCurrentTime() {
		return getGameStateData().getInt("absorption-time");
	}

	private void applyAbsorptionEffect() {
		broadcastFiltered(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, (getValue() - getCurrentTime()) * 20 * 60, 1, true, false, false)));
	}

	private void removeAbsorptionEffect() {
		broadcastFiltered(player -> player.removePotionEffect(PotionEffectType.ABSORPTION));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityPotionEffect(@Nonnull EntityPotionEffectEvent event) {
		if (event.getAction() != Action.REMOVED) return;
		if (!(event.getEntity() instanceof Player)) return;
		if (!shouldExecuteEffect()) return;
		Player player = (Player) event.getEntity();
		if (ignorePlayer(player)) return;
		kill(player);
		Message.forName("zero-hearts-failed").broadcast(Prefix.CHALLENGES, NameHelper.getName(player));
	}

}