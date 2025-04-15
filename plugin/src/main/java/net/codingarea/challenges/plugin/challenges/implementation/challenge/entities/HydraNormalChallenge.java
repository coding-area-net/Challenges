package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.codingarea.challenges.plugin.challenges.type.abstraction.HydraChallenge;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 1.0
 */
public class HydraNormalChallenge extends HydraChallenge {

	public HydraNormalChallenge() {
		super(MenuType.CHALLENGES);
		setCategory(SettingCategory.ENTITIES);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.WITCH_SPAWN_EGG, Message.forName("item-hydra-challenge"));
	}

	@Override
	public int getNewMobsCount(@Nonnull EntityType entityType) {
		return 2;
	}
}