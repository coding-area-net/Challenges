package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.scheduler.policy.PlayerCountPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;

import java.util.LinkedList;
import java.util.List;

public class IntervallTrigger extends ChallengeTrigger {

  public IntervallTrigger(String name) {
    super(name, SubSettingsBuilder.createChooseItem("time").fill(builder -> {
      builder.addSetting("1", new ItemBuilder(Material.MUSIC_DISC_13, Message.forName("item-custom-trigger-intervall-second"), "1").build());
      String seconds = "item-custom-trigger-intervall-seconds";
      builder.addSetting("2", new ItemBuilder(Material.MUSIC_DISC_CAT, Message.forName(seconds), "2"));
      builder.addSetting("5", new ItemBuilder(Material.MUSIC_DISC_BLOCKS, Message.forName(seconds), "5"));
      builder.addSetting("10", new ItemBuilder(Material.MUSIC_DISC_CHIRP, Message.forName(seconds), "10"));
      builder.addSetting("20", new ItemBuilder(Material.MUSIC_DISC_FAR, Message.forName(seconds), "20"));
      builder.addSetting("30", new ItemBuilder(Material.MUSIC_DISC_MALL, Message.forName(seconds), "30"));
      builder.addSetting("60", new ItemBuilder(Material.MUSIC_DISC_MELLOHI, Message.forName(seconds), "60"));
      String minutes = "item-custom-trigger-intervall-minutes";
      builder.addSetting("120", new ItemBuilder(Material.MUSIC_DISC_STAL, Message.forName(minutes), "2"));
      builder.addSetting("180", new ItemBuilder(Material.MUSIC_DISC_STRAD, Message.forName(minutes), "3"));
      builder.addSetting("240", new ItemBuilder(Material.MUSIC_DISC_WARD, Message.forName(minutes), "4"));
      builder.addSetting("300", new ItemBuilder(Material.MUSIC_DISC_11, Message.forName(minutes), "5"));
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
