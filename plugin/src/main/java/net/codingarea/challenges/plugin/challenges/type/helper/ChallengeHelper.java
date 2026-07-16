package net.codingarea.challenges.plugin.challenges.type.helper;

import lombok.Getter;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.challenges.type.IModifier;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.content.i18n.ArgumentFormat;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.generator.IChallengesMenuGenerator;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.common.collection.pair.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public final class ChallengeHelper {

  @Getter
  private static boolean inInstantKill = false;

  private ChallengeHelper() {
  }

  public static void kill(@NotNull Player player) {
    if (runSyncIfConcurrent(() -> kill(player))) return;

    inInstantKill = true;
    player.damage(Integer.MAX_VALUE);
    inInstantKill = false;
  }

  public static void kill(@NotNull Player player, int delay) {
    Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), () -> kill(player), delay);
  }

  @CheckReturnValue
  public static boolean runSyncIfConcurrent(Runnable task) {
    if (Bukkit.isPrimaryThread()) {
      return false;
    }
    Bukkit.getScheduler().callSyncMethod(Challenges.getInstance(), () -> {
      task.run();
      return null;
    });
    return true;
  }

  public static void runSync(@NotNull Runnable task) {
    if (Bukkit.isPrimaryThread()) {
      task.run();
      return;
    }
    Bukkit.getScheduler().runTask(Challenges.getInstance(), task);
  }

  public static void updateItems(@NotNull IChallenge challenge) {
    challenge.getType().executeWithGenerator(IChallengesMenuGenerator.class, gen -> gen.updateElementDisplay(challenge));
  }

  public static void handleModifierClick(@NotNull MenuClickInfo info, @NotNull IModifier modifier) {
    int newValue = modifier.getValue();
    int amount = info.isShiftClick()
      ? (modifier.getValue() == modifier.getMinValue() || info.isRightClick() && modifier.getValue() == (10 - (modifier.getMinValue() - 1)) ? 9 : 10)
      : 1;
    newValue += info.isRightClick() ? -amount : amount;

    if (newValue > modifier.getMaxValue())
      newValue = modifier.getMinValue();
    if (newValue < modifier.getMinValue())
      newValue = modifier.getMaxValue();

    modifier.setValue(newValue);
    modifier.playValueChangeTitle();
    SoundSample.CLICK.play(info.getPlayer());
  }

  @NotNull
  @Deprecated
  public static String getColoredChallengeName(@NotNull AbstractChallenge challenge) {
    return challenge.getChallengeName().getLocalizableKey().getKey();
  }

  public static void breakBlock(@NotNull Block block, @Nullable ItemStack tool) {
    breakBlock(block, tool, null);
  }

  public static void breakBlock(@NotNull Block block, @Nullable ItemStack tool, @Nullable Inventory targetInventory) {

    if (!ChallengeAPI.getDropChance(block.getType())) return;
    boolean putIntoInventory = ChallengeAPI.getItemsDirectIntoInventory() && targetInventory != null;

    List<Material> customDrops = ChallengeAPI.getCustomDrops(block.getType());
    Location location = block.getLocation().clone().add(0.5, 0, 0.5);
    if (!customDrops.isEmpty()) {
      if (putIntoInventory) {
        customDrops.forEach(drop -> InventoryUtils.dropOrGiveItem(targetInventory, location, drop));
      } else {
        customDrops.forEach(drop -> block.getWorld().dropItem(location, new ItemStack(drop)));
      }
      block.setType(Material.AIR);
      return;
    }

    if (putIntoInventory) {
      block.getDrops(tool).forEach(drop -> InventoryUtils.dropOrGiveItem(targetInventory, location, drop));
      block.setType(Material.AIR);
      return;
    }

    block.breakNaturally(tool);

  }

  public static void dropItem(@NotNull ItemStack itemStack, @NotNull Location dropLocation, @NotNull Inventory inventory) {
    boolean directIntoInventory = Challenges.getInstance().getBlockDropManager().isItemsDirectIntoInventory();

    if (directIntoInventory) {
      InventoryUtils.dropOrGiveItem(inventory, dropLocation, itemStack);
      return;
    }

    if (dropLocation.getWorld() == null) return;
    dropLocation.getWorld().dropItemNaturally(dropLocation, itemStack);

  }

  public static boolean ignoreDamager(@NotNull Entity damager) {
    Player damagerPlayer = getDamagerPlayer(damager);
    if (damagerPlayer == null) return false;
    return AbstractChallenge.ignorePlayer(damagerPlayer);
  }

  @Nullable
  public static Player getDamagerPlayer(@NotNull Entity damager) {
    return switch (damager) {
      case Player player -> player;
      case Projectile projectile when projectile.getShooter() instanceof Player shooter -> shooter;
      case TNTPrimed tnt when tnt.getSource() instanceof Player source -> source;
      default -> null;
    };
  }

  public static List<Player> getIngamePlayers() {
    return Bukkit.getOnlinePlayers().stream().filter(player -> !AbstractChallenge.ignorePlayer(player)).collect(Collectors.toList());
  }

  public static void playChallengeToggleTitle(@NotNull AbstractChallenge challenge) {
    playChallengeToggleTitle(challenge, challenge.isEnabled());
  }

  public static void playChallengeToggleTitle(@NotNull AbstractChallenge challenge, boolean enabled) {
    Challenges.getInstance().getTitleManager().sendChallengeToggleTitle(challenge, enabled);
  }

  public static void playGoalToggleTitle(@NotNull IGoal goal) {
    playGoalToggleTitle(goal, goal.isEnabled());
  }

  public static void playGoalToggleTitle(@NotNull IGoal goal, boolean enabled) {
    Challenges.getInstance().getTitleManager().sendGoalToggleTitle(goal, enabled);
  }

  public static <T extends AbstractChallenge & IModifier> void playChallengeValueTitle(@NotNull T modifier) {
    LocalizableMessage description = modifier.getSettingsDescription();
    playChallengeValueTitle(modifier, description != null ? description : modifier.getSettingsName()); // TODO or #getValue instead of#getSettingsName
  }

  public static void playChallengeValueTitle(@NotNull AbstractChallenge challenge, @NotNull Object value) {
    Challenges.getInstance().getTitleManager().sendChallengeValueTitle(challenge, value);
  }

  @Deprecated
  public static void playChallengeHeartsValueChangeTitle(@NotNull AbstractChallenge challenge, int health) {
    playChallengeValueTitle(challenge, (health / 2f) + " §c❤");
  }

  @Deprecated
  public static void playChallengeHeartsValueChangeTitle(@NotNull Modifier modifier) {
    playChallengeHeartsValueChangeTitle(modifier, modifier.getValue());
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getSettingsDescriptionModifierValue(int value) {
    return MessageKey.of("challenge.settings-modifier-value").withArgs(value);
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getSettingsDescriptionModifierMultiplier(int value) {
    return MessageKey.of("challenge.settings-modifier-multiplier").withArgs(value);
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getSettingsDescriptionModifierStrength(int value) {
    return MessageKey.of("challenge.settings-modifier-strength").withArgs(value);
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getSettingsDescriptionIntervalSecondsRange(int baseSeconds, int rangeSecondsAround) {
    return MessageKey.of("challenge.settings-modifier-interval").withArgs(ArgumentFormat.SECONDS_RANGE.apply(Tuple.of(baseSeconds, rangeSecondsAround)));
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getSettingsDescriptionIntervalSeconds(int timeInSeconds) {
    return MessageKey.of("challenge.settings-modifier-interval").withArgs(ArgumentFormat.TIME.apply(timeInSeconds));
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getSettingsDescriptionTimeSeconds(int timeInSeconds) {
    return MessageKey.of("challenge.settings-modifier-time").withArgs(ArgumentFormat.TIME.apply(timeInSeconds));
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getSettingsDescriptionTimeSecondsRange(int baseSeconds, int rangeSecondsAround) {
    return MessageKey.of("challenge.settings-modifier-time").withArgs(ArgumentFormat.SECONDS_RANGE.apply(Tuple.of(baseSeconds, rangeSecondsAround)));
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getSettingsDescriptionMaxHealth(int hp) {
    return MessageKey.of("challenge.settings-modifier-max-health").withArgs(ArgumentFormat.HP.apply(hp));
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getSettingsDescriptionDamage(int hp) {
    return MessageKey.of("challenge.settings-modifier-damage").withArgs(ArgumentFormat.HP.apply(hp));
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getSettingsDescriptionRadiusChunks(int chunks) {
    // TODO refactor
    return MessageKey.of("challenge.settings-modifier-radius")
      .withArgs(MessageKey.of("arg-format.radius").withArgs(chunks, MessageKey.pluralize("generic.chunk", chunks)));
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getSettingsDescriptionRadiusBlocks(int blocks) {
    // TODO refactor
    return MessageKey.of("challenge.settings-modifier-radius")
      .withArgs(MessageKey.of("arg-format.radius").withArgs(blocks, MessageKey.pluralize("generic.block", blocks)));
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getChallengeEnabledName() {
    return MessageKey.of("generic.enabled");
  }

  @NotNull
  @Contract(pure = true)
  public static LocalizableMessage getChallengeDisabledName() {
    return MessageKey.of("generic.disabled");
  }

  public static boolean finalDamageIsNull(@NotNull EntityDamageEvent event) {
    return getFinalDamage(event) == 0;
  }

  public static double getFinalDamage(@NotNull EntityDamageEvent event) {
    return event.getFinalDamage() + event.getDamage(DamageModifier.ABSORPTION);
  }

}
