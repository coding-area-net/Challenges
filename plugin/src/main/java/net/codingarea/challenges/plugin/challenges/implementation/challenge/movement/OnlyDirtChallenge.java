package net.codingarea.challenges.plugin.challenges.implementation.challenge.movement;

import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.challenges.annotations.CanInstaKillOnEnable;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

@CanInstaKillOnEnable
public class OnlyDirtChallenge extends Setting {

  public OnlyDirtChallenge() {
    super(MenuType.CHALLENGES);
    setCategory(SettingCategory.MOVEMENT);
  }

  @Nonnull
  @Override
  public ItemStack getSettingsItem() {
    return super.getSettingsItem();
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.DIRT, Message.forName("item-only-dirt-challenge"));
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerMove(@Nonnull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getTo() == null) return;

    Block blockBelow = BlockUtils.getBlockBelow(event.getTo());
    if (blockBelow == null) return;
    if (blockBelow.getType() != Material.DIRT && !BukkitReflectionUtils.isAir(blockBelow.getType())) {
      Message.forName("only-dirt-failed").broadcast(Prefix.CHALLENGES, NameHelper.getName(event.getPlayer()));
      kill(event.getPlayer());
    }

  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockSpread(@Nonnull BlockSpreadEvent event) {
    if (!shouldExecuteEffect()) return;

    if (event.getNewState().getType() == Material.GRASS_BLOCK) {
      event.setCancelled(true);
    }

  }

}
