package net.codingarea.challenges.plugin.challenges.implementation.challenge.movement;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class OnlyDownChallenge extends Setting {

  public OnlyDownChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.MOVEMENT, new ItemStack(Material.ACACIA_SLAB), "only-down");
  }

  @EventHandler
  public void onPlayerMove(@NotNull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getTo() == null) return;
    if (event.getTo().getBlockY() <= event.getFrom().getBlockY()) return;
    getChallengeMessageKey("failed").broadcast(Prefix.CHALLENGES, NameHelper.getName(event.getPlayer()));
    ChallengeHelper.kill(event.getPlayer());
  }

}
