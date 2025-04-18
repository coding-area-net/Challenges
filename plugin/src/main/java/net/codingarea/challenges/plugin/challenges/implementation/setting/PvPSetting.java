package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.spigot.events.EntityDamageByPlayerEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import javax.annotation.Nonnull;

public class PvPSetting extends Setting {

	public PvPSetting() {
		super(MenuType.SETTINGS, true);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.STONE_SWORD, Message.forName("item-pvp-setting"));
	}

	@EventHandler
	public void onDamage(@Nonnull EntityDamageByPlayerEvent event) {
		if (isEnabled()) return;
		if (!(event.getEntity() instanceof Player)) return;
		event.setCancelled(true);
	}

}
