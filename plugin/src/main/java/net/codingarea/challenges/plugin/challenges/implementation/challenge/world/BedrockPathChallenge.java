package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder.LeatherArmorBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @autho<a href="r">KxmischesDomi | https://github</a>.com/kxmischesdomi
 * @since 1.0
 */
public class BedrockPathChallenge extends Setting {

	public BedrockPathChallenge() {
		super(MenuType.CHALLENGES);
		setCategory(SettingCategory.WORLD);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new LeatherArmorBuilder(Material.LEATHER_BOOTS, Message.forName("item-bedrock-path-challenge")).setColor(Color.GRAY);
	}

	@EventHandler
	public void onMove(@Nonnull PlayerMoveEvent event) {
		if (!shouldExecuteEffect()) return;
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR)
			return;

		BlockUtils.createBlockPath(event.getFrom(), event.getTo(), Material.BEDROCK);
	}

}