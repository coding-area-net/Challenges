package net.codingarea.challenges.plugin.utils.misc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class CommandHelper {

  private CommandHelper() {
  }

  public static List<String> getCompletions(CommandSender sender) {
    LinkedList<String> list = new LinkedList<>();
    Player player = sender instanceof Player ? ((Player) sender) : null;
    Bukkit.getOnlinePlayers().forEach(player1 -> {
      if (player == null || player.canSee(player1)) {
        list.add(player1.getName());
      }
    });
    list.addAll(Arrays.asList("@a", "@r", "@p", "@s"));
    return list;
  }

  public static List<Player> getPlayers(@Nonnull CommandSender sender, @Nonnull String input) {
    ArrayList<Player> list = new ArrayList<>();
    Location senderLocation = getSenderLocation(sender);

    switch (input) {
      case "@a": {
        list.addAll(Bukkit.getOnlinePlayers());
        break;
      }
      case "@r": {
        list.add(new ArrayList<Player>(Bukkit.getOnlinePlayers()).get(ThreadLocalRandom.current().nextInt(Bukkit.getOnlinePlayers().size())));
        break;
      }
      case "@p": {
        if (senderLocation != null) {
          Player nearestPlayer = getNearestPlayer(senderLocation);
          if (nearestPlayer != null) list.add(nearestPlayer);
        }
        break;
      }
      case "@s": {
        if (sender instanceof Player) list.add(((Player) sender));
        break;
      }
      default: {
        Player player = Bukkit.getPlayer(input);
        if (player != null) list.add(player);
      }

    }

    return list;
  }

  @Nullable
  public static Location getSenderLocation(@Nonnull CommandSender sender) {
    if (sender instanceof Player) return ((Player) sender).getLocation();
    if (sender instanceof BlockCommandSender)
      return ((BlockCommandSender) sender).getBlock().getLocation();
    return null;
  }

  @Nullable
  public static Player getNearestPlayer(@Nonnull Location location) {
    if (location.getWorld() == null) return null;
    Player currentPlayer = null;
    double playersDistance = -1;

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (location.getWorld() != player.getWorld()) continue;
      double distance = location.distance(player.getLocation());
      if (playersDistance == -1 || distance < playersDistance) {
        currentPlayer = player;
        playersDistance = distance;
      }
    }

    return currentPlayer;
  }

}
