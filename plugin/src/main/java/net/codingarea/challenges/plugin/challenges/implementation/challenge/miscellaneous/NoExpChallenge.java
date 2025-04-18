package net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;

import javax.annotation.Nonnull;

public class NoExpChallenge extends Setting {

  public NoExpChallenge() {
    super(MenuType.CHALLENGES);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onExp(PlayerExpChangeEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getAmount() <= 0) return;
    Message.forName("exp-picked-up").broadcast(Prefix.CHALLENGES, NameHelper.getName(event.getPlayer()));
    ChallengeHelper.kill(event.getPlayer());
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.EXPERIENCE_BOTTLE, Message.forName("item-no-exp-challenge"));
  }

}
