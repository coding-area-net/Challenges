package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifierGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Since("2.2.0")
public class GetFullHealthGoal extends SettingModifierGoal {

  public GetFullHealthGoal() {
    super(MenuType.GOAL, SettingCategory.FASTEST_TIME, 1, 20, 20, new ItemStack(Material.AZURE_BLUET), "get-full-health-goal");
  }

  @Override
  protected void onEnable() {
    broadcastFiltered(player -> {
      player.setHealth(getValue());
    });
  }

  @Override
  protected void onValueChange() {
    broadcastFiltered(player -> {
      player.setHealth(getValue());
    });
  }

  @Override
  public void getWinnersOnEnd(@NotNull List<Player> winners) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (ignorePlayer(player)) continue;
      AttributeInstance attribute = player.getAttribute(MinecraftNameWrapper.MAX_HEALTH);
      if (attribute != null) {
        if (player.getHealth() >= attribute.getBaseValue()) {
          winners.add(player);
        }
      }
    }
  }

  @Nullable
  @Override
  public LocalizableMessage getSettingsDescription() {
    return MessageKey.of("item-heart-start-description").withArgs(getValue() / 2f);
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeHeartsValueChangeTitle(this);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onHealthChange(EntityRegainHealthEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer((Player) event.getEntity())) return;
    Bukkit.getScheduler().runTask(plugin, () -> {
      AttributeInstance attribute = ((Player) event.getEntity()).getAttribute(MinecraftNameWrapper.MAX_HEALTH);
      if (attribute != null && ((Player) event.getEntity()).getHealth() >= attribute.getBaseValue()) {
        ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED);
      }
    });
  }

}
