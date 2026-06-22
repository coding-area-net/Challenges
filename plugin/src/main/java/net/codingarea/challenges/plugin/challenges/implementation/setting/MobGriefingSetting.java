package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MobGriefingSetting extends Setting {

  public MobGriefingSetting() {
    super(MenuType.SETTINGS, null, new ItemStack(Material.CREEPER_HEAD), "mob-griefing");
  }

  @Override
  protected void onEnable() {
    for (World world : Bukkit.getWorlds()) {
      world.setGameRule(GameRule.MOB_GRIEFING, false);
    }
  }

  @Override
  protected void onDisable() {
    for (World world : Bukkit.getWorlds()) {
      world.setGameRule(GameRule.MOB_GRIEFING, true);
    }
  }
}
