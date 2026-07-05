package net.codingarea.challenges.plugin.challenges.implementation.challenge.movement;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Since("2.0.2")
public class DontStopRunningChallenge extends SettingModifier {

  private static final int YELLOW = 5, RED = 3;

  private final Map<Player, Integer> playerStandingCount = new HashMap<>();

  public DontStopRunningChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.MOVEMENT, 3, 30, 10, new ItemStack(Material.SADDLE), "dont-stop-running");
  }

  @Override
  protected void onEnable() {
    bossbar.setContent((bossbar, player) -> {
      int count = playerStandingCount.getOrDefault(player, 1);
      int timeLeft = getValue() - count + 1;

      if (timeLeft <= RED) bossbar.setColor(BossBar.Color.RED);
      else if (timeLeft <= YELLOW) bossbar.setColor(BossBar.Color.YELLOW);
      else bossbar.setColor(BossBar.Color.GREEN);

      String time = "§e" + timeLeft + " §7" + (timeLeft == 1 ? Message.forName("second").asString() : Message.forName("seconds").asString());

      bossbar.setTitle(Message.forName("bossbar-dont-stop-running").asString(time));
    });
    bossbar.show();
  }

  @Override
  protected void onDisable() {
    bossbar.hide();
  }

  @ScheduledTask(ticks = 20)
  public void onSecond() {
    removeOfflinePlayers();
    countUpOrKillEveryone();
    bossbar.update();
  }

  private void removeOfflinePlayers() {
    for (Player player : new ArrayList<>(playerStandingCount.keySet())) {
      if (!player.isOnline() || ignorePlayer(player)) playerStandingCount.remove(player);
    }
  }

  private void countUpOrKillEveryone() {

    broadcastFiltered(player -> {
      Integer count = playerStandingCount.getOrDefault(player, 0);
      if (count >= getValue()) {
        Message.forName("stopped-moving").broadcast(Prefix.CHALLENGES, NameHelper.getName(player));
        playerStandingCount.remove(player);
        ChallengeHelper.kill(player);
        return;
      }
      playerStandingCount.put(player, count + 1);

    });

  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMove(@NotNull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getTo() == null) return;
    if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ() && event.getFrom().getY() == event.getTo().getY())
      return;
    playerStandingCount.remove(event.getPlayer());
  }

}
