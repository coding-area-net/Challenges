package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;

public class NoHitDelaySetting extends Setting {

	public NoHitDelaySetting() {
		super(MenuType.SETTINGS);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onDamageByEntity(@Nonnull EntityDamageEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) return;
		if (!shouldExecuteEffect()) return;
		Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), () -> {
			((LivingEntity) event.getEntity()).setNoDamageTicks(0);
		}, 1);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.FEATHER, Message.forName("item-no-hit-delay-setting"));
	}

}
