package net.codingarea.challenges.plugin.challenges.implementation.challenge;

import net.codingarea.challenges.plugin.challenges.implementation.setting.MaxHealthSetting;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class ZeroHeartsChallenge extends SettingModifier {

  public ZeroHeartsChallenge() {
    super(MenuType.CHALLENGES, null, 5, 30, 10, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE), "zero-hearts");
  }

  @Override
  protected void onEnable() {
    bossbar.setContent((bossbar, player) -> {
      int currentTime = getCurrentTime();
      int maxTime = getValue() * 60;
      bossbar.setTitle(getChallengeMessageKey("bossbar"), maxTime - currentTime);
      bossbar.setColor(BossBar.Color.GREEN);
      bossbar.setProgress(1 - ((float) currentTime / maxTime));
    });
    bossbar.show();
    Bukkit.getOnlinePlayers().forEach(player -> {
      AttributeInstance attribute = player.getAttribute(MinecraftNameWrapper.MAX_HEALTH);
      if (attribute == null) return;
      attribute.setBaseValue(0);
    });
  }

  @Override
  protected void onDisable() {
    bossbar.hide();
    AbstractChallenge.getFirstInstance(MaxHealthSetting.class).onValueChange();
  }

  @Override
  protected void onValueChange() {
    bossbar.update();
  }

  @ScheduledTask(ticks = 20, async = false)
  public void onSecond() {
    broadcast(player -> {
      AttributeInstance attribute = player.getAttribute(MinecraftNameWrapper.MAX_HEALTH);
      if (attribute == null) return;
      attribute.setBaseValue(0);
    });
    int absorptionTime = getCurrentTime();
    if (absorptionTime >= getValue() * 60) {
      bossbar.hide();
      if (absorptionTime == getValue() * 60) {
        removeAbsorptionEffect();
      }
      return;
    }
    getGameStateData().set("absorption-time", absorptionTime + 1);
    applyAbsorptionEffect();
    bossbar.update();
  }

  private int getCurrentTime() {
    return getGameStateData().getInt("absorption-time");
  }

  private void applyAbsorptionEffect() {
    broadcastFiltered(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, (getValue() - getCurrentTime()) * 20 * 60, 1, true, false, false)));
  }

  private void removeAbsorptionEffect() {
    broadcastFiltered(player -> player.removePotionEffect(PotionEffectType.ABSORPTION));
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityPotionEffect(@NotNull EntityPotionEffectEvent event) {
    if (event.getAction() != Action.REMOVED) return;
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof Player player)) return;
    if (ignorePlayer(player)) return;
    ChallengeHelper.kill(player);
    MessageKey.of("zero-hearts-failed").broadcast(Prefix.CHALLENGES, player);
  }

}
