package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Since("2.0")
public class MobSightDamageChallenge extends SettingModifier {

	public MobSightDamageChallenge() {
		super(MenuType.CHALLENGES);
		setCategory(SettingCategory.ENTITIES);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.SPIDER_EYE, Message.forName("item-no-mob-sight-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-heart-damage-description").asArray(getValue() / 2f);
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeHeartsValueChangeTitle(this);
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
					entity -> !(entity instanceof Player) && entity instanceof LivingEntity
			);
			if (result == null) continue;

			Location location = result.getHitPosition().toLocation(player.getWorld());
			LivingEntity entity = ((LivingEntity) result.getHitEntity());
			if (entity == null) continue;

			double distance = entity.getEyeLocation().distance(location) * 5;

			BoundingBox box = entity.getBoundingBox();
			double volume = box.getWidthX() + box.getWidthZ() + box.getHeight();
			if (distance > volume) continue;

			player.damage(getValue());
		}

	}

}
