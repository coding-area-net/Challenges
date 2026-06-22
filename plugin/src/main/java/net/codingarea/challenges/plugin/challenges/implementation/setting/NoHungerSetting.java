package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NoHungerSetting extends Setting {

  public NoHungerSetting() {
    super(MenuType.SETTINGS, null, new ItemStack(Material.BREAD), "no-hunger");
  }

  @Override
  protected void onEnable() {
    broadcastFiltered(this::feedPlayer);
  }

  @EventHandler(ignoreCancelled = true)
  public void onHunger(@NotNull FoodLevelChangeEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    if (!shouldExecuteEffect()) return;
    feedPlayer(((Player) event.getEntity()));
    event.setCancelled(true);
  }

  private void feedPlayer(@NotNull Player player) {
    player.setFoodLevel(20);
    player.setSaturation(20);
  }

}
