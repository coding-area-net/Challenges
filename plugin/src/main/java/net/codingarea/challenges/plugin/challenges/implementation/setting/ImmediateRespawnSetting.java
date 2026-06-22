package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class ImmediateRespawnSetting extends Setting {

  private boolean respawnWithEvent;

  public ImmediateRespawnSetting() {
    super(MenuType.SETTINGS, null, new ItemStack(Material.GOLDEN_APPLE), "item-immediate-respawn-setting");
  }

  @Override
  protected void onEnable() {
    if (respawnWithEvent) {
      return;
    }
    try {
      for (World world : Bukkit.getWorlds()) {
        world.setGameRule(MinecraftNameWrapper.IMMEDIATE_RESPAWN, true);
      }
    } catch (NoSuchFieldError ignored) {
      respawnWithEvent = true;
    }
  }

  @Override
  protected void onDisable() {
    if (respawnWithEvent) {
      return;
    }
    try {
      for (World world : Bukkit.getWorlds()) {
        world.setGameRule(MinecraftNameWrapper.IMMEDIATE_RESPAWN, false);
      }
    } catch (NoSuchFieldError ignored) {
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
    if (!isEnabled()) return;
    if (!respawnWithEvent) return;
    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> event.getEntity().spigot().respawn(), 1);
  }


}
