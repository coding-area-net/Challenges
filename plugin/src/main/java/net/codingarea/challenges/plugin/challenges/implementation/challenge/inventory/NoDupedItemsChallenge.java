package net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.CanInstaKillOnEnable;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import net.codingarea.commons.common.collection.pair.Triple;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Since("2.0")
@CanInstaKillOnEnable
public class NoDupedItemsChallenge extends Setting {

  public NoDupedItemsChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.INVENTORY, new ItemStack(Material.OBSERVER), "no-duped-items");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInventoryClick(@NotNull PlayerInventoryClickEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    checkInventories();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEntityPickUpItem(@NotNull PlayerPickupItemEvent event) {
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      if (!shouldExecuteEffect()) return;
      if (ignorePlayer(event.getPlayer())) return;
      checkInventories();
    }, 1);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInteract(@NotNull PlayerInteractEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getClickedBlock() == null) return;
    checkInventories();
  }

  private void checkInventories() {
    Map<Player, List<Material>> blackList = new HashMap<>();

    for (Player player : Bukkit.getOnlinePlayers()) {
      Triple<Player, Player, Material> result = checkInventory(player, blackList);
      if (result != null) {
        getChallengeMessageKey("failed").broadcast(
          Prefix.CHALLENGES,
          result.getFirst(),
          result.getSecond(),
          result.getThird()
        );
        ChallengeHelper.kill(result.getFirst());
        ChallengeHelper.kill(result.getSecond());
      }

    }

  }

  private Triple<Player, Player, Material> checkInventory(@NotNull Player player, Map<Player, List<Material>> blacklist) {
    List<Material> localBlacklist = new ArrayList<>();
    List<Material> playerBlacklist = blacklist.getOrDefault(player, new ArrayList<>());
    blacklist.put(player, playerBlacklist);

    for (ItemStack item : player.getInventory().getContents()) {
      if (item == null) continue;
      if (localBlacklist.contains(item.getType())) continue;
      localBlacklist.add(item.getType());

      for (Entry<Player, List<Material>> entry : blacklist.entrySet()) {
        List<Material> currentBlacklist = entry.getValue();
        if (currentBlacklist.contains(item.getType())) {
          return new Triple<>(player, entry.getKey(), item.getType());
        }

      }
      playerBlacklist.add(item.getType());
    }

    return null;
  }

}
