package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.codingarea.commons.bukkit.utils.misc.CompatibilityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.1.2")
public class HotBarRandomizerChallenge extends TimedChallenge {

  public HotBarRandomizerChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.RANDOMIZER, 1, 10, 5, new ItemStack(Material.HOPPER_MINECART), "hotbar-randomizer");
  }

  @Override
  public LocalizableMessage getSettingsDescription() {
    return ChallengeHelper.getSettingsDescriptionIntervalSeconds(getValue() * 60);
  }

  /**
   * @param force if true only sets items if inventory is empty
   */
  public static void addItems(Player player, boolean force) {

    if (!force && !player.getInventory().isEmpty()) {
      return;
    }

    player.getInventory().clear();
    for (int i = 0; i < 9; i++) {
      player.getInventory().setItem(i, InventoryUtils.getRandomItem(false, true));
    }
  }


  @Override
  protected int getSecondsUntilNextActivation() {
    return getValue() * 60;
  }

  @Override
  protected void onTimeActivation() {

    broadcastFiltered(player -> {
      addItems(player, true);
    });
    restartTimer();
  }

  @TimerTask(status = TimerStatus.RUNNING)
  public void onStart() {
    // Execute after hotbar items are removed
    Bukkit.getScheduler().runTask(plugin, () -> {
      broadcastFiltered(player -> {
        addItems(player, false);
      });

    });
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onJoin(PlayerJoinEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    addItems(event.getPlayer(), false);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBreak(BlockBreakEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    event.setDropItems(false);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onInventoryClick(@NotNull PlayerInventoryClickEvent event) {
    Player player = event.getPlayer();
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(player)) return;
    Inventory clickedInventory = event.getClickedInventory();
    if (event.getCursor() == null) return;
    if (clickedInventory == null) return;
    InventoryType type = CompatibilityUtils.getTopInventory(player).getType();
    if (type == InventoryType.WORKBENCH || type == InventoryType.CRAFTING) return;
    if (clickedInventory.getType() == InventoryType.CRAFTING) return;
    if (clickedInventory.getType() == InventoryType.PLAYER) {
      if (event.getInventory().getType() != InventoryType.PLAYER) {
        event.setCancelled(true);
      }
    }

  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerDropItem(@NotNull PlayerDropItemEvent event) {
    if (!shouldExecuteEffect()) return;
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityExplosion(EntityExplodeEvent event) {
    if (!shouldExecuteEffect()) return;
    for (Block block : event.blockList()) {
      block.setType(Material.AIR);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockExplosion(BlockExplodeEvent event) {
    if (!shouldExecuteEffect()) return;
    for (Block block : event.blockList()) {
      block.setType(Material.AIR);
    }
  }

}
