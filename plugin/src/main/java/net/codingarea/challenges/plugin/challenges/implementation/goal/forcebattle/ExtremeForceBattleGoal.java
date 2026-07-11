package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets.*;
import net.codingarea.challenges.plugin.challenges.type.abstraction.ForceBattleDisplayGoal;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.misc.BukkitReflectionUtils;
import net.codingarea.commons.common.config.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class ExtremeForceBattleGoal extends ForceBattleDisplayGoal<ForceTarget<?>> {

  private final BooleanSubSetting giveItemSetting, giveBlockSetting;

  public ExtremeForceBattleGoal() {
    super(new ItemStack(Material.BOOK), "extreme-force-battle");

    giveItemSetting = registerSetting("give-item", new BooleanSubSetting(
      new ItemStack(Material.CHEST), MessageKey.of("challenge.force-item-battle.sub.give-item"),
      false
    ));
    giveBlockSetting = registerSetting("give-block", new BooleanSubSetting(
      new ItemStack(Material.CHEST), MessageKey.of("challenge.force-block-battle.sub.give-block"),
      false
    ));
  }

  @Override
  protected ForceTarget<?>[] getTargetsPossibleToFind() {
    //Currently not needed as the setRandomTarget logic of the ForceBattleGoal class is not used
    return new ForceTarget<?>[0];
  }

  @ScheduledTask(ticks = 5, async = false, timerPolicy = TimerPolicy.STARTED)
  public void checkTargets() {
    for (Map.Entry<UUID, ForceTarget<?>> entry : currentTarget.entrySet()) {
      UUID uuid = entry.getKey();
      ForceTarget<?> target = entry.getValue();
      Player player = Bukkit.getPlayer(uuid);
      if (player == null || ignorePlayer(player)) continue;

      if (target.check(player)) {
        handleTargetFound(player);
      }
    }
  }

  @Override
  public ForceTarget<?> getTargetFromDocument(Document document, String path) {
    Document targetDocument = document.getDocument(path);
    String targetTypeString = targetDocument.getString("type");
    String value = targetDocument.getString("value");

    TargetType targetType = TargetType.valueOf(targetTypeString);
    return targetType.parse().apply(value);
  }

  @Override
  public List<ForceTarget<?>> getListFromDocument(Document document, String path) {
    List<Document> targetDocuments = document.getDocumentList(path);
    List<ForceTarget<?>> targets = new ArrayList<>();
    for (Document targetDocument : targetDocuments) {
      String targetTypeString = targetDocument.getString("type");
      Object value = targetDocument.getObject("value");

      TargetType targetType = TargetType.valueOf(targetTypeString);
      targets.add(targetType.parse().apply(value));
    }
    return targets;
  }

  @Override
  public void setTargetInDocument(Document document, String path, ForceTarget<?> target) {
    document.set(path, Document.create().set("type", target.getType().name()).set("value", target.getTargetSaveObject()));
  }

  @Override
  public void setFoundListInDocument(Document document, String path, List<ForceTarget<?>> targets) {
    List<Document> documents = new ArrayList<>();
    for (ForceTarget<?> forceTarget : targets) {
      documents.add(Document.create().set("type", forceTarget.getType().name()).set("value", forceTarget.getTargetSaveObject()));
    }
    document.set(path, documents);
  }

  @Override
  protected Message getLeaderboardTitleMessage() {
    return Message.forName("extreme-force-battle-leaderboard");
  }

  @Override
  public void setRandomTarget(Player player) {
    updateDisplayStand(player);

    TargetType targetType = globalRandom.choose(TargetType.values());
    ForceTarget<?> newTarget = targetType.getRandomTarget().apply(player);

    if (newTarget instanceof AdvancementTarget) {
      AdvancementProgress progress = player.getAdvancementProgress(((AdvancementTarget) newTarget).getTarget());
      progress.getAwardedCriteria().forEach(progress::revokeCriteria);
    }

    currentTarget.put(player.getUniqueId(), newTarget);
    getNewTargetMessage(newTarget)
      .send(player, Prefix.CHALLENGES, getTargetMessageReplacement(newTarget));
    SoundSample.PLING.play(player);

    if (scoreboard.isShown()) {
      scoreboard.update();
    }

  }

  @Override
  protected void setScoreboardContent() {
    scoreboard.setContent((board, _) -> {
      List<Player> ingamePlayers = ChallengeAPI.getIngamePlayers();
      int emptyLinesAvailable = board.getRemainingLines() - ingamePlayers.size();

      if (emptyLinesAvailable > 0) {
        board.addEmptyLine();
        emptyLinesAvailable--;
      }

      for (int i = 0; i < ingamePlayers.size() && i < 15; i++) {
        Player ingamePlayer = ingamePlayers.get(i);
        ForceTarget<?> target = currentTarget.get(ingamePlayer.getUniqueId());
        LocalizableMessage display = target == null ? MessageKey.of("none") : target.getScoreboardDisplayMessage().withArgs(getTargetName(target));
//        board.addLine(NameHelper.getName(ingamePlayer) + " §8» §e" + display); // TODO translation format
      }

      if (emptyLinesAvailable > 0) {
        board.addEmptyLine();
      }
    });
  }

  @Override
  protected boolean shouldRegisterDupedTargetsSetting() {
    return false;
  }

  @Override
  public void handleJokerUse(Player player) {
    ForceTarget<?> target = currentTarget.get(player.getUniqueId());
    if (shouldGiveItemOnSkip() && target instanceof ItemTarget itemTarget) {
      InventoryUtils.dropOrGiveItem(player.getInventory(), player.getLocation(), itemTarget.getTarget());
    } else if (shouldBlockOnSkip() && target instanceof BlockTarget blockTarget) {
      InventoryUtils.dropOrGiveItem(player.getInventory(), player.getLocation(), blockTarget.getTarget());
    }
    super.handleJokerUse(player);
  }

  public enum TargetType {
    ITEM(object -> {
      return new ItemTarget(Material.valueOf((String) object));
    }, player -> {
      return new ItemTarget(globalRandom.choose(ItemTarget.getPossibleItems()));
    }),
    BLOCK(object -> {
      return new BlockTarget(Material.valueOf((String) object));
    }, player -> {
      return new BlockTarget(globalRandom.choose(BlockTarget.getPossibleBlocks()));
    }),
    HEIGHT(object -> {
      return new HeightTarget(Integer.valueOf((String) object));
    }, player -> {
      World world = ChallengeAPI.getGameWorld(World.Environment.NORMAL);
      int height = globalRandom.range(BukkitReflectionUtils.getMinHeight(world), world.getMaxHeight());
      return new HeightTarget(height);
    }),
    MOB(object -> {
      return new MobTarget(EntityType.valueOf((String) object));
    }, player -> {
      return new MobTarget(globalRandom.choose(MobTarget.getPossibleMobs()));
    }),
    BIOME(object -> {
      return new BiomeTarget(Biome.valueOf((String) object));
    }, player -> {
      return new BiomeTarget(globalRandom.choose(BiomeTarget.getPossibleBiomes()));
    }),
    DAMAGE(object -> {
      return new DamageTarget(Integer.valueOf((String) object));
    }, player -> {
      return new DamageTarget(globalRandom.range(1, 19));
    }),
    ADVANCEMENT(object -> {
      return new AdvancementTarget(Bukkit.getAdvancement(Objects.requireNonNull(NamespacedKey.fromString((String) object))));
    }, player -> {
      return new AdvancementTarget(globalRandom.choose(AdvancementTarget.getPossibleAdvancements()));
    }),
    POSITION(object -> {
      Document document = (Document) object;
      return new PositionTarget(document.getDouble("x"), document.getDouble("z"));
    }, PositionTarget::new);

    private final Function<Object, ForceTarget<?>> parseFunction;
    private final Function<Player, ForceTarget<?>> randomTargetFunction;

    TargetType(Function<Object, ForceTarget<?>> parseFunction, Function<Player, ForceTarget<?>> randomTargetFunction) {
      this.parseFunction = parseFunction;
      this.randomTargetFunction = randomTargetFunction;
    }

    public Function<Object, ForceTarget<?>> parse() {
      return parseFunction;
    }

    public Function<Player, ForceTarget<?>> getRandomTarget() {
      return randomTargetFunction;
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onKill(@NotNull EntityDeathEvent event) {
    if (!shouldExecuteEffect()) return;
    LivingEntity entity = event.getEntity();
    Player killer = entity.getKiller();
    if (killer == null) return;
    if (ignorePlayer(killer)) return;
    if (currentTarget.get(killer.getUniqueId()) == null) return;
    if (currentTarget.get(killer.getUniqueId()) instanceof MobTarget) {
      MobTarget target = (MobTarget) currentTarget.get(killer.getUniqueId());
      if (target.getTarget() != entity.getType()) return;
      handleTargetFound(killer);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(@NotNull EntityDamageEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof Player)) return;
    Player player = (Player) event.getEntity();
    if (ignorePlayer(player)) return;
    if (currentTarget.get(player.getUniqueId()) == null) return;
    if (currentTarget.get(player.getUniqueId()) instanceof DamageTarget) {
      DamageTarget target = (DamageTarget) currentTarget.get(player.getUniqueId());
      int damage = (int) ChallengeHelper.getFinalDamage(event);
      if (damage != target.getTarget()) return;
      handleTargetFound(player);
    }
  }

  private boolean shouldGiveItemOnSkip() {
    return giveItemSetting.getAsBoolean();
  }

  private boolean shouldBlockOnSkip() {
    return giveBlockSetting.getAsBoolean();
  }

}
