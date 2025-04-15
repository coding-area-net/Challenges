package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.0
 */
@Since("2.0")
public class StoneSightChallenge extends Setting {

	private final Random random = new Random();

	public StoneSightChallenge() {
		super(MenuType.CHALLENGES);
		setCategory(SettingCategory.ENTITIES);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.COBBLESTONE, Message.forName("item-stone-sight-challenge"));
	}

	@ScheduledTask(ticks = 1, async = false)
	public void onTick() {

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (ignorePlayer(player)) continue;

			RayTraceResult result = player.getWorld().rayTraceEntities(
					player.getEyeLocation(),
					player.getLocation().getDirection(),
					30,
					0.01,
					entity -> !(entity instanceof Player) && !(entity instanceof EnderDragon) && entity instanceof LivingEntity
			);
			if (result == null) continue;

			Location location = result.getHitPosition().toLocation(player.getWorld());
			LivingEntity entity = ((LivingEntity) result.getHitEntity());
			if (entity == null) continue;

			double distance = entity.getEyeLocation().distance(location) * 5;

			BoundingBox box = entity.getBoundingBox();
			double volume = box.getWidthX() + box.getWidthZ() + box.getHeight();
			if (distance > volume) continue;

			entity.getLocation().getBlock().setType(getRandomStone(), false);
			entity.remove();
			SoundSample.BREAK.play(player);

		}

	}

	@Nonnull
	private Material getRandomStone() {
		Material[] materials = {
				Material.STONE,
				Material.STONE,
				Material.STONE,
				Material.COBBLESTONE,
				Material.COBBLESTONE,
				Material.MOSSY_COBBLESTONE
		};
		return materials[random.nextInt(materials.length)];
	}

}