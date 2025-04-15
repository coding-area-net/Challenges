package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.EntityDamageByPlayerEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.3
 */
@Since("2.1.3")
public class RandomTeleportOnHitChallenge extends Setting {

	public RandomTeleportOnHitChallenge() {
		super(MenuType.CHALLENGES);
		setCategory(SettingCategory.RANDOMIZER);
	}

	public static void switchEntityLocations(LivingEntity entity1, LivingEntity entity2) {
		entity1.setInvisible(true);
		entity2.setInvisible(false);
		Location entity2Location = entity2.getLocation().clone();
		entity2.teleport(entity1.getLocation());
		entity1.teleport(entity2Location);
		entity2.setInvisible(false);
		entity1.setInvisible(false);
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.ENDER_CHEST, Message.forName("item-mob-damage-teleport-challenge"));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamageByPlayer(EntityDamageByPlayerEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getDamager())) return;

		World world = event.getDamager().getWorld();
		List<LivingEntity> livingEntities = new ArrayList<>(world.getLivingEntities());
		livingEntities.removeIf(entity -> entity == event.getDamager() || entity instanceof Player && ignorePlayer((Player) entity));
		LivingEntity entity = globalRandom.choose(livingEntities);

		switchEntityLocations(entity, event.getDamager());
	}

}
