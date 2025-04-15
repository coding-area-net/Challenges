package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityResurrectEvent;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.0
 */
@Since("2.0")
public class TotemSaveDeathSetting extends Setting {

	public TotemSaveDeathSetting() {
		super(MenuType.SETTINGS);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.TOTEM_OF_UNDYING, Message.forName("item-totem-save-setting"));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityResurrect(@Nonnull EntityResurrectEvent event) {
		if (ChallengeHelper.isInInstantKill() && !isEnabled()) {
			event.setCancelled(true);
		}
	}

}