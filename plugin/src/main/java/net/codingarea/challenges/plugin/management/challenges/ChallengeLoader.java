package net.codingarea.challenges.plugin.management.challenges;

import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.damage.*;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.effect.*;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.entities.*;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.extraworld.JumpAndRunChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.extraworld.WaterMLGChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.force.*;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory.*;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous.*;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.movement.*;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer.*;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.time.MaxBiomeTimeChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.time.MaxHeightTimeChallenge;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.world.*;
import net.codingarea.challenges.plugin.challenges.implementation.goal.*;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.*;
import net.codingarea.challenges.plugin.challenges.implementation.setting.*;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder.PotionBuilder;
import net.codingarea.challenges.plugin.utils.misc.ArmorUtils;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * This class loads all challenges of this plugin.
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
