package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.KillMobsGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.3
 */
@Since("2.1.3")
public class KillAllMobsGoal extends KillMobsGoal {

	public KillAllMobsGoal() {
		super(getAllMobsToKill());
		setCategory(SettingCategory.KILL_ENTITY);
	}

	static List<EntityType> getAllMobsToKill() {
		LinkedList<EntityType> list = new LinkedList<>(Arrays.asList(EntityType.values()));
		list.removeIf(type -> !type.isAlive());
		list.remove(EntityType.GIANT);
		list.remove(EntityType.ILLUSIONER);
		list.remove(EntityType.PLAYER);
		list.remove(EntityType.ARMOR_STAND);
		return list;
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.BOW, Message.forName("item-all-mobs-goal"));
	}

	@Override
	public Message getBossbarMessage() {
		return Message.forName("bossbar-kill-all-mobs");
	}

}
