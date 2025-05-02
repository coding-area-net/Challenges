package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.commons.bukkit.utils.animation.AnimatedInventory;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.cloud.CloudSupportManager;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.management.stats.LeaderboardInfo;
import net.codingarea.challenges.plugin.management.stats.PlayerStats;
import net.codingarea.challenges.plugin.management.stats.Statistic;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder.SkullBuilder;
import net.codingarea.challenges.plugin.utils.misc.StatsHelper;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatsCommand implements PlayerCommand {

  private final Map<Player, Long> submitTimeByPlayer = new ConcurrentHashMap<>();

  @Override
  public void onCommand(@Nonnull Player player, @Nonnull String[] args) {
    if (!Challenges.getInstance().getStatsManager().isEnabled()) {
      Message.forName("feature-disabled").send(player, Prefix.CHALLENGES);
      SoundSample.BASS_OFF.play(player);
      return;
    } else if (!Challenges.getInstance().getStatsManager().hasDatabaseConnection()) {
      Message.forName("no-database-connection").send(player, Prefix.CHALLENGES);
      SoundSample.BASS_OFF.play(player);
      return;
    }

    if (System.currentTimeMillis() - submitTimeByPlayer.getOrDefault(player, System.currentTimeMillis() - 10 * 1000) < 5 * 1000) {
      SoundSample.BASS_OFF.play(player);
      return;
    }
    submitTimeByPlayer.put(player, System.currentTimeMillis());

    switch (args.length) {
      case 0:
        Message.forName("fetching-data").send(player, Prefix.CHALLENGES);
        handleCommand(player);
        break;
      case 1:
        Message.forName("fetching-data").send(player, Prefix.CHALLENGES);
        handleCommand(player, args[0]);
        break;
      default:
        Message.forName("syntax").send(player, Prefix.CHALLENGES, "stats [player]");
    }
  }

  private void handleCommand(@Nonnull Player player) {
    Challenges.getInstance().runAsync(() -> {
      open(player, player.getUniqueId(), player.getName());
    });
  }

  private void handleCommand(@Nonnull Player player, @Nonnull String name) {
    Challenges.getInstance().runAsync(() -> {
      Player target = Bukkit.getPlayer(name);
      if (target != null) {
        open(player, target.getUniqueId(), target.getName());
        return;
      }

      try {
        UUID uuid = Utils.fetchUUID(name);
        open(player, uuid, name);
      } catch (IOException ex) {
        player.sendMessage(Prefix.CHALLENGES + "§7Something went wrong");
      }
    });
  }

  private void open(@Nonnull Player player, @Nonnull UUID uuid, @Nonnull String name) {

    PlayerStats stats = Challenges.getInstance().getStatsManager().getStats(uuid, name);
    name = stats.getPlayerName();

    CloudSupportManager cloudSupport = Challenges.getInstance().getCloudSupportManager();
    String coloredName = cloudSupport.isNameSupport() && cloudSupport.hasNameFor(uuid) ? cloudSupport.getColoredName(uuid) : name;

    AnimatedInventory inventory = new AnimatedInventory(InventoryTitleManager.getStatsTitle(name), 5 * 9, MenuPosition.HOLDER);
    StatsHelper.setAccent(inventory, 3);
    inventory.cloneLastAndAdd().setItem(13, new SkullBuilder(Message.forName("stats-of").asString(coloredName)).setOwner(uuid, name).build());

    LeaderboardInfo info = Challenges.getInstance().getStatsManager().getLeaderboardInfo(uuid);
    createInventory(stats, info, inventory, StatsHelper.getSlots(2));

    MenuPosition.setEmpty(player);
    inventory.open(player, Challenges.getInstance());

    submitTimeByPlayer.remove(player);
  }

  private void createInventory(@Nonnull PlayerStats stats, @Nonnull LeaderboardInfo info, @Nonnull AnimatedInventory inventory, @Nonnull int... slots) {
    for (int i = 0; i < Statistic.values().length; i++) {
      Statistic statistic = Statistic.values()[i];
      double value = stats.getStatisticValue(statistic);
      String format = statistic.formatChat(value);

      ItemBuilder item = new ItemBuilder(StatsHelper.getMaterial(statistic), StatsHelper.getNameMessage(statistic).asString()).setLore(Message.forName("stats-display").asArray(format, info.getPlace(statistic))).hideAttributes();
      inventory.cloneLastAndAdd().setItem(slots[i], item);
    }
  }

}
