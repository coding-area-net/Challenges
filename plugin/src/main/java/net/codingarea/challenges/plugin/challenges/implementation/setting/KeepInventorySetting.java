package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public class KeepInventorySetting extends Setting {

  public KeepInventorySetting() {
    super(MenuType.SETTINGS, null, new ItemStack(Material.ENDER_EYE), "keep-inventory");
  }

  @Override
  protected void onEnable() {
    for (World world : Bukkit.getWorlds()) {
      world.setGameRule(GameRule.KEEP_INVENTORY, true);
    }
  }

  @Override
  protected void onDisable() {
    for (World world : Bukkit.getWorlds()) {
      world.setGameRule(GameRule.KEEP_INVENTORY, false);
    }
  }
}
