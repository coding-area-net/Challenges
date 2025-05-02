package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;

public class PregameMovementSetting extends Setting {

  public PregameMovementSetting() {
    super(MenuType.SETTINGS, true);
  }

  @EventHandler
  public void onMove(@Nonnull PlayerMoveEvent event) {
    if (ChallengeAPI.isStarted() || isEnabled()) return;
    if (event.getPlayer().getGameMode() == GameMode.SPECTATOR || event.getPlayer().getGameMode() == GameMode.CREATIVE)
      return;

    Location to = event.getTo();
    if (to == null) return;
    if (BlockUtils.isSameLocationIgnoreHeight(event.getFrom(), to)) return;

    Location target = event.getFrom().clone();
    target.setY(target.getBlockY());
    findNearestBlock(target);
    target.add(0, 1, 0);

    target.setPitch(to.getPitch());
    target.setYaw(to.getYaw());
    target.setDirection(to.getDirection());
    event.setTo(target);

    Message.forName("title-pregame-movement-setting").sendTitleInstant(event.getPlayer());
  }

  private void findNearestBlock(@Nonnull Location location) {
    for (; location.getBlockY() > 0 && location.getBlock().isPassable(); location.subtract(0, 1, 0))
      ;
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.PISTON, Message.forName("pregame-movement-setting"));
  }

}
