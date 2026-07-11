package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.utils.misc.PotionEffectUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PlayerGlowSetting extends Setting {

  public PlayerGlowSetting() {
    super(MenuType.SETTINGS, null, new ItemStack(Material.GLASS_BOTTLE), "glow");
  }

  @Override
  protected void onEnable() {
    updateEffects();
  }

  @Override
  protected void onDisable() {
    updateEffects();
  }

  @TimerTask(status = {TimerStatus.PAUSED, TimerStatus.RUNNING}, async = false)
  @ScheduledTask(ticks = 50, async = false, timerPolicy = TimerPolicy.ALWAYS)
  public void updateEffects() {
    if (!shouldExecuteEffect()) {
      broadcast(player -> player.removePotionEffect(PotionEffectType.GLOWING));
      return;
    }
    broadcast(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffectUtils.INFINITE_DURATION, 1, true, false, false)));
  }

  @EventHandler
  public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
    updateEffects();
  }

}
