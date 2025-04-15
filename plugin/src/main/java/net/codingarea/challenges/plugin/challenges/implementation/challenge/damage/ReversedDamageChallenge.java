package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.EntityDamageByPlayerEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 1.0
 */
public class ReversedDamageChallenge extends Setting {

	public ReversedDamageChallenge() {
		super(MenuType.CHALLENGES);
		setCategory(SettingCategory.DAMAGE);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.GOLDEN_SWORD, Message.forName("item-reversed-damage-challenge"));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamageByPlayer(@Nonnull EntityDamageByPlayerEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getDamager())) return;

		double damage = event.getFinalDamage();
		event.getDamager().damage(damage);
	}

}