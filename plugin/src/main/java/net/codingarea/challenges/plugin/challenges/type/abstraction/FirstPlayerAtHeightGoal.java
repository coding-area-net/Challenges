package net.codingarea.challenges.plugin.challenges.type.abstraction;

import lombok.Getter;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Getter
public abstract class FirstPlayerAtHeightGoal extends SettingGoal {

  private int heightToGetTo;

  public FirstPlayerAtHeightGoal(@Nullable SettingCategory category, @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    super(category, displayItemPreset, nameMessageKey);
  }

  @Override
  protected void onEnable() {
    bossbar.setContent((bar, player) -> {
      bar.setTitle(MessageKey.of("bossbar-first-at-height-goal"), getHeightToGetTo());
    });
    bossbar.show();
  }

  @Override
  protected void onDisable() {
    bossbar.hide();
  }

  @Override
  public void getWinnersOnEnd(@NotNull List<Player> winners) {
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onMove(PlayerMoveEvent event) {
    if (event.getTo() == null) return;
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getTo().getBlockY() == event.getFrom().getBlockY()) return;
    if (event.getTo().getBlockY() == heightToGetTo) {
      MessageKey.of("height-reached").broadcast(Prefix.CHALLENGES, event.getPlayer(), getHeightToGetTo());
      ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED, () -> Collections.singletonList(event.getPlayer()));
    }
  }

  @NotNull
  @Override
  public LocalizableMessage getChallengeDescription() {
    return super.getChallengeDescription().withArgs(heightToGetTo);
  }

  protected void setHeightToGetTo(int heightToGetTo) {
    this.heightToGetTo = heightToGetTo;
  }

}
