package net.codingarea.challenges.plugin.challenges.type.helper;

import lombok.Getter;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.challenges.type.IModifier;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.management.menu.generator.impl.challenge.ChallengesMenuGenerator;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.CheckReturnValue;
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
    Bukkit.getScheduler().runTask(Challenges.getInstance(), task);
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
    challenge.getType().executeWithGenerator(ChallengesMenuGenerator.class, gen -> gen.updateElementDisplay(challenge));
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

  public static Player getDamagerPlayer(@NotNull Entity damager) {
    if (damager instanceof Player) return ((Player) damager);
    if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player)
      return ((Player) ((Projectile) damager).getShooter());
    return null;
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
    playChallengeValueTitle(modifier, modifier.getValue());
  }

  public static void playChallengeValueTitle(@NotNull AbstractChallenge challenge, @NotNull Object value) {
    Challenges.getInstance().getTitleManager().sendChallengeValueTitle(challenge, value);
  }

  public static void playChallengeHeartsValueChangeTitle(@NotNull AbstractChallenge challenge, int health) {
    playChallengeValueTitle(challenge, (health / 2f) + " §c❤");
  }

  public static void playChallengeHeartsValueChangeTitle(@NotNull Modifier modifier) {
    playChallengeHeartsValueChangeTitle(modifier, modifier.getValue());
  }

  public static void playChallengeSecondsValueChangeTitle(@NotNull AbstractChallenge challenge, int seconds) {
    playChallengeValueTitle(challenge, Message.forName("subtitle-time-seconds").asString(seconds));
  }

  public static void playChallengeSecondsRangeValueChangeTitle(@NotNull AbstractChallenge challenge, int min, int max) {
    playChallengeValueTitle(challenge, Message.forName("subtitle-time-seconds-range").asString(min, max));
  }

  public static void playChallengeMinutesValueChangeTitle(@NotNull AbstractChallenge challenge, int seconds) {
    playChallengeValueTitle(challenge, Message.forName("subtitle-time-minutes").asString(seconds));
  }

  @NotNull
  public static String[] getTimeRangeSettingsDescription(@NotNull Modifier modifier, int multiplier, int range) {
    return Message.forName("item-time-seconds-range-description").asArray(modifier.getValue() * multiplier - range, modifier.getValue() * multiplier + range);
  }

  @NotNull
  public static String[] getTimeRangeSettingsDescription(@NotNull Modifier modifier, int range) {
    return getTimeRangeSettingsDescription(modifier, 1, range);
  }

  public static boolean finalDamageIsNull(@NotNull EntityDamageEvent event) {
    return getFinalDamage(event) == 0;
  }

  public static double getFinalDamage(@NotNull EntityDamageEvent event) {
    return event.getFinalDamage() + event.getDamage(DamageModifier.ABSORPTION);
  }

}
