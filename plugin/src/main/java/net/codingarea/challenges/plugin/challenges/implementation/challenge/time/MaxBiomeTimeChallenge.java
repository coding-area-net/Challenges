package net.codingarea.challenges.plugin.challenges.implementation.challenge.time;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class MaxBiomeTimeChallenge extends SettingModifier {

  public MaxBiomeTimeChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.LIMITED_TIME, 3, 20, new ItemStack(Material.SPRUCE_SAPLING), "max-biome-time");
  }

  @Override
  protected void onEnable() {
    bossbar.setContent((bossbar, player) -> {
      int currentTime = getCurrentTime(player);
      int maxTime = (getValue() * 60);
      bossbar.setTitle(Message.forName("bossbar-biome-time-left").asComponent(getBiome(player), maxTime - currentTime));
      bossbar.setColor(BarColor.GREEN);
      bossbar.setProgress(1 - ((float) currentTime / maxTime));
    });
    bossbar.show();
  }

  @Override
  protected void onDisable() {
    bossbar.hide();
  }

  @Override
  protected void onValueChange() {
    bossbar.update();
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-time-seconds-description").asArray(getValue() * 60);
//  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeSecondsValueChangeTitle(this, getValue() * 60);
  }

  @EventHandler
  public void onPlayerMove(@NotNull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getTo() == null) return;
    if (event.getFrom().getBlock().getBiome() == event.getTo().getBlock().getBiome()) return;
    bossbar.update(event.getPlayer());
  }

  @ScheduledTask(ticks = 20)
  public void onSecond() {
    broadcast(this::updateBiomeTime);
  }

  private void updateBiomeTime(@NotNull Player player) {
    if (ignorePlayer(player)) {
      bossbar.update(player);
      return;
    }

    Biome biome = player.getLocation().getBlock().getBiome();
    int time = getCurrentTime(player) + 1;
    if (time > getValue() * 60) {
      ChallengeHelper.kill(player);
    }
    if (time < getValue() * 60) {
      getPlayerData(player).set(biome.name(), time);
    }
    bossbar.update(player);
  }

  private int getCurrentTime(@NotNull Player player) {
    return getPlayerData(player).getInt(getBiome(player).name(), 0);
  }

  private Biome getBiome(@NotNull Player player) {
    return player.getLocation().getBlock().getBiome();
  }

}
