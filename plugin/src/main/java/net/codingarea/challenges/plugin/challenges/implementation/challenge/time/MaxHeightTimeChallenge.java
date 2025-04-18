package net.codingarea.challenges.plugin.challenges.implementation.challenge.time;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Since("2.0")
public class MaxHeightTimeChallenge extends SettingModifier {

  public MaxHeightTimeChallenge() {
    super(MenuType.CHALLENGES, 3, 20);
    setCategory(SettingCategory.LIMITED_TIME);
  }

  @Override
  protected void onEnable() {
    bossbar.setContent((bossbar, player) -> {
      int currentTime = getCurrentTime(player);
      int maxTime = (getValue() * 60);
      bossbar.setTitle(Message.forName("bossbar-height-time-left").asString(player.getLocation().getBlockY(), maxTime - currentTime));
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

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.PARROT_SPAWN_EGG, Message.forName("item-max-height-time-challenge"));
  }

  @Nullable
  @Override
  protected String[] getSettingsDescription() {
    return Message.forName("item-time-seconds-description").asArray(getValue() * 60);
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeSecondsValueChangeTitle(this, getValue() * 60);
  }

  @EventHandler
  public void onPlayerMove(@Nonnull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getTo() == null) return;
    if (event.getFrom().getBlockY() == event.getTo().getBlockY()) return;
    Bukkit.getScheduler().runTask(plugin, () -> bossbar.update(event.getPlayer()));
  }

  @ScheduledTask(ticks = 20)
  public void onSecond() {
    broadcast(this::updateHeightTime);
  }

  private void updateHeightTime(@Nonnull Player player) {
    if (ignorePlayer(player)) {
      bossbar.update(player);
      return;
    }

    int time = getCurrentTime(player) + 1;
    if (time > getValue() * 60) {
      kill(player);
    }
    if (time < getValue() * 60) {
      getPlayerData(player).set(String.valueOf(player.getLocation().getBlockY()), time);
    }
    bossbar.update(player);
  }

  private int getCurrentTime(@Nonnull Player player) {
    return getPlayerData(player).getInt(String.valueOf(player.getLocation().getBlockY()), 0);
  }

}
