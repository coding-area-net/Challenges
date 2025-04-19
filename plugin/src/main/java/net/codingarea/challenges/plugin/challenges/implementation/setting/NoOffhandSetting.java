package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

@Since("2.0")
public class NoOffhandSetting extends Setting {

  public NoOffhandSetting() {
    super(MenuType.SETTINGS);
  }

  @Override
  protected void onEnable() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
      if (itemInOffHand.getType() == Material.AIR) return;
      player.getWorld().dropItemNaturally(player.getLocation(), itemInOffHand);
      player.getInventory().setItemInOffHand(null);
      player.updateInventory();
    }
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.SHIELD, Message.forName("item-no-offhand-setting"));
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getWhoClicked() instanceof Player)) return;
    Player player = (Player) event.getWhoClicked();
    if (ignorePlayer(player)) return;
    if (event.getClickedInventory() == null) return;
    if (event.getClickedInventory().getType() != InventoryType.PLAYER) return;
    if (event.getSlot() != 40) return;
    event.setCancelled(true);
  }

}
