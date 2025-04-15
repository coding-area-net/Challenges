package net.codingarea.challenges.plugin.challenges.implementation.challenge.movement;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.0
 */
@Since("2.0")
public class OnlyDownChallenge extends Setting {

	public OnlyDownChallenge() {
		super(MenuType.CHALLENGES);
		setCategory(SettingCategory.MOVEMENT);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.ACACIA_SLAB, Message.forName("item-only-down-challenge"));
	}

	@EventHandler
	public void onPlayerMove(@Nonnull PlayerMoveEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (event.getTo() == null) return;
		if (event.getTo().getBlockY() <= event.getFrom().getBlockY()) return;
		Message.forName("only-down-failed").broadcast(Prefix.CHALLENGES, NameHelper.getName(event.getPlayer()));
		ChallengeHelper.kill(event.getPlayer());
	}

}