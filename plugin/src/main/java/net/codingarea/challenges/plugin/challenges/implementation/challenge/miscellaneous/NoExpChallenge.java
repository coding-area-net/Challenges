package net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;

public class NoExpChallenge extends Setting {

  public NoExpChallenge() {
    super(MenuType.CHALLENGES, null, new ItemStack(Material.EXPERIENCE_BOTTLE), "no-exp");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onExp(PlayerExpChangeEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getAmount() <= 0) return;
    getChallengeMessageKey("picked-up").broadcast(Prefix.CHALLENGES, event.getPlayer());
    ChallengeHelper.kill(event.getPlayer());
  }

}
