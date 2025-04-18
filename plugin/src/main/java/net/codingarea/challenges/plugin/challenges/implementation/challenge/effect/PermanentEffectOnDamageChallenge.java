package net.codingarea.challenges.plugin.challenges.implementation.challenge.effect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.collection.pair.Tuple;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.challenges.plugin.utils.misc.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Since("2.0")
public class PermanentEffectOnDamageChallenge extends SettingModifier {

	private static final int GLOBAL_EFFECT = 1;
	private final Random random = new Random();

	public PermanentEffectOnDamageChallenge() {
		super(MenuType.CHALLENGES, 1, 2);
		setCategory(SettingCategory.EFFECT);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.MAGMA_CREAM, Message.forName("item-permanent-effect-on-damage-challenge"));
	}

	@Override
	protected void onEnable() {
		if (!shouldExecuteEffect()) {
			clearEffects();
			return;
		}
		updateEffects();
	}

	@Override
	protected void onDisable() {
		clearEffects();
	}

	@TimerTask(status = TimerStatus.RUNNING, async = false)
	public void onTimerStart() {
		if (!isEnabled()) return;
		updateEffects();
	}

	@TimerTask(status = TimerStatus.PAUSED, async = false)
	public void onTimerPause() {
		if (!isEnabled()) return;
		clearEffects();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConsume(@Nonnull PlayerItemConsumeEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (event.getItem().getType() != Material.MILK_BUCKET) return;
		Bukkit.getScheduler().runTask(plugin, this::updateEffects);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(@Nonnull PlayerJoinEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		updateEffects();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerItemConsume(@Nonnull PlayerItemConsumeEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		updateEffects();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerGameModeChange(@Nonnull PlayerGameModeChangeEvent event) {
		if (!shouldExecuteEffect()) return;
		Bukkit.getScheduler().runTask(plugin, () -> {
			clearEffects();
			updateEffects();
		});
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerDamage(@Nonnull EntityDamageEvent event) {
		if (!shouldExecuteEffect()) return;
		if (event.getFinalDamage() <= 0 && event.getDamage(DamageModifier.ABSORPTION) >= 0) return;
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if (ignorePlayer(player)) return;
		DamageCause cause = event.getCause();
		if (cause == DamageCause.POISON || cause == DamageCause.WITHER) return;

		addRandomEffect(player);
		updateEffects();
	}

	public void addRandomEffect(@Nonnull Player eventPlayer) {
		PotionEffectType randomEffect = getNewRandomEffect();
		if (randomEffect == null) return;
		applyNewEffect(eventPlayer, randomEffect);
	}

	private void applyNewEffect(@Nonnull Player player, @Nonnull PotionEffectType potionEffectType) {
		String path = player.getUniqueId().toString();
		Document effects = getGameStateData().getDocument(path);

		int amplifier = 1;
		if (!effects.contains(potionEffectType.getName())) {
			effects.set(potionEffectType.getName(), 1);
		} else {
			amplifier = effects.getInt(potionEffectType.getName()) + 1;
			effects.set(potionEffectType.getName(), amplifier);
		}

		if (effectsToEveryone()) {
			Message.forName("new-effect").broadcast(Prefix.CHALLENGES, potionEffectType, amplifier);
		} else {
			Message.forName("new-effect").send(player, Prefix.CHALLENGES, potionEffectType, amplifier);
		}

		getGameStateData().set(path, effects);
	}

	@Nullable
	private PotionEffectType getNewRandomEffect() {
		ArrayList<PotionEffectType> possibleEffects = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
		possibleEffects.remove(MinecraftNameWrapper.INSTANT_HEALTH);
		possibleEffects.remove(MinecraftNameWrapper.INSTANT_DAMAGE);
		return possibleEffects.get(random.nextInt(possibleEffects.size()));
	}

	public void updateEffects() {
		forEachEffect((player, effectType, amplifier) -> {
			if (effectsToEveryone()) {
				broadcastFiltered(currentPlayer -> {
					addEffect(currentPlayer, effectType, amplifier);
				});
			} else {
				if (!ignorePlayer(player)) {
					addEffect(player, effectType, amplifier);
				}
			}
		});
	}

	private void addEffect(@Nonnull Player player, @Nonnull PotionEffectType effectType, int amplifier) {

		if (player.hasPotionEffect(effectType)) {
			PotionEffect effect = player.getPotionEffect(effectType);
			if (effect != null && effect.getAmplifier() == amplifier - 1) {
				return;
			}
		}

		player.removePotionEffect(effectType);
		player.addPotionEffect(new PotionEffect(effectType, Integer.MAX_VALUE, amplifier - 1));
	}

	private void clearEffects() {
		forEachEffect((player, potionEffectType, amplifier) -> {
			broadcast(currentPlayer -> {
				currentPlayer.removePotionEffect(potionEffectType);
			});
		});
	}

	private void forEachEffect(@Nonnull TriConsumer<Player, PotionEffectType, Integer> action) {
		List<Tuple<PotionEffectType, Integer>> effects = new ArrayList<>();

		for (String uuid : getGameStateData().keys()) {

			Document effectsDocument = getGameStateData().getDocument(uuid);
			for (String effectName : effectsDocument.keys()) {
				PotionEffectType effectType = PotionEffectType.getByName(effectName);
				int amplifier = effectsDocument.getInt(effectName);
				if (effectType == null) continue;

				if (effectsToEveryone()) {
					addEffectToList(effects, effectType, amplifier);
				} else {
					try {
						Player player = Bukkit.getPlayer(UUID.fromString(uuid));
						action.accept(player, effectType, amplifier);
					} catch (Exception ex) {
						Logger.debug("UUID formation for uuid '" + uuid + "' failed in permanent effect challenge!");
					}
				}
			}

		}

		effects.forEach(tuple -> {
			broadcastFiltered(player -> {
				action.accept(player, tuple.getFirst(), tuple.getSecond());
			});
		});
	}

	private void addEffectToList(@Nonnull List<Tuple<PotionEffectType, Integer>> effectsList, @Nonnull PotionEffectType effectType, int amplifier) {
		Tuple<PotionEffectType, Integer> effectTuple = effectsList.stream().filter(tuple -> tuple.getFirst() == effectType).findFirst()
				.orElse(new Tuple<>(effectType, 0));
		effectTuple.setSecond(effectTuple.getSecond() + amplifier);

		if (!effectsList.contains(effectTuple)) effectsList.add(effectTuple);
	}

	private boolean effectsToEveryone() {
		return GLOBAL_EFFECT == getValue();
	}

	@Override
	protected void onValueChange() {
		if (!shouldExecuteEffect()) return;
		clearEffects();
		updateEffects();
	}

	@Nonnull
	@Override
	public ItemBuilder createSettingsItem() {
		if (!isEnabled()) return DefaultItem.disabled();
		if (getValue() == GLOBAL_EFFECT)
			return DefaultItem.create(Material.ENDER_CHEST, Message.forName("everyone").asString()).appendLore("", Message.forName("item-permanent-effect-target-everyone-description").asString());
		return DefaultItem.create(Material.CHEST, Message.forName("player").asString()).appendLore("", Message.forName("item-permanent-effect-target-player-description").asString());
	}

	@Override
	public void playValueChangeTitle() {
		if (GLOBAL_EFFECT == getValue()) {
			ChallengeHelper.playChangeChallengeValueTitle(this, Message.forName("everyone").asString());
		} else {
			ChallengeHelper.playChangeChallengeValueTitle(this, Message.forName("player").asString());
		}
	}

	@Override
	public void loadGameState(@Nonnull Document document) {
		super.loadGameState(document);
		broadcast(player -> {
			for (PotionEffect potionEffect : player.getActivePotionEffects()) {
				player.removePotionEffect(potionEffect.getType());
			}
		});
		if (shouldExecuteEffect()) {
			updateEffects();
		}
	}

}
