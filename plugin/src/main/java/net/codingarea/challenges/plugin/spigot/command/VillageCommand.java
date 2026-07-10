package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.StructureType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VillageCommand implements PlayerCommand {

  @Override
  public void onCommand(@NotNull Player player, @NotNull String[] args) throws Exception {
    player.setNoDamageTicks(10);
    MessageKey.of("command-village-search").send(player, Prefix.CHALLENGES);

    Bukkit.getScheduler().runTask(Challenges.getInstance(), () -> {

      Location village = player.getWorld().locateNearestStructure(player.getLocation(), StructureType.VILLAGE, 5000, true);
      if (village == null) {
        MessageKey.of("command-village-not-found").send(player, Prefix.CHALLENGES);
        return;
      }

      village = player.getWorld().getHighestBlockAt(village).getLocation().add(0.5, 1, 0.5);
      village.getChunk().load(true);

      Location finalVillage = village;
      Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), () -> {
        player.teleport(finalVillage);
        SoundSample.TELEPORT.play(player);
        MessageKey.of("command-village-teleport").send(player, Prefix.CHALLENGES);
      }, 20 /* run after 1 second to give the chunks/world time to load/generate */);

    });

  }

}
