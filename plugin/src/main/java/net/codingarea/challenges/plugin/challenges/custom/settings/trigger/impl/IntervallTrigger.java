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
      builder.addSetting(SelectableKey.of("1", Material.MUSIC_DISC_13, "item-custom-trigger-intervall-second", "1"));
      String seconds = "item-custom-trigger-intervall-seconds";
      builder.addSetting(SelectableKey.of("2", Material.MUSIC_DISC_CAT, seconds, "2"));
      builder.addSetting(SelectableKey.of("5", Material.MUSIC_DISC_BLOCKS, seconds, "5"));
      builder.addSetting(SelectableKey.of("10", Material.MUSIC_DISC_CHIRP, seconds, "10"));
      builder.addSetting(SelectableKey.of("20", Material.MUSIC_DISC_FAR, seconds, "20"));
      builder.addSetting(SelectableKey.of("30", Material.MUSIC_DISC_MALL, seconds, "30"));
      builder.addSetting(SelectableKey.of("60", Material.MUSIC_DISC_MELLOHI, seconds, "60"));
      String minutes = "item-custom-trigger-intervall-minutes";
      builder.addSetting(SelectableKey.of("120", Material.MUSIC_DISC_STAL, minutes, "2"));
      builder.addSetting(SelectableKey.of("180", Material.MUSIC_DISC_STRAD, minutes, "3"));
      builder.addSetting(SelectableKey.of("240", Material.MUSIC_DISC_WARD, minutes, "4"));
      builder.addSetting(SelectableKey.of("300", Material.MUSIC_DISC_11, minutes, "5"));
    }));
    Challenges.getInstance().getScheduler().register(this);
  }

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
