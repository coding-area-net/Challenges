package net.codingarea.challenges.plugin.challenges.custom.settings;

import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.ChallengeAction;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.impl.*;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl.*;
import net.codingarea.challenges.plugin.management.challenges.annotations.RequireVersion;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomSettingsLoader {

	@Getter
	private final Map<String, ChallengeTrigger> triggers;
	private final Map<String, ChallengeTrigger> fallbackTriggers;

	@Getter
	private final Map<String, ChallengeAction> actions;
	private final Map<String, ChallengeAction> fallbackActions;

	public CustomSettingsLoader() {
		actions = new LinkedHashMap<>();
		fallbackActions = new LinkedHashMap<>();
		triggers = new LinkedHashMap<>();
		fallbackTriggers = new LinkedHashMap<>();
	}

	public void enable() {
		loadTrigger();
		loadActions();
	}

	private void loadTrigger() {
		registerTriggers(
				new IntervallTrigger("intervall"),
				new PlayerJumpTrigger("jump"),
				new PlayerSneakTrigger("sneak"),
				new MoveBlockTrigger("move_block"),
				new BreakBlockTrigger("block_break"),
				new PlaceBlockTrigger("block_place"),
				new EntityDeathTrigger("death"),
				new EntityDamageTrigger("damage"),
				new EntityDamageByPlayerTrigger("damage_by_player"),
				new ConsumeItemTrigger("consume_item"),
				new PickupItemTrigger("pickup_item"),
				new DropItemTrigger("drop_item"),
				new AdvancementTrigger("advancement"),
				new HungerTrigger("hunger"),
				new MoveUpTrigger("move_up"),
				new MoveDownTrigger("move_down"),
				new MoveCameraTrigger("move_camera"),
				new StandsOnSpecificBlockTrigger("stands_on_specific_block"),
				new StandsNotOnSpecificBlockTrigger("stands_not_on_specific_block"),
				new GainXPTrigger("gain_xp"),
				new LevelUpTrigger("level_up"),
				new CraftItemTrigger("item_craft"),
				new InLiquidTrigger("in_liquid"),
				new GetItemTrigger("get_item")
		);
	}

	private void loadActions() {
		registerActions(
				new CancelEventAction("cancel"),
				new WinChallengeAction("win"),
				new ExecuteCommandAction("command"),
				new KillEntityAction("kill"),
				new DamageEntityAction("damage"),
				new HealEntityAction("heal"),
				new ModifyMaxHealthAction("max_health"),
				new HungerPlayerAction("hunger"),
				new SpawnEntityAction("spawn_entity"),
				new RandomMobAction("random_mob"),
				new RandomItemAction("random_item"),
				new UncraftInventoryAction("uncraft_inventory"),
				new BoostEntityInAirAction("boost_in_air"),
				new PotionEffectAction("potion_effect"),
				new AddPermanentEffectAction("permanent_effect"),
				new RandomPotionEffectAction("random_effect"),
				new ClearInventoryAction("clear_inventory"),
				new DropRandomItemAction("drop_random_item"),
				new RemoveRandomItemAction("remove_random_item"),
				new SwapRandomItemAction("swap_random_item"),
				new FreezeAction("freeze"),
				new InvertHealthAction("invert_health"),
				new WaterMLGAction("water_mlg"),
				new JumpAndRunAction("jnr"),
				new RandomHotBarAction("random_hotbar"),
				new ChangeWorldBorderAction("modify_border"),
				new SwapRandomMobAction("swap_mobs"),
				new PlaceStructureAction("place_structure")
		);
	}

	public void registerTriggers(ChallengeTrigger... trigger) {
		for (ChallengeTrigger trigger1 : trigger) {
			if (trigger1.getClass().isAnnotationPresent(RequireVersion.class)) {
				RequireVersion requireVersion = trigger1.getClass().getAnnotation(RequireVersion.class);
				MinecraftVersion minVersion = requireVersion.value();

				if (!MinecraftVersion.current().isNewerOrEqualThan(minVersion)) {
					Logger.debug("Did not register trigger {}, requires version {}, server running on {}", trigger1.getClass().getSimpleName(), minVersion, MinecraftVersion.current());
					continue;
				}
			}
			if (trigger1.getClass().isAnnotationPresent(FallbackNames.class)) {
				FallbackNames fallbackName = trigger1.getClass().getAnnotation(FallbackNames.class);
				String[] fallbackNames = fallbackName.value();

				for (String name : fallbackNames) {
					fallbackTriggers.put(name, trigger1);
				}
			}
			triggers.put(trigger1.getName(), trigger1);
			Bukkit.getPluginManager().registerEvents(trigger1, Challenges.getInstance());
		}
	}

	public void registerActions(ChallengeAction... action) {
		for (ChallengeAction action1 : action) {
			if (action1.getClass().isAnnotationPresent(RequireVersion.class)) {
				RequireVersion requireVersion = action1.getClass().getAnnotation(RequireVersion.class);
				MinecraftVersion minVersion = requireVersion.value();

				if (!MinecraftVersion.current().isNewerOrEqualThan(minVersion)) {
					Logger.debug("Did not register action {}, requires version {}, server running on {}", action1.getClass().getSimpleName(), minVersion, MinecraftVersion.current());
					continue;
				}
			}
			if (action1.getClass().isAnnotationPresent(FallbackNames.class)) {
				FallbackNames fallbackName = action1.getClass().getAnnotation(FallbackNames.class);
				String[] fallbackNames = fallbackName.value();

				for (String name : fallbackNames) {
					fallbackActions.put(name, action1);
				}
			}
			actions.put(action1.getName(), action1);
		}
	}

	@Nullable
	public ChallengeAction getActionByName(String name) {
		return actions.getOrDefault(name, fallbackActions.get(name));
	}

	@Nullable
	public ChallengeTrigger getTriggerByName(String name) {
		return triggers.getOrDefault(name, fallbackTriggers.get(name));
	}

}
