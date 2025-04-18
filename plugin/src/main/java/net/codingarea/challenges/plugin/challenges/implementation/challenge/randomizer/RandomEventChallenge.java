package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Since("2.0")
public class RandomEventChallenge extends TimedChallenge {

	private final Event[] events;

	public RandomEventChallenge() {
		super(MenuType.CHALLENGES, 1, 10, 3, false);
		setCategory(SettingCategory.RANDOMIZER);
		events = new Event[]{
				new SpeedEvent(),
                new SpawnEntitiesEvent(),
				new HoleEvent(),
				new FlyEvent(),
                new CobWebEvent(),
				new ReplaceOresEvent(),
				new SicknessEvent()
		};
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.CLOCK, Message.forName("item-random-event-challenge").asItemDescription(events.length));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-time-seconds-range-description").asArray(getValue() * 60 - 30, getValue() * 60 + 30);
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeSecondsRangeValueChangeTitle(this, getValue() * 60 - 30, getValue() * 60 + 30);
	}

	@Override
	protected int getSecondsUntilNextActivation() {
		return globalRandom.around(getValue() * 60, 30);
	}

	@Override
	protected void onTimeActivation() {
		restartTimer();
		Event event = globalRandom.choose(events);
		Logger.debug("Running random event {}", event.getClass().getSimpleName());
		event.getActivationMessage().broadcastRandom(globalRandom, Prefix.CHALLENGES);
		broadcastFiltered(event::run);
	}

	public interface Event {

		@Nonnull
		Message getActivationMessage();

		void run(@Nonnull Player player);

	}

	public static class SpeedEvent implements Event {

		@Nonnull
		@Override
		public Message getActivationMessage() {
			return Message.forName("random-event-speed");
		}

		@Override
		public void run(@Nonnull Player player) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 99));
		}

	}

	public static class HoleEvent implements Event {

		@Nonnull
		@Override
		public Message getActivationMessage() {
			return Message.forName("random-event-hole");
		}

		@Override
		public void run(@Nonnull Player player) {
			Location location = player.getLocation().getBlock().getLocation();
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					for (int y = 1; y >= -8; y--) {
						location.clone().add(x, y, z).getBlock().setType(Material.AIR, true);
					}
				}
			}
		}

	}

	public static class FlyEvent implements Event {

		@Nonnull
		@Override
		public Message getActivationMessage() {
			return Message.forName("random-event-fly");
		}

		@Override
		public void run(@Nonnull Player player) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 3 * 20, 5));
		}

	}

	public static class ReplaceOresEvent implements Event {

		@Nonnull
		@Override
		public Message getActivationMessage() {
			return Message.forName("random-event-ores");
		}

		@Override
		public void run(@Nonnull Player player) {
			Location location = player.getLocation();
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				for (int x = -100; x <= 100; x++) {
					for (int z = -100; z <= 100; z++) {
						int finalZ = z;
						int finalX = x;
						Bukkit.getScheduler().runTask(plugin, () -> {
							if (location.getWorld() == null) return;
							for (int y = BukkitReflectionUtils.getMinHeight(location.getWorld()); y < 80; y++) {
								Location current = location.clone().add(finalX, 0, finalZ);
								current.setY(y);
								Block block = current.getBlock();
								if (block.getType().name().contains("ORE")) {
									Environment environment = location.getWorld().getEnvironment();
									block.setType(environment == Environment.NETHER ? Material.NETHERRACK : environment == Environment.THE_END ? Material.END_STONE : Material.STONE, true);
								}
							}
						});
					}
				}
			});
		}

	}

	public static class SicknessEvent implements Event {

		@Nonnull
		@Override
		public Message getActivationMessage() {
			return Message.forName("random-event-sickness");
		}

		@Override
		public void run(@Nonnull Player player) {
			player.addPotionEffect(new PotionEffect(MinecraftNameWrapper.NAUSEA, 7 * 20, 0));
			player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 3 * 20, 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, 1));
		}
	}

	public static class SpawnEntitiesEvent implements Event {

		@Nonnull
		@Override
		public Message getActivationMessage() {
			return Message.forName("random-event-entities");
		}

		@Override
		public void run(@Nonnull Player player) {
			EntityType type = globalRandom.choose(EntityType.PIG, EntityType.CHICKEN, EntityType.CAT, EntityType.SILVERFISH, EntityType.WOLF);

			for (int i = 0; i < globalRandom.nextInt(5) + 5; i++) {
				Location randomLocation = player.getLocation().add(globalRandom.nextInt(10) - 5, -10, globalRandom.nextInt(10 - 5));
				if (randomLocation.getWorld() == null) return;
				while (!randomLocation.getBlock().isPassable() && randomLocation.getBlockY() < randomLocation.getWorld().getMaxHeight())
					randomLocation.add(0, 1, 0);

				randomLocation.getWorld().spawnEntity(randomLocation, type);
			}
		}

	}

	public static class CobWebEvent implements Event {

		@Nonnull
		@Override
		public Message getActivationMessage() {
			return Message.forName("random-event-webs");
		}

		@Override
		public void run(@Nonnull Player player) {
			for (int i = 0; i < 13; i++) {
				Location randomLocation = player.getLocation().add(globalRandom.nextInt(10) - 5, -20, globalRandom.nextInt(10 - 5));
				if (randomLocation.getWorld() == null) return;
				while (!randomLocation.getBlock().isPassable() && randomLocation.getBlockY() < randomLocation.getWorld().getMaxHeight())
					randomLocation.add(0, 1, 0);

				randomLocation.getBlock().setType(Material.COBWEB, false);
			}
		}

	}

}
