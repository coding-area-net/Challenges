package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nonnull;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
@Since("2.0")
public class DeathPositionSetting extends Setting {

	private static final String POSITION_PREFIX = "death-";

	public DeathPositionSetting() {
		super(MenuType.SETTINGS);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(@Nonnull PlayerDeathEvent event) {
		if (!shouldExecuteEffect()) return;

		int index = 1;
		while (AbstractChallenge.getFirstInstance(PositionSetting.class).containsPosition(POSITION_PREFIX + index))
			index++;

		Player player = event.getEntity();
		player.performCommand("pos " + POSITION_PREFIX + index);

	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.MUSIC_DISC_11, Message.forName("item-death-position-setting"));
	}

}
