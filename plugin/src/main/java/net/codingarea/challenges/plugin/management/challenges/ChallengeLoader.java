package net.codingarea.challenges.plugin.management.challenges;

import net.anweisen.utilities.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.damage.*;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.effect.BlockEffectChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.effect.ChunkRandomEffectChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.effect.EntityRandomEffectChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.effect.InfectionChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.effect.PermanentEffectOnDamageChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.effect.RandomPotionEffectChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.entities.AllMobsToDeathPoint;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.entities.BlockMobsChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.entities.DupedSpawningChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.entities.HydraNormalChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.entities.HydraPlusChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.entities.InvisibleMobsChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.entities.MobSightDamageChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.entities.MobTransformationChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.entities.MobsRespawnInEndChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.entities.NewEntityOnJumpChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.entities.StoneSightChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.extraworld.JumpAndRunChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.extraworld.WaterMLGChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.force.ForceBiomeChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.force.ForceBlockChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.force.ForceHeightChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.force.ForceItemChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.force.ForceMobChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory.DamageInventoryClearChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory.MissingItemsChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory.MovementItemRemovingChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory.NoDupedItemsChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory.PermanentItemChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory.PickupItemLaunchChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory.UncraftItemsChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous.EnderGamesChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous.FoodLaunchChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous.FoodOnceChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous.InvertHealthChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous.LowDropRateChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous.NoExpChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous.NoSharedAdvancementsChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous.NoTradingChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous.OneDurabilityChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.movement.AlwaysRunningChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.movement.DontStopRunningChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.movement.FiveHundredBlocksChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.movement.HigherJumpsChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.movement.HungerPerBlockChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.movement.MoveMouseDamage;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.movement.OnlyDirtChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.movement.OnlyDownChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.movement.TrafficLightChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.BlockRandomizerChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.CraftingRandomizerChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.EntityLootRandomizerChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.HotBarRandomizerChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.MobRandomizerChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.RandomChallengeChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.RandomEventChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.RandomItemChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.RandomItemDroppingChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.RandomItemRemovingChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.RandomItemSwappingChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.RandomTeleportOnHitChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.RandomizedHPChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.time.MaxBiomeTimeChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.time.MaxHeightTimeChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.AllBlocksDisappearChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.AnvilRainChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.BedrockPathChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.BedrockWallChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.BlockFlyInAirChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.BlocksDisappearAfterTimeChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.ChunkDeconstructionChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.ChunkDeletionChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.FloorIsLavaChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.IceFloorChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.LevelBorderChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.LoopChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.RepeatInChunkChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.SnakeChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.SurfaceHoleChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.TsunamiChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.goal.*;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.*;
import net.codingarea.challenges.plugin.challenges.implementation.setting.*;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder.PotionBuilder;
import net.codingarea.challenges.plugin.utils.misc.ArmorUtils;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * This class loads all challenges of this plugin.
 * In pre2 versions, this class was known as PluginChallengeLoader.
 *
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @autho<a href="r">KxmischesDomi | https://github</a>.com/kxmischesdomi
 * @since 2.0
 */
public final class ChallengeLoader extends ModuleChallengeLoader {

	public ChallengeLoader() {
		super(Challenges.getInstance());
	}

