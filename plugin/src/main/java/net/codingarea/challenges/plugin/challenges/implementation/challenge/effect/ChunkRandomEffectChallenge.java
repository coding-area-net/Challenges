package net.codingarea.challenges.plugin.challenges.implementation.challenge.effect;

import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.collection.IRandom;
import net.anweisen.utilities.common.collection.SeededRandomWrapper;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.spigot.events.PlayerIgnoreStatusChangeEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Since("2.1.1")
public class ChunkRandomEffectChallenge extends Setting {

	/*
	 * 	Saving potion effect to prevent the possibility that an effect remove was somehow skipped
	 */
	private Map<UUID, PotionEffect> currentPotionEffects = new HashMap<>();

	private long worldSeed;

	public ChunkRandomEffectChallenge() {
		super(MenuType.CHALLENGES);
		setCategory(SettingCategory.EFFECT);
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.CAULDRON, Message.forName("item-chunk-effect-challenge"));
	}

	@Override
	protected void onEnable() {
		currentPotionEffects = new HashMap<>();
		worldSeed = ChallengeAPI.getGameWorld(Environment.NORMAL).getSeed();
		broadcastFiltered(player -> {
			addEffect(player, player.getLocation());
		});
	}

	@Override
	protected void onDisable() {
		broadcastFiltered(player -> {
			removeEffect(player, player.getLocation());
		});
		worldSeed = 0;
		currentPotionEffects = null;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onGameModeChange(PlayerIgnoreStatusChangeEvent event) {
		if (!shouldExecuteEffect()) return;
		if (event.isIgnored()) {
			removeEffect(event.getPlayer(), event.getPlayer().getLocation());
		} else {
			addEffect(event.getPlayer(), event.getPlayer().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (event.getTo() == null) return;
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (event.getTo().getChunk() == event.getFrom().getChunk()) return;
		removeEffect(event.getPlayer(), event.getFrom());
		addEffect(event.getPlayer(), event.getTo());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent event) {
		if (event.getTo() == null) return;
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (event.getTo().getChunk() == event.getFrom().getChunk()) return;
		removeEffect(event.getPlayer(), event.getFrom());
		addEffect(event.getPlayer(), event.getTo());
	}

	@ScheduledTask(ticks = 20, async = false)
	public void onSecond() {
		broadcastFiltered(player -> {
			addEffect(player, player.getLocation());
		});
	}

	public void removeEffect(Player player, Location location) {
		PotionEffectType type = getEffect(location.getChunk()).getType();
		player.removePotionEffect(type);

		removeEffectInCache(player);
	}

	public void addEffect(Player player, Location location) {
		Chunk chunk = location.getChunk();
		PotionEffect effect = getEffect(chunk);

		removeEffectInCache(player);

		if (player.hasPotionEffect(effect.getType())) return;
		player.addPotionEffect(effect);
		currentPotionEffects.put(player.getUniqueId(), effect);
	}

	public void removeEffectInCache(Player player) {
		PotionEffect currentEffect = currentPotionEffects.remove(player.getUniqueId());
		if (currentEffect != null) {
			player.removePotionEffect(currentEffect.getType());
		}
	}

	public PotionEffect getEffect(Chunk chunk) {

		IRandom random = new SeededRandomWrapper(worldSeed * (
				(long) (chunk.getX() == 0 ? Integer.MAX_VALUE : chunk.getX()) * (chunk.getZ() == 0 ? Integer.MAX_VALUE : chunk.getZ())));

		PotionEffectType[] types = PotionEffectType.values();
		PotionEffectType type = types[random.nextInt(types.length)];

		return type.createEffect(Integer.MAX_VALUE, random.nextInt(type.isInstant() ? 1 : 4));
	}

}
