package net.codingarea.challenges.plugin.challenges.implementation.challenge.movement;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.CanInstaKillOnEnable;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import net.codingarea.commons.bukkit.utils.misc.BukkitReflectionUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@CanInstaKillOnEnable
public class OnlyDirtChallenge extends Setting {

  public OnlyDirtChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.MOVEMENT, new ItemStack(Material.DIRT), "only-dirt");
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerMove(@NotNull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getTo() == null) return;

    Block blockBelow = BlockUtils.getBlockBelow(event.getTo());
    if (blockBelow == null) return;
    if (blockBelow.getType() != Material.DIRT && !BukkitReflectionUtils.isAir(blockBelow.getType())) {
      getChallengeMessageKey("failed").broadcast(Prefix.CHALLENGES, event.getPlayer());
      ChallengeHelper.kill(event.getPlayer());
    }

  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockSpread(@NotNull BlockSpreadEvent event) {
    if (!shouldExecuteEffect()) return;

    if (event.getNewState().getType() == Material.GRASS_BLOCK) {
      event.setCancelled(true);
    }

  }

}
