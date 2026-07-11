package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.FallbackNames;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SelectableKey;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.management.scheduler.policy.PlayerCountPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

@FallbackNames("intervall")
public class IntervallTrigger extends ChallengeTrigger {

  public IntervallTrigger(@NotNull String name) {
    super(name, SubSettingsBuilder.createChooseItem("time").fill(builder -> {
      builder.addSetting(SelectableKey.ofName("1", Material.MUSIC_DISC_13, "interval.second", "1"));
      builder.addSetting(SelectableKey.ofName("2", Material.MUSIC_DISC_CAT, "interval.seconds", "2"));
      builder.addSetting(SelectableKey.ofName("5", Material.MUSIC_DISC_BLOCKS, "interval.seconds", "5"));
      builder.addSetting(SelectableKey.ofName("10", Material.MUSIC_DISC_CHIRP, "interval.seconds", "10"));
      builder.addSetting(SelectableKey.ofName("20", Material.MUSIC_DISC_FAR, "interval.seconds", "20"));
      builder.addSetting(SelectableKey.ofName("30", Material.MUSIC_DISC_MALL, "interval.seconds", "30"));
      builder.addSetting(SelectableKey.ofName("60", Material.MUSIC_DISC_MELLOHI, "interval.seconds", "60"));
      builder.addSetting(SelectableKey.ofName("120", Material.MUSIC_DISC_STAL, "interval.minutes", "2"));
      builder.addSetting(SelectableKey.ofName("180", Material.MUSIC_DISC_STRAD, "interval.minutes", "3"));
      builder.addSetting(SelectableKey.ofName("240", Material.MUSIC_DISC_WARD, "interval.minutes", "4"));
      builder.addSetting(SelectableKey.ofName("300", Material.MUSIC_DISC_11, "interval.minutes", "5"));
    }));
    Challenges.getInstance().getScheduler().register(this);
  }

  @NotNull
  @Override
  public Material getMaterial() {
    return Material.CLOCK;
  }

  @ScheduledTask(ticks = 20, playerPolicy = PlayerCountPolicy.ALWAYS)
  public void onSecond() {
    long currentTime = Challenges.getInstance().getChallengeTimer().getTime();

    List<String> list = new LinkedList<>();
    list.add("1");


    if (currentTime % 2 == 0) {
      list.add("2");
    }
    if (currentTime % 5 == 0) {
      list.add("5");
    }
    if (currentTime % 10 == 0) {
      list.add("10");
    }
    if (currentTime % 20 == 0) {
      list.add("20");
    }
    if (currentTime % 30 == 0) {
      list.add("30");
    }
    if (currentTime % 60 == 0) {
      list.add("60");
    }
    if (currentTime % 120 == 0) {
      list.add("120");
    }
    if (currentTime % 180 == 0) {
      list.add("180");
    }
    if (currentTime % 180 == 0) {
      list.add("180");
    }
    if (currentTime % 300 == 0) {
      list.add("300");
    }

    createData()
      .data("time", list)
      .execute();
  }

}
