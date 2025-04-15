package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.EntityDeathByPlayerEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.2
 */
@Since("2.1.2")
public class MobsRespawnInEndChallenge extends Setting {

	private final Map<EntityType, Integer> toSpawnEntities = new HashMap<>();
	private int totalMobsInEnd = 0;

	public MobsRespawnInEndChallenge() {
		super(MenuType.CHALLENGES);
		setCategory(SettingCategory.ENTITIES);
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.ENDER_EYE, Message.forName("item-respawn-end-challenge"));
	}

	@Override
	protected void onEnable() {
		bossbar.setContent((bar, player) -> {
			bar.setColor(BarColor.PURPLE);
			bar.setTitle(Message.forName("bossbar-respawn-end").asString(totalMobsInEnd));
		});
		bossbar.show();
	}

	@Override
	protected void onDisable() {
		bossbar.hide();
	}

	@Override
	public void writeGameState(@NotNull Document document) {
		document.set("total", totalMobsInEnd);
	}

	@Override
	public void loadGameState(@NotNull Document document) {
		totalMobsInEnd = document.getInt("total");
	}

	private void addEntityToSpawn(EntityType type) {
		if (!ChallengeAPI.isPlayerInGameWorld(World.Environment.THE_END)) {
			increaseEntityToSpawnCount(type);
		} else {
			spawnEntityInEnd(type, 1);
		}
		totalMobsInEnd++;
		bossbar.update();
	}

	private void spawnEntityInEnd(EntityType type, int count) {
		World world = ChallengeAPI.getGameWorld(World.Environment.THE_END);
		Location spawnLocation = world.getHighestBlockAt(0, 0).getLocation().add(0.5, 1, 0.5);
		for (int i = 0; i < count; i++) {
			Entity entity = world.spawnEntity(spawnLocation, type);

			if (entity instanceof LivingEntity) {
				entity.setVelocity(Vector.getRandom().multiply(ThreadLocalRandom.current().nextBoolean() ? -1 : 1).setY(0));
				((LivingEntity) entity).setNoDamageTicks(20 * 5);
			}
		}
	}

	private void increaseEntityToSpawnCount(EntityType type) {
		Integer count = toSpawnEntities.getOrDefault(type, 0);
		toSpawnEntities.put(type, count + 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMobKill(EntityDeathByPlayerEvent event) {
		if (!shouldExecuteEffect()) return;
		if (event.getEntity() instanceof Player || event.getEntity() instanceof EnderDragon) return;
		if (event.getEntity().getLocation().getWorld() == null) return;
		if (event.getEntity().getLocation().getWorld().getEnvironment() == World.Environment.THE_END)
			return;
		if (ChallengeHelper.ignoreDamager(event.getKiller())) return;
		addEntityToSpawn(event.getEntityType());
		SoundSample.PLING.play((event.getKiller()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldChange(PlayerTeleportEvent event) {
		if (event.getTo() == null || event.getTo().getWorld() == null || event.getTo().getWorld() == event.getFrom().getWorld())
			return;
		if (event.getTo().getWorld().getEnvironment() != World.Environment.THE_END) return;

		for (Map.Entry<EntityType, Integer> entry : toSpawnEntities.entrySet()) {
			EntityType type = entry.getKey();
			Integer count = entry.getValue();
			spawnEntityInEnd(type, count);
		}
		toSpawnEntities.clear();

	}

}
