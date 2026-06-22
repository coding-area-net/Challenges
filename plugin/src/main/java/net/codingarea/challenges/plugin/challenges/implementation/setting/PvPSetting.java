package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.spigot.events.EntityDamageByPlayerEvent;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PvPSetting extends Setting {

  public PvPSetting() {
    super(MenuType.SETTINGS, null, true, new ItemStack(Material.STONE_SWORD), "pvp-setting");
  }

  @EventHandler
  public void onDamage(@NotNull EntityDamageByPlayerEvent event) {
    if (isEnabled()) return;
    if (!(event.getEntity() instanceof Player)) return;
    event.setCancelled(true);
  }

}
