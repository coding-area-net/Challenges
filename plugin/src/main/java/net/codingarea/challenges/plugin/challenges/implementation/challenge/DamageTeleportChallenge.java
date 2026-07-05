package net.codingarea.challenges.plugin.challenges.implementation.challenge;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

@Since("2.0.2")
public class DamageTeleportChallenge extends SettingModifier {

  private static final int PLAYER = 1, EVERYONE = 2;

  public DamageTeleportChallenge() {
    super(MenuType.CHALLENGES, null, 1, 2, new ItemStack(Material.SHULKER_SHELL), "item-damage-teleport-challenge");
  }

//  @NotNull
//  @Override
//  public LegacyItemBuilder createSettingsItem() {
//    if (getValue() == 1) {
//      return DefaultItem.create(Material.ENDER_CHEST, Message.forName("everyone"));
//    } else {
//      return DefaultItem.create(Material.PLAYER_HEAD, Message.forName("player"));
//    }
//  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeValueTitle(this, getValue() == 0 ? Message.forName("everyone").asString() : Message.forName("player").asString());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(@NotNull EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    if (!shouldExecuteEffect()) return;
    if (ChallengeHelper.finalDamageIsNull(event)) return;

    handleDamage(((Player) event.getEntity()));
  }

  private void handleDamage(@NotNull Player player) {

    Location location = player.getWorld().getHighestBlockAt(getRandomLocation(player.getWorld())).getLocation();
    location.setY(location.getY() + 1);
    location.getChunk().load(true);

    if (getValue() == PLAYER) {
      player.teleport(location);
    } else {
      broadcastFiltered(player1 -> player1.teleport(location));
    }

  }

  public Location getRandomLocation(World world) {
    double size = world.getWorldBorder().getSize() / 2;
    size--;

    final double randomX = ThreadLocalRandom.current().nextDouble(-size, size);
    final double randomY = ThreadLocalRandom.current().nextDouble(-size, size);

    return world.getWorldBorder().getCenter().add(randomX, 0, randomY);
  }

}
