package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TopCommandSetting extends Setting implements PlayerCommand {

  public TopCommandSetting() {
    super(MenuType.SETTINGS);
  }

  @Override
  public void onCommand(@Nonnull Player player, @Nonnull String[] args) {
    if (!isEnabled()) {
      Message.forName("feature-disabled").send(player, Prefix.CHALLENGES);
      SoundSample.BASS_OFF.play(player);
      return;
    }

    World world = player.getWorld();
    Environment environment = world.getEnvironment();
    Location playerLocation = player.getLocation();
    if (environment == Environment.NORMAL) {
      Message.forName("top-to-surface").send(player, Prefix.CHALLENGES);

      Location location = player.getWorld().getHighestBlockAt(playerLocation).getLocation().add(0.5, 1, 0.5);
      location.setYaw(playerLocation.getYaw());
      location.setPitch(playerLocation.getPitch());
      player.setFallDistance(0);
      player.teleport(location);

    } else {
      Message.forName("top-to-overworld").send(player, Prefix.CHALLENGES);

      if (environment == Environment.NETHER) {
        Location location = playerLocation.clone();
        world = ChallengeAPI.getGameWorld(Environment.NORMAL);
        location.setWorld(world);
        location.multiply(8);
        location = world.getHighestBlockAt(location).getLocation().add(0.5, 1, 0.5);
        player.setFallDistance(0);
        player.teleport(location);
      } else {
        world = ChallengeAPI.getGameWorld(Environment.NORMAL);
        Location location = player.getBedSpawnLocation();
        if (location == null) location = world.getSpawnLocation();
        player.setFallDistance(0);
        player.teleport(location);
      }

    }
    SoundSample.TELEPORT.play(player);

  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.MAGENTA_GLAZED_TERRACOTTA, Message.forName("top-command-setting"));
  }

}
