package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NoItemDamageSetting extends Setting {

  public NoItemDamageSetting() {
    super(MenuType.SETTINGS, null, new ItemStack(Material.ANVIL), "no-item-damage");
  }

  @EventHandler
  public void onItemDamage(PlayerItemDamageEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;

    event.setCancelled(true);
    event.getPlayer().updateInventory();
  }

}
