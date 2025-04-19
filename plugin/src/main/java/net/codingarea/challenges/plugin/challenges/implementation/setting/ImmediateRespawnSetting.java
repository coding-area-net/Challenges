package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nonnull;

@Since("2.0")
public class ImmediateRespawnSetting extends Setting {

  private boolean respawnWithEvent;

  public ImmediateRespawnSetting() {
    super(MenuType.SETTINGS);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.GOLDEN_APPLE, Message.forName("item-immediate-respawn-setting"));
  }

  @Override
  protected void onEnable() {
    if (respawnWithEvent) {
      return;
    }
    try {
      for (World world : Bukkit.getWorlds()) {
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
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
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
      }
    } catch (NoSuchFieldError ignored) {
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeath(@Nonnull PlayerDeathEvent event) {
    if (!isEnabled()) return;
    if (!respawnWithEvent) return;
    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> event.getEntity().spigot().respawn(), 1);
  }


}