	public void enable() {

		// Settings
		registerWithCommand(DifficultySetting.class, "difficulty");
		register(RegenerationSetting.class);
		register(OneTeamLifeSetting.class);
		register(RespawnSetting.class);
		register(SplitHealthSetting.class);
		register(DamageDisplaySetting.class);
		register(LanguageSetting.class);

		register(PregameMovementSetting.class);
		register(DeathMessageSetting.class);
		register(HealthDisplaySetting.class);
		registerWithCommand(PositionSetting.class, "position");
		register(DeathPositionSetting.class);
		register(PlayerGlowSetting.class);
		register(NoHungerSetting.class);

		register(NoItemDamageSetting.class);
		register(MobGriefingSetting.class);
		register(KeepInventorySetting.class);
		registerWithCommand(BackpackSetting.class, "backpack");
		registerWithCommand(EnderChestCommandSetting.class, "enderchest");
		register(TimberSetting.class);
		register(PvPSetting.class);

		register(NoHitDelaySetting.class);
		registerWithCommand(TopCommandSetting.class, "top");
		register(MaxHealthSetting.class);
		register(DamageMultiplierModifier.class);
		register(CutCleanSetting.class);
		register(FortressSpawnSetting.class);
		register(BastionSpawnSetting.class);

		register(NoOffhandSetting.class);
		register(ImmediateRespawnSetting.class);

		register(SlotLimitSetting.class);
		register(OldPvPSetting.class);
		register(TotemSaveDeathSetting.class);
		register(SoupSetting.class);


		// Challenges

		// Randomizer
		register(RandomChallengeChallenge.class);
		register(RandomizedHPChallenge.class);
		register(BlockRandomizerChallenge.class);
		register(CraftingRandomizerChallenge.class);
		register(HotBarRandomizerChallenge.class);
		registerWithCommand(EntityLootRandomizerChallenge.class, "searchloot");
		register(MobRandomizerChallenge.class);
		register(RandomItemDroppingChallenge.class);
		register(RandomItemRemovingChallenge.class);
		register(RandomItemSwappingChallenge.class);
		register(RandomItemChallenge.class);
		register(RandomEventChallenge.class);
		register(RandomTeleportOnHitChallenge.class);

		// Force
		register(ForceHeightChallenge.class);
		register(ForceBlockChallenge.class);
		register(ForceMobChallenge.class);
		register(ForceItemChallenge.class);
		register(ForceBiomeChallenge.class);

		// Entities
		register(HydraNormalChallenge.class);
		register(HydraPlusChallenge.class);
		register(DupedSpawningChallenge.class);
		register(NewEntityOnJumpChallenge.class);
		register(InvisibleMobsChallenge.class);
		register(StoneSightChallenge.class);
		register(MobSightDamageChallenge.class);
		register(AllMobsToDeathPoint.class);
		register(MobsRespawnInEndChallenge.class);
		register(MobTransformationChallenge.class);
		register(BlockMobsChallenge.class);

		// Damage
		register(DamagePerBlockChallenge.class);
		register(SneakDamageChallenge.class);
		register(JumpDamageChallenge.class);
		register(BlockBreakDamageChallenge.class);
		register(BlockPlaceDamageChallenge.class);
		register(AdvancementDamageChallenge.class);
		register(DamagePerItemChallenge.class);
		register(WaterAllergyChallenge.class);
		register(DeathOnFallChallenge.class);
		register(ReversedDamageChallenge.class);
		register(FreezeChallenge.class);
		register(DelayDamageChallenge.class);

		// Effect
		register(ChunkRandomEffectChallenge.class);
		register(BlockEffectChallenge.class);
		register(EntityRandomEffectChallenge.class);
		register(RandomPotionEffectChallenge.class);
		register(PermanentEffectOnDamageChallenge.class);
		register(InfectionChallenge.class);

		// World
		register(SurfaceHoleChallenge.class);
		register(BedrockWallChallenge.class);
		register(BedrockPathChallenge.class);
		register(FloorIsLavaChallenge.class);
		register(ChunkDeconstructionChallenge.class);
		register(AllBlocksDisappearChallenge.class);
		register(AnvilRainChallenge.class);
		register(TsunamiChallenge.class);
		register(RepeatInChunkChallenge.class);
		register(SnakeChallenge.class);
		register(BlockFlyInAirChallenge.class);
		register(BlocksDisappearAfterTimeChallenge.class);
		register(LoopChallenge.class);
		register(IceFloorChallenge.class);
		register(LevelBorderChallenge.class);
		register(ChunkDeletionChallenge.class);

		// Inventory
		register(PermanentItemChallenge.class);
		register(NoDupedItemsChallenge.class);
		register(DamageInventoryClearChallenge.class);
		register(UncraftItemsChallenge.class);
		registerWithCommand(MissingItemsChallenge.class, "openmissingitems");
		register(PickupItemLaunchChallenge.class);
		register(MovementItemRemovingChallenge.class);

		// Movement
		register(TrafficLightChallenge.class);
		register(HungerPerBlockChallenge.class);
		register(OnlyDownChallenge.class);
		register(OnlyDirtChallenge.class);
		register(HigherJumpsChallenge.class);
		register(AlwaysRunningChallenge.class);
		register(DontStopRunningChallenge.class);
		register(MoveMouseDamage.class);
		register(FiveHundredBlocksChallenge.class);

		// Limited Time
		register(MaxBiomeTimeChallenge.class);
		register(MaxHeightTimeChallenge.class);

		// Custom World
		register(WaterMLGChallenge.class);
		register(JumpAndRunChallenge.class);

		// Misc
		register(OneDurabilityChallenge.class);
		register(NoTradingChallenge.class);
		register(NoExpChallenge.class);
		register(FoodOnceChallenge.class);
		register(FoodLaunchChallenge.class);
		register(LowDropRateChallenge.class);
		register(EnderGamesChallenge.class);
		register(InvertHealthChallenge.class);
		register(NoSharedAdvancementsChallenge.class);


		// Goal

		// Kill
		register(KillEnderDragonGoal.class);
		register(KillWitherGoal.class);
		register(KillElderGuardianGoal.class);
		register(KillWardenGoal.class);
		register(KillAllBossesGoal.class);
		register(KillAllBossesNewGoal.class);
		register(KillIronGolemGoal.class);
		register(KillSnowGolemGoal.class);
		register(KillAllMobsGoal.class);
		register(KillAllMonsterGoal.class);

		// Score Points
		register(CollectMostDeathsGoal.class);
		register(CollectMostItemsGoal.class);
		register(MineMostBlocksGoal.class);
		register(CollectMostExpGoal.class);
		register(MostEmeraldsGoal.class);
		register(MostOresGoal.class);
		register(EatMostGoal.class);

		// Fastest Time
		register(FirstOneToDieGoal.class);
		register(CollectWoodGoal.class);
		register(FinishRaidGoal.class);
		register(AllAdvancementGoal.class);
		register(MaxHeightGoal.class);
		register(MinHeightGoal.class);
		register(RaceGoal.class);
		register(FindElytraGoal.class);
		register(EatCakeGoal.class);
		register(CollectHorseAmorGoal.class);
		register(CollectIceBlocksGoal.class);
		register(CollectSwordsGoal.class);
		register(CollectWorkstationsGoal.class);
		register(GetFullHealthGoal.class);

		// Force battle
		register(ForceItemBattleGoal.class);
		register(ForceMobBattleGoal.class);
		register(ForceAdvancementBattleGoal.class);
		register(ForceBlockBattleGoal.class);
		register(ForceBiomeBattleGoal.class);
		register(ForceDamageBattleGoal.class);
		register(ForceHeightBattleGoal.class);
		register(ForcePositionBattleGoal.class);
		register(ExtremeForceBattleGoal.class);

		// Misc
		register(LastManStandingGoal.class);
		registerWithCommand(CollectAllItemsGoal.class, "skipitem");


		// Damage Rules
		registerDamageRule("none", Material.TOTEM_OF_UNDYING, DamageCause.values());
		registerDamageRule("fire", Material.LAVA_BUCKET, DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA, DamageCause.HOT_FLOOR);
		registerDamageRule("attack", Material.DIAMOND_SWORD, DamageCause.ENTITY_ATTACK, DamageCause.ENTITY_SWEEP_ATTACK, DamageCause.ENTITY_EXPLOSION, DamageCause.THORNS);
		registerDamageRule("projectile", Material.ARROW, DamageCause.PROJECTILE);
		registerDamageRule("fall", Material.FEATHER, DamageCause.FALL);
		registerDamageRule("explosion", Material.TNT, DamageCause.ENTITY_EXPLOSION, DamageCause.BLOCK_EXPLOSION);
		registerDamageRule("drowning", PotionBuilder.createWaterBottle(), DamageCause.DROWNING);
		registerDamageRule("block", Material.SAND, DamageCause.FALLING_BLOCK, DamageCause.SUFFOCATION, DamageCause.CONTACT);
		registerDamageRule("magic", Material.BREWING_STAND, DamageCause.MAGIC, DamageCause.POISON, DamageCause.WITHER);

		if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_17)) {
			registerDamageRule("freeze", Material.POWDER_SNOW_BUCKET, DamageCause.FREEZE);
		}

		// Material Rules
		registerMaterialRule("§cArmor", "Armor", ArmorUtils.getArmor());
		registerMaterialRule("§6Golden Apple", "Golden Apple", Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE);
		registerMaterialRule("§6Crafting Table", "Crafting Table", Material.CRAFTING_TABLE);
		registerMaterialRule("§6Chest", "Chest", Material.CHEST);
		registerMaterialRule("§cFurnace", "Furnace", Material.FURNACE, Material.FURNACE_MINECART);
		registerMaterialRule("§5Enchanting Table", "Enchanting Table", Material.ENCHANTING_TABLE);
		registerMaterialRule("§cAnvil", "Anvil", Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL);
		registerMaterialRule("§dBrewing Stand", "Brewing Stand", Material.BREWING_STAND);
		registerMaterialRule("§cBow", "Bow", Material.BOW);
		registerMaterialRule("§fSnowball", "Snowball", Material.SNOWBALL);
		registerMaterialRule("§cFlint and Steel", "Flint and Steel", Material.FLINT_AND_STEEL);
		registerMaterialRule("§cBucket", "Bucket", Material.BUCKET);
	}

}
